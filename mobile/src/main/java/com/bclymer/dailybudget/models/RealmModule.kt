package com.bclymer.dailybudget.models

import android.content.Context
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.annotations.RealmModule

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

@RealmModule(classes = arrayOf(
        Budget::class,
        Transaction::class
))
class RealmModule {

}

object DatabaseManager2 {

    // Realm has in `inMemory` option for unit tests.
    fun setup(context: Context) {
        val realmConfiguration = RealmConfiguration.Builder(context)
                .schemaVersion(1)
                .migration(Migration())
                .modules(com.bclymer.dailybudget.models.RealmModule())
                .initialData { realm ->
                    // fill initial data

                    val budgets = Budget.getDao().queryForAll()
                    budgets.forEach {
                        val budget = realm.createObject(Budget::class.java)
                        budget.id = it.id
                        budget.amountPerPeriod = it.amountPerPeriod
                        budget.cachedDate = it.cachedDate
                        budget.cachedValue = it.cachedValue
                        budget.name = it.name
                        budget.periodLengthInDays = it.periodLengthInDays
                    }

                    val transactions = Transaction.getDao().queryForAll()
                    transactions.forEach {
                        val transaction = realm.createObject(Transaction::class.java)
                        transaction.id = it.id
                        transaction.amount = it.amount
                        transaction.amountOther = it.amountOther
                        transaction.date = it.date
                        transaction.location = it.location
                        transaction.paidForSomeone = it.paidForSomeone
                        transaction.budget = realm.where(Budget::class.java).equalTo("id", it.budget.id).findFirst()
                        transaction.budget.transactions.add(transaction)
                    }
                }
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        Realm.getDefaultInstance()
    }

}

private class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        if (realm == null) {
            return // uhhh?
        }

    }
}