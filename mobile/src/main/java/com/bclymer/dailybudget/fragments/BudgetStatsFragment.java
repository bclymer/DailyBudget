package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.TextBrew;
import com.bclymer.dailybudget.utilities.Util;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;

/**
 * Created by bclymer on 12/8/14.
 * Things I want from this class:
 * 1) A weekly, monthly, and overall list of places, sortable by:
 * a) number of visits
 * b) total money spent
 * c) alphabetical
 * d) average spent per visit
 * 2) A graph of money spent per day, week, and month.
 * 3) Be able to select any place and bring up a list of only those transactions.
 */
public class BudgetStatsFragment extends BaseDialogFragment {

    public static final String TAG = "BudgetStatsFragment";

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    @Bind(R.id.fragment_budget_stats_textview_totalamount)
    protected TextView mTextViewTotalAmount;
    @Bind(R.id.fragment_budget_stats_textview_amountperday)
    protected TextView mTextViewAmountPerDay;
    @Bind(R.id.fragment_budget_stats_textview_sortedplaces)
    protected TextView mTextViewSortedPlaces;

    private int mBudgetId;

    public BudgetStatsFragment() {
    }

    public static BudgetStatsFragment newInstance(int budgetId) {
        BudgetStatsFragment fragment = new BudgetStatsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_budget_stats;
        mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Budget budget = Budget.getDao().queryForId(mBudgetId);

        getDialog().setTitle(budget.name);

        List<Transaction> transactions = budget.getSortedTransactions();
        if (transactions.size() == 0) {
            Util.toast("No Transactions");
            return;
        }

        Transaction firstTransaction = transactions.get(transactions.size() - 1);
        Calendar firstDate = new GregorianCalendar();
        firstDate.setTime(firstTransaction.date);
        Util.setCalendarToBeginningOfDay(firstDate);
        double totalSpent = 0;

        Map<String, Double> places = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.location == null || transaction.location.trim().length() == 0 || transaction.getTotalAmount() > 0) {
                continue;
            }

            totalSpent -= transaction.getTotalAmount();
            boolean foundMatch = false;
            for (Map.Entry<String, Double> entry : places.entrySet()) {
                if (TextBrew.compareAndGiveBestScore(entry.getKey(), transaction.location) > 0.9) {
                    entry.setValue(entry.getValue() - transaction.getTotalAmount());
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                places.put(transaction.location, -transaction.getTotalAmount());
            }
        }
        mTextViewTotalAmount.setText("Total: " + Util.makeLikeMoney(totalSpent));

        // + 1 because today counts.
        long daysSinceFirstTransaction = Util.getDaysBetweenDates(firstTransaction.date, new Date()) + 1;
        mTextViewAmountPerDay.setText("Per Day: " + Util.makeLikeMoney(totalSpent / daysSinceFirstTransaction));

        places = sortByValue(places);

        StringBuilder stringBuilder = new StringBuilder("Favorite Places\n");
        for (Map.Entry<String, Double> entry : places.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(Util.makeLikeMoney(entry.getValue())).append("\n");
        }
        mTextViewSortedPlaces.setText(stringBuilder.toString());
    }

    public Map<String, Double> sortByValue(Map<String, Double> unsortedMap) {
        Map<String, Double> sortedMap = new TreeMap<>(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    static class ValueComparator implements Comparator<String> {

        final Map<String, Double> map;

        public ValueComparator(Map<String, Double> map) {
            this.map = map;
        }

        public int compare(String keyA, String keyB) {
            Double valueA = map.get(keyA);
            Comparable<Double> valueB = map.get(keyB);
            return valueB.compareTo(valueA);
        }
    }
}
