package com.bclymer.dailybudget.events;

import com.bclymer.dailybudget.models.Budget;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetUpdatedEvent {
    public final Budget budget;
    public BudgetUpdatedEvent(Budget budget) {
        this.budget = budget;
    }
}
