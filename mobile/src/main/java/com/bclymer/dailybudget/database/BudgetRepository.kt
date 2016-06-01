package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.PrimaryKeyGenerator
import com.bclymer.dailybudget.utilities.Util
import java.util.*

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */
// TODO Dagger 2
internal object BudgetRepository : BaseRepository<Budget>(Budget::class) {

    fun updateCache(budget: Budget) {
        val today = Date()
        if (Util.isSameDay(today, budget.cachedDate)) return

        mainRealm.executeTransaction {
            val days = Util.getDaysBetweenDates(budget.cachedDate, today)
            budget.cachedValue += days * budget.amountPerPeriod
            val calendar = GregorianCalendar()
            calendar.time = budget.cachedDate
            for (i in 0..days - 1) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val transaction = TransactionRepository.createAllowance(calendar.time, budget.amountPerPeriod)
                transaction.budget = budget
                budget.transactions.add(transaction)
            }
            budget.cachedDate = today
        }
    }

    fun deleteBudget(budget: Budget) {
        mainRealm.executeTransaction {
            budget.deleteFromRealm()
        }
    }

    fun createBudget(): Budget {
        val budget = mainRealm.createObject(Budget::class.java)
        budget.id = PrimaryKeyGenerator.getId(Budget::class, mainRealm)
        budget.name = "New Budget"
        budget.amountPerPeriod = 10.0
        budget.periodLengthInDays = 1
        budget.cachedValue = 0.0
        budget.cachedDate = Date()
        return budget
    }
}