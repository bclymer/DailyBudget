package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.utilities.Util
import java.util.*

/**
 * Created by brianclymer on 9/7/15.
 */
class BudgetStats(private val budget: Budget) {

    var totalSpent: Double = 0.toDouble()
    var totalAllowance: Double = 0.toDouble()
    var totalDays: Long = 0
    var places: MutableMap<String, Double> = HashMap()

    init {
        val transactions = budget.transactions.toList()
        if (transactions.size == 0) {
            Util.toast("No Transactions")
        } else {
            val firstTransaction = transactions[transactions.size - 1]
            val firstDate = GregorianCalendar()
            firstDate.time = firstTransaction.date
            Util.setCalendarToBeginningOfDay(firstDate)
            totalDays = Util.getDaysBetweenDates(firstTransaction.date, Date()) + 1

            for (transaction in transactions) {
                if (transaction.location == null || transaction.location.trim({ it <= ' ' }).length == 0 || transaction.totalAmount > 0) {
                    if (transaction.totalAmount > 0) {
                        totalAllowance += transaction.totalAmount
                    }
                    continue
                }

                totalSpent -= transaction.totalAmount
                val oldPlace = places[transaction.location]
                if (oldPlace != null) {
                    places.put(transaction.location, oldPlace - transaction.totalAmount)
                } else {
                    places.put(transaction.location, -transaction.totalAmount)
                }
            }

            places = sortByValue(places)
        }
    }

    val spentPerDay = totalSpent / totalDays

    fun sortByValue(unsortedMap: Map<String, Double>): MutableMap<String, Double> {
        val sortedMap = TreeMap<String, Double>(ValueComparator(unsortedMap))
        sortedMap.putAll(unsortedMap)
        return sortedMap
    }

    internal class ValueComparator(val map: Map<String, Double>) : Comparator<String> {

        override fun compare(keyA: String, keyB: String): Int {
            val valueA = map[keyA]!!
            val valueB = map[keyB]!!
            return valueB.compareTo(valueA)
        }
    }

    companion object {

        private val TAG = BudgetStats::class.java.simpleName
    }
}