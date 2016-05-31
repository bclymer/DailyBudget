package com.bclymer.dailybudget.database

import com.bclymer.dailybudget.extensions.*
import com.bclymer.dailybudget.utilities.failOnBackgroundThread
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import rx.Observable
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

internal abstract class BaseRepository<T>(private val clazz: KClass<T>) where T : RealmObject {

    companion object MainRealm {
        val mainRealm by lazy {
            val realm = Realm.getDefaultInstance()
            realm.isAutoRefresh = true
            realm
        }
    }

    fun monitorAll(): Observable<List<T>> {
        return where { monitorSortedAsync("id") }
    }

    fun getAll(): List<T> {
        return where { findAll().toList() }
    }

    inline fun <T> loadDb(func: Realm.() -> T): T {
        failOnBackgroundThread("Load db uses the main realm, and can only be called on the main thread")
        return mainRealm.func()
    }

    inline fun <R> where(func: RealmQuery<T>.() -> R): R {
        return loadDb { where().func() }
    }

    fun monitorById(id: Int): Observable<T> {
        return where {
            equalTo("id", id)
                    .monitorSingleAsync()
                    .filterOutNulls()
        }
    }

    fun getById(id: Int): T? {
        return where { equalTo("id", id).findFirst() }
    }

    fun Realm.where(): RealmQuery<T> {
        return where(clazz.java)
    }

    fun Realm.findById(id: Long, forceAllowMainThreadAccess: Boolean = false): T? {
        return findById(clazz, id, forceAllowMainThreadAccess = forceAllowMainThreadAccess)
    }

    fun Realm.findByIdAsync(id: Long): Observable<T?> {
        return findByIdAsync(clazz, id)
    }

    fun Realm.waitById(id: Long): Observable<T> {
        return waitById(clazz, id)
    }


}