package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.BudgetStats;
import com.bclymer.dailybudget.utilities.ThreadManager;
import com.bclymer.dailybudget.utilities.Util;

import java.util.Map;

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

        final Budget budget = Budget.getDao().queryForId(mBudgetId);

        getDialog().setTitle(budget.name);

        ThreadManager.runInBackground(new Runnable() {
            @Override
            public void run() {
                final BudgetStats stats = new BudgetStats(budget);
                ThreadManager.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewTotalAmount.setText("Total: " + Util.makeLikeMoney(stats.totalSpent));

                        mTextViewAmountPerDay.setText("Per Day: " + Util.makeLikeMoney(stats.getSpentPerDay()));

                        StringBuilder stringBuilder = new StringBuilder("Favorite Places\n");
                        for (Map.Entry<String, Double> entry : stats.places.entrySet()) {
                            stringBuilder.append(entry.getKey()).append(": ").append(Util.makeLikeMoney(entry.getValue())).append("\n");
                        }
                        mTextViewSortedPlaces.setText(stringBuilder.toString());
                    }
                });
            }
        });

    }
}
