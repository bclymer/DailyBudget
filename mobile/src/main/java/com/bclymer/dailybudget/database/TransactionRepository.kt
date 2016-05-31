package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Transaction
import io.realm.Case
import io.realm.Realm
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
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->

        }
    }

    fun searchByLocation(query: String): List<Transaction> {
        return where { contains("location", query, Case.INSENSITIVE).findAll().toList() }
    }

}