package com.bclymer.dailybudget.events;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetUpdatedEvent {
    public final int budgetId;
    public BudgetUpdatedEvent(int budgetId) {
        this.budgetId = budgetId;
    }
}
