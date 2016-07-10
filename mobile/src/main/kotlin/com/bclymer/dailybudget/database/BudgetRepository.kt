package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.BudgetStats
import com.bclymer.dailybudget.models.Transaction
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by Brian on 7/9/2016.
 */
object BudgetRepository : BaseRepository() { // TODO no really you need to do this one. Dagger 2 not static.

    private val budgetDao = DatabaseManager.getBaseDao<AsyncRuntimeExceptionDao<Budget, Int>, Budget, Int>(Budget::class.java, Int::class.java)

    fun getBudgets(): Observable<List<Budget>> {
        val obs = Observable.create<List<Budget>> {
            it.onStart()
            val budgets = budgetDao.queryForAll()
            it.onNext(budgets.toList())
            it.onCompleted()
        }
        return obs.subscribeOn(Schedulers.io())
    }

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

    fun getBudgetStats(budgetId: Int): Observable<BudgetStats> {
        return getBudget(budgetId)
                .map {
                    BudgetStats(it)
                }
                .subscribeOn(Schedulers.io())
    }

    fun createBudget(name: String, amountPerPeriod: Double, periodLengthInDays: Int): Observable<Budget> {
        val obs = Observable.create<Budget> {
            it.onStart()
            val budget = getPlaceholderBudget()
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

    fun getPlaceholderBudget(): Budget {
        val budget = Budget()
        budget.name = "New Budget"
        budget.amountPerPeriod = 10.0
        budget.periodLengthInDays = 1
        budget.cachedValue = 0.0
        budget.cachedDate = Date()
        budget.transactions = budgetDao.getEmptyForeignCollection(Budget.Columns.TRANSACTIONS)
        return budget
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
