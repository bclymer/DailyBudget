package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by Brian on 7/9/2016.
 */
object BudgetRepository : BaseRepository() { // TODO no really you need to do this one. Dagger 2 not static.

    private val budgetDao = Budget.getDao()

    fun getBudget(budgetId: Int): Observable<Budget> {
        val obs = Observable.create<Budget> {
            it.onStart()
            val budget = budgetDao.queryForId(budgetId)
            if (budget != null) {
                it.onNext(budget)
                it.onCompleted()
            } else {
                it.onError(EntityNotFoundException(Budget::class.java, budgetId))
            }
        }
        return obs.subscribeOn(Schedulers.io())
    }

    fun createBudget(name: String, amountPerPeriod: Double, periodLengthInDays: Int): Observable<Budget> {
        val obs = Observable.create<Budget> {
            it.onStart()
            val budget = Budget.createBudget()
            budget.name = name
            budget.amountPerPeriod = amountPerPeriod
            budget.periodLengthInDays = periodLengthInDays

            val transaction = Transaction(Date(), amountPerPeriod)
            transaction.budget = budget
            budget.transactions.add(transaction)
            budget.cachedValue = amountPerPeriod

            budget.create()

            it.onNext(budget)
            it.onCompleted()
        }
        return obs.subscribeOn(Schedulers.io())
    }

    fun updateBudget(budgetId: Int, name: String, amountPerPeriod: Double, periodLengthInDays: Int): Observable<Budget> {
        val obs = getBudget(budgetId)
                .doOnNext {
                    it.name = name
                    it.amountPerPeriod = amountPerPeriod
                    it.periodLengthInDays = periodLengthInDays
                    it.update()
                }
        return obs.subscribeOn(Schedulers.io())
    }

    fun deleteBudget(budgetId: Int): Observable<Unit> {
        return getBudget(budgetId)
                .doOnNext {
                    it.delete()
                }
                .map { }
                .subscribeOn(Schedulers.io())
    }

}
