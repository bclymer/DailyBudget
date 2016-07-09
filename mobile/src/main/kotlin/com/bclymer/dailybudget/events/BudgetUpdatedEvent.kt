package com.bclymer.dailybudget.events

import com.bclymer.dailybudget.models.Budget

/**
 * Created by bclymer on 9/28/2014.
 */
class BudgetUpdatedEvent(val budget: Budget, val deleted: Boolean = false)
