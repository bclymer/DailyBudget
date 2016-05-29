package com.bclymer.dailybudget

import android.app.Application
import com.bclymer.dailybudget.database.DatabaseManager
import com.bclymer.dailybudget.models.DatabaseManager2
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        DatabaseManager.init(this)
        DatabaseManager2.setup(this)
    }

    companion object {
        var instance: BudgetApplication by Delegates.notNull()
            private set
    }

}
