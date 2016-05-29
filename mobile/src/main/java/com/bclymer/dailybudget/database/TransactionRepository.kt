package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.models.Transaction

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

internal object TransactionRepository : BaseRepository<Transaction>(Transaction::class) {


}