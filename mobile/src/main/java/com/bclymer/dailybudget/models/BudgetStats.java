package com.bclymer.dailybudget.models;

import android.util.Log;

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

/**
 * Created by brianclymer on 9/7/15.
 */
public class BudgetStats {

    private static final String TAG = BudgetStats.class.getSimpleName();

    public double totalSpent;
    public double totalAllowance;
    public long totalDays;
    public Map<String, Double> places = new HashMap<>();

    private Budget budget;

    public BudgetStats(Budget budget) {
        this.budget = budget;

        List<Transaction> transactions = budget.getSortedTransactions();
        if (transactions.size() == 0) {
            Util.toast("No Transactions");
            return;
        }

        Transaction firstTransaction = transactions.get(transactions.size() - 1);
        Calendar firstDate = new GregorianCalendar();
        firstDate.setTime(firstTransaction.date);
        Util.setCalendarToBeginningOfDay(firstDate);
        totalDays = Util.getDaysBetweenDates(firstTransaction.date, new Date()) + 1;

        for (Transaction transaction : transactions) {
            if (transaction.location == null || transaction.location.trim().length() == 0 || transaction.getTotalAmount() > 0) {
                if (transaction.getTotalAmount() > 0) {
                    totalAllowance += transaction.getTotalAmount();
                }
                continue;
            }

            totalSpent -= transaction.getTotalAmount();
            boolean foundMatch = false;
            for (Map.Entry<String, Double> entry : places.entrySet()) {
                double ranking = TextBrew.compareAndGiveBestScore(entry.getKey().toLowerCase(), transaction.location.toLowerCase());
                if (ranking > 0.8) {
                    if (ranking < 1) {
                        Log.v(TAG, "Matched " + transaction.location + " to " + entry.getKey() + " at " + ranking);
                    }
                    entry.setValue(entry.getValue() - transaction.getTotalAmount());
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                places.put(transaction.location, -transaction.getTotalAmount());
            }
        }

        places = sortByValue(places);
    }

    public double getSpentPerDay() {
        return totalSpent / totalDays;
    }

    public double getCurrentAllowance() {
        return budget.cachedValue;
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
