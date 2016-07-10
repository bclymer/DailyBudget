package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Transaction
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Brian on 7/9/2016.
 */
object TransactionRepository { // TODO no really you need to do this one. Dagger 2 not static.

    private val transactionDao = DatabaseManager.getBaseDao<AsyncRuntimeExceptionDao<Transaction, Int>, Transaction, Int>(Transaction::class.java, Int::class.java)

    fun getBudgetTransactions(budgetId: Int): Observable<List<Transaction>> {
        val obs = Observable.create<List<Transaction>> {
            it.onStart()
            val transactions = transactionDao
                    .queryBuilder()
                    .orderBy(Transaction.Columns.DATE, false)
                    .where()
                    .eq(Transaction.Columns.FOREIGN_BUDGET, budgetId)
                    .query()
            it.onNext(transactions)
            it.onCompleted()
        }
        return obs.subscribeOn(Schedulers.io())
    }

}
