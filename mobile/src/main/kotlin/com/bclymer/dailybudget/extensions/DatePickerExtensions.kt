package com.bclymer.dailybudget.extensions

import android.widget.DatePicker
import java.util.*

/**
 * Created by brianclymer on 7/9/16.
 * Copyright Travefy, Inc.
 */

fun DatePicker.date(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}