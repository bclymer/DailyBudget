package com.bclymer.dailybudget.utilities

import android.os.Looper

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

fun failOnMainThread(msg: String) {
    if (Looper.getMainLooper().isCurrentThread) {
        throw IllegalStateException(msg)
    }
}

fun failOnBackgroundThread(msg: String) {
    if (!Looper.getMainLooper().isCurrentThread) {
        throw IllegalStateException(msg)
    }
}