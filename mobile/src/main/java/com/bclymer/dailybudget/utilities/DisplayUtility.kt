package com.bclymer.dailybudget.utilities

import com.bclymer.dailybudget.BudgetApplication

/**
 * Created by Brian on 10/7/2014.
 */
object DisplayUtility {

    fun dpToPixels(dp: Int): Int {
        return (BudgetApplication.instance.resources.displayMetrics.density * dp.toFloat() + 0.5f).toInt()
    }

}
