package com.bclymer.dailybudget.utilities

import android.widget.Toast
import com.bclymer.dailybudget.BudgetApplication
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by bclymer on 9/28/2014.
 */
object Util {

    @JvmOverloads fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        ThreadManager.runOnUi { Toast.makeText(BudgetApplication.application, text, duration).show() }
    }

    @JvmOverloads fun toast(recId: Int, duration: Int = Toast.LENGTH_SHORT) {
        val text = BudgetApplication.application.resources.getString(recId)
        toast(text, duration)
    }

    fun isSameDay(day1: Date, day2: Date): Boolean {
        val day1Cal = GregorianCalendar()
        day1Cal.time = day1
        val day2Cal = GregorianCalendar()
        day2Cal.time = day2
        return day1Cal.get(Calendar.YEAR) == day2Cal.get(Calendar.YEAR) && day1Cal.get(Calendar.DAY_OF_YEAR) == day2Cal.get(Calendar.DAY_OF_YEAR)
    }

    fun getDaysBetweenDates(startDate: Date?, endDate: Date?): Long {
        if (startDate == null || endDate == null) {
            return java.lang.Long.MIN_VALUE
        }
        val startCal = GregorianCalendar()
        startCal.time = startDate
        val endCal = GregorianCalendar()
        endCal.time = endDate
        return getDaysBetweenDates(startCal, endCal)
    }

    fun getDaysBetweenDates(startCal: Calendar?, endCal: Calendar?): Long {
        if (startCal == null || endCal == null) {
            return java.lang.Long.MIN_VALUE
        }
        setCalendarToBeginningOfDay(startCal)
        setCalendarToBeginningOfDay(endCal)
        endCal.set(Calendar.MINUTE, 1) // make sure it's 1 minute past, in case seconds make a difference.
        val diff = endCal.timeInMillis - startCal.timeInMillis
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun setCalendarToBeginningOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun makeLikeMoney(value: Double): String {
        return NumberFormat.getCurrencyInstance().format(value)
    }

}
