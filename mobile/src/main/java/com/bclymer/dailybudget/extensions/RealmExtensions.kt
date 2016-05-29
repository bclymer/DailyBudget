package com.bclymer.dailybudget.extensions

import com.bclymer.dailybudget.utilities.failOnBackgroundThread
import com.bclymer.dailybudget.utilities.failOnMainThread
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.Sort
import rx.Observable
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

fun <T> Realm.findById(clazz: KClass<T>, id: Long, forceAllowMainThreadAccess: Boolean = false): T? where T : RealmObject {
    if (!forceAllowMainThreadAccess) {
        failOnMainThread("Sync queries are only allowed on background threads")
    }
    val obs = where(clazz.java).equalTo("id", id).findFirst()
    return obs
}

fun <T> Realm.findByIdAsync(clazz: KClass<T>, id: Long): Observable<T?> where T : RealmObject {
    failOnBackgroundThread("Async queries are only allowed on the main thread.")
    // TODO Async when it's not broken
    val obs = this.where(clazz.java).equalTo("id", id).findAll()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.firstOrNull() }
            .take(1)
    return obs
}

fun <T> Realm.waitById(clazz: KClass<T>, id: Long): Observable<T> where T : RealmObject {
    val obs = this.where(clazz.java).equalTo("id", id)
            .monitorAsync()
            .map { it.firstOrNull() }
            .filterOutNulls()
            .take(1)
    return obs
}

fun <T> RealmQuery<T>.monitorSingleAsync(): Observable<T?> where T : RealmObject {
    // TODO Async when it's not broken
    val obs = this.findAll()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.firstOrNull() }
    return obs
}

fun <T> RealmQuery<T>.monitorAsync(): Observable<List<T>> where T : RealmObject {
    val obs = monitorSortedAsync()
    return obs
}

fun <T> RealmQuery<T>.monitorSortedAsync(sort: String? = null, order: Sort = Sort.ASCENDING): Observable<List<T>> where T : RealmObject {
    if (sort != null) {
        val obs = this.findAllSorted(sort, order)
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList() }
        return obs
    } else {
        // TODO Async when it's not broken
        val obs = this.findAll()
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList() }
        return obs
    }
}

fun <T> RealmQuery<T>.monitorCount(): Observable<Int> where T : RealmObject {
    val obs = this.findAll()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.size }
    return obs
}