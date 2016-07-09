package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Transaction

/**
 * Created by Brian on 7/9/2016.
 */
object TransactionRepository { // TODO no really you need to do this one. Dagger 2 not static.

    private val transactionDao = Transaction.getDao()

}
