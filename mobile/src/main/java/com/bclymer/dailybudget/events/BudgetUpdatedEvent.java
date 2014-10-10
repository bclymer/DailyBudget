package com.bclymer.dailybudget.events;

import com.bclymer.dailybudget.models.Budget;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetUpdatedEvent {

    public final Budget budget;
    public final boolean deleted;

    public BudgetUpdatedEvent(Budget budget) {
        this(budget, false);
    }

    public BudgetUpdatedEvent(Budget budget, boolean deleted) {
        this.budget = budget;
        this.deleted = deleted;
    }
}
