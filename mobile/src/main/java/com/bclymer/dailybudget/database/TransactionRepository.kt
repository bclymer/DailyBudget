package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.extensions.monitorAsync
import com.bclymer.dailybudget.models.Transaction
import io.realm.Case
import rx.Observable
import java.util.*

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

internal object TransactionRepository : BaseRepository<Transaction>(Transaction::class) {

    fun createAllowance(date: Date, allowance: Double): Transaction {
        val transaction = mainRealm.createObject(Transaction::class.java)
        transaction.date = date
        transaction.amount = allowance
        return transaction
    }

    fun updateTransaction(id: Int, amountMe: Double, amountOther: Double, date: Date, isSplit: Boolean, location: String?) {
        mainRealm.executeTransaction { realm ->

        }
    }

    fun monitorTransactions(budgetId: Int): Observable<List<Transaction>> {
        return where { equalTo("budget.id", budgetId).monitorAsync() }
    }

    fun searchByLocation(query: String): List<Transaction> {
        return where { contains("location", query, Case.INSENSITIVE).findAll().toList() }
    }

    fun delete(transaction: Transaction) {
        mainRealm.executeTransaction {
            transaction.deleteFromRealm()
        }
    }

}