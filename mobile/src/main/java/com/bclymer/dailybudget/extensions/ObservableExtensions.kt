package com.bclymer.dailybudget.extensions

import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */
fun <T> Observable<T?>.filterOutNulls(): Observable<T> {
    return this.filter { it != null }.map { it!! }
}

fun <T, R> Observable<T>.takeIfNotNull(func: (T) -> R?): Observable<R> {
    val obs = this.map {
        func(it)
    }.filterOutNulls()
    return obs
}

fun <T> Observable<T>.observeOnMainThread(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnMainThread(onNext: ((T) -> Unit)? = null, onError: ((Throwable) -> Unit)? = null, onCompleted: (() -> Unit)? = null): Subscription {
    return observeOnMainThread().safeSubscribe(onNext, onError, onCompleted)
}

fun <T> Observable<T>.safeSubscribe(onNext: ((T) -> Unit)? = null, onError: ((Throwable) -> Unit)? = null, onCompleted: (() -> Unit)? = null, shouldReportError: (Throwable) -> Boolean = { true }): Subscription {
    return this.subscribe({ next ->
        if (onNext != null) {
            onNext(next)
        }
    }, { error ->
        if (onError != null) {
            onError(error)
        }
    }, {
        if (onCompleted != null) {
            onCompleted()
        }
    })
}

fun <T> Observable<T>.asVoid(): Observable<Unit> {
    return this.map { }
}