package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.database.DatabaseResource
import com.bclymer.dailybudget.models.Budget.Columns.AMOUNT_PER_PERIOD
import com.bclymer.dailybudget.models.Budget.Columns.CACHED_DATE
import com.bclymer.dailybudget.models.Budget.Columns.CACHED_VALUE
import com.bclymer.dailybudget.models.Budget.Columns.ID
import com.bclymer.dailybudget.models.Budget.Columns.PERIOD_LENGTH_IN_DAYS
import com.bclymer.dailybudget.models.Budget.Columns.TRANSACTIONS
import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
class Budget : DatabaseResource<Budget, Int>() {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    var id: Int = 0

    @DatabaseField(columnName = Columns.NAME)
    var name: String? = null

    @DatabaseField(columnName = AMOUNT_PER_PERIOD)
    var amountPerPeriod: Double = 0.toDouble()

    @DatabaseField(columnName = PERIOD_LENGTH_IN_DAYS)
    var periodLengthInDays: Int = 0

    @DatabaseField(columnName = CACHED_VALUE)
    var cachedValue: Double = 0.toDouble()

    @DatabaseField(columnName = CACHED_DATE)
    var cachedDate: Date = Date(0)

    @ForeignCollectionField(columnName = TRANSACTIONS)
    var transactions: ForeignCollection<Transaction>? = null

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val AMOUNT_PER_PERIOD = "amount_per_period"
        const val PERIOD_LENGTH_IN_DAYS = "period_length"
        const val CACHED_VALUE = "cached_value"
        const val CACHED_DATE = "cached_date"
        const val TRANSACTIONS = "transactions"
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val budget = o as Budget?

        return id == budget!!.id

    }

    override fun hashCode(): Int {
        return id
    }
}
