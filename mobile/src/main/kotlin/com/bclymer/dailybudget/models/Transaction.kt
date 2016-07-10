package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao
import com.bclymer.dailybudget.database.DatabaseManager
import com.bclymer.dailybudget.database.DatabaseResource
import com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT
import com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT_OTHER
import com.bclymer.dailybudget.models.Transaction.Columns.DATE
import com.bclymer.dailybudget.models.Transaction.Columns.FOREIGN_BUDGET
import com.bclymer.dailybudget.models.Transaction.Columns.ID
import com.bclymer.dailybudget.models.Transaction.Columns.LOCATION
import com.bclymer.dailybudget.models.Transaction.Columns.PAID_FOR_SOMEONE
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
class Transaction : DatabaseResource<Transaction, Int> {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    var id: Int = 0
    @DatabaseField(columnName = DATE)
    var date: Date = Date(0)
    @DatabaseField(columnName = AMOUNT)
    var amount: Double = 0.toDouble()
    @DatabaseField(columnName = AMOUNT_OTHER)
    var amountOther: Double = 0.toDouble()
    @DatabaseField(columnName = LOCATION)
    var location: String? = null
    @DatabaseField(columnName = PAID_FOR_SOMEONE)
    var paidForSomeone: Boolean = false
    @DatabaseField(columnName = FOREIGN_BUDGET, foreign = true)
    var budget: Budget? = null

    constructor() {
    }

    constructor(date: Date, amount: Double) {
        this.date = date
        this.amount = amount
    }

    val totalAmount: Double
        get() = amount + amountOther

    object Columns {
        const val ID = "id"
        const val DATE = "date"
        const val AMOUNT = "amount"
        const val AMOUNT_OTHER = "amount_other"
        const val LOCATION = "notes"
        const val PAID_FOR_SOMEONE = "paid_for_someone"
        const val FOREIGN_BUDGET = "budget_id"
    }

    companion object {

        val dao: AsyncRuntimeExceptionDao<Transaction, Int>
            get() = DatabaseManager.getBaseDao<AsyncRuntimeExceptionDao<Transaction, Int>, Transaction, Int>(Transaction::class.java, Int::class.java)
    }

}
