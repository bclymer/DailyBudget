package com.bclymer.dailybudget

import android.app.Application
import com.bclymer.dailybudget.database.DatabaseManager
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        DatabaseManager.init(this)
    }

    companion object {

        private var instance: BudgetApplication by Delegates.notNull()

        val application: Application
            get() = instance
    }

}
