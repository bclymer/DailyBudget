package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.extensions.monitorSortedAsync
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import io.realm.Case
import io.realm.Sort
import rx.Observable
import java.util.*

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

internal object TransactionRepository : BaseRepository<Transaction>(Transaction::class) {

    fun updateTransaction(id: Int, amountMe: Double, amountOther: Double, date: Date, isSplit: Boolean, location: String?) {
        mainRealm.executeTransaction { realm ->

        }
    }

    fun monitorTransactions(budgetId: Int): Observable<List<Transaction>> {
        return where { equalTo("budget.id", budgetId).monitorSortedAsync("date", Sort.DESCENDING) }
    }

    fun searchByLocation(query: String): List<Transaction> {
        return where { contains("location", query, Case.INSENSITIVE).findAll().toList() }
    }

    fun addAllowanceTransaction(budget: Budget, date: Date) {
        mainRealm.executeTransaction { realm ->
            val transaction = realm.createObject(Transaction::class.java)
            transaction.date = date
            transaction.amount = budget.amountPerPeriod
            transaction.budget = budget
            budget.transactions.add(transaction)
        }
    }

    fun addTransaction(budget: Budget, amountMe: Double, amountOther: Double, date: Date, isSplit: Boolean, location: String?) {

    }

}