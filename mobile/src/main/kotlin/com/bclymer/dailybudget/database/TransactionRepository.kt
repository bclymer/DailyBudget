package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.models.Transaction
import de.greenrobot.event.EventBus
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by Brian on 7/9/2016.
 */
object TransactionRepository : BaseRepository() { // TODO no really you need to do this one. Dagger 2 not static.

    private val transactionDao = DatabaseManager.getBaseDao<AsyncRuntimeExceptionDao<Transaction, Int>, Transaction, Int>(Transaction::class.java, Int::class.java)

    fun getTransaction(transactionId: Int): Observable<Transaction> {
        val obs = Observable.create<Transaction> {
            it.onStart()
            val transaction = transactionDao.queryForId(transactionId)
            if (transaction != null) {
                it.onNext(transaction)
                it.onCompleted()
            } else {
                it.onError(EntityNotFoundException(Transaction::class.java, transactionId))
            }
        }
        return obs.subscribeOn(Schedulers.io())
    }

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


    fun createTransaction(budgetId: Int, amount: Double, amountOther: Double, date: Date, location: String?): Observable<Transaction> {
        return Observable.create<Transaction> {
            it.onStart()
            val transaction = Transaction()
            transaction.create()
            it.onNext(transaction)
            it.onCompleted()
        }.flatMap { transaction ->
            BudgetRepository.getBudget(budgetId).map {
                it to transaction
            }
        }.flatMap {
            val (budget, transaction) = it
            budget.transactions?.add(transaction)
            transaction.budget = budget
            budget.update()
            transaction.update()
            updateTransaction(transaction.id, amount, amountOther, date, location)
        }.subscribeOn(Schedulers.io())
    }

    fun updateTransaction(transactionId: Int, amount: Double, amountOther: Double, date: Date, location: String?): Observable<Transaction> {
        return getTransaction(transactionId)
                .flatMap { transaction ->
                    BudgetRepository.getBudget(transaction.budget!!.id).map {
                        it to transaction
                    }
                }
                .doOnNext {
                    val (budget, transaction) = it
                    budget.cachedDate = Date()
                    budget.cachedValue -= transaction.totalAmount
                    transaction.amount = amount
                    transaction.amountOther = amountOther
                    transaction.date = date
                    transaction.location = location
                    transaction.paidForSomeone = (amountOther > 0)
                    transaction.update()
                    budget.cachedValue += transaction.totalAmount
                    budget.update()
                }
                .map {
                    EventBus.getDefault().post(BudgetUpdatedEvent(it.first, false))
                    it.second
                }
                .subscribeOn(Schedulers.io())
    }


    fun deleteTransaction(transactionId: Int): Observable<Unit> {
        return getTransaction(transactionId)
                .flatMap { transaction ->
                    val budgetId = transaction.budget!!.id
                    BudgetRepository.getBudget(budgetId).map {
                        it to transaction
                    }
                }
                .map {
                    val (budget, transaction) = it
                    budget.transactions?.remove(transaction)
                    budget.cachedValue -= transaction.totalAmount
                    transaction.delete()
                    budget.update()
                    EventBus.getDefault().post(BudgetUpdatedEvent(budget, false))
                    Unit
                }
                .subscribeOn(Schedulers.io())
    }

    fun searchTransactionLocations(query: String): Observable<List<String>> {
        val obs = Observable.create<List<String>> {
            it.onStart()

            val trans = transactionDao.queryBuilder().selectColumns(
                    Transaction.Columns.LOCATION,
                    Transaction.Columns.AMOUNT,
                    Transaction.Columns.AMOUNT_OTHER).where().like(Transaction.Columns.LOCATION, "%$query%").query()

            val unique = HashMap<String, Double>()
            trans.filter { (it.location?.length ?: 0) > 0 }
                    .forEach {
                        if (unique.containsKey(it.location)) {
                            val oldValue = unique[it.location]!!
                            unique.put(it.location!!, oldValue + it.totalAmount)
                        } else {
                            unique.put(it.location!!, it.totalAmount)
                        }
                    }

            val sortedResults = unique.entries.sortedByDescending { it.value }.map { it.key }
            it.onNext(sortedResults)
            it.onCompleted()
        }
        return obs.subscribeOn(Schedulers.io())
    }

}
