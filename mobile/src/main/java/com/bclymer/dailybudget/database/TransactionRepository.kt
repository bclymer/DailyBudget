package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Transaction
import io.realm.Realm
import java.util.*

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

internal object TransactionRepository : BaseRepository<Transaction>(Transaction::class) {

    fun updateTransaction(id: Int, amountMe: Double, amountOther: Double, date: Date, isSplit: Boolean, location: String?) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            
        }
    }

}