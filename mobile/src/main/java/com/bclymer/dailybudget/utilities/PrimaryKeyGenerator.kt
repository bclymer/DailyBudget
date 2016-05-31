package com.bclymer.dailybudget.utilities

import io.realm.Realm
import io.realm.RealmObject
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 5/31/16.
 * Copyright Travefy, Inc.
 */

internal object PrimaryKeyGenerator {

    private val classesMap: HashMap<KClass<*>, AtomicInteger> = hashMapOf()

    internal fun <T> getId(clazz: KClass<T>, realm: Realm): Int where T : RealmObject {
        synchronized(classesMap) {
            if (!classesMap.contains(clazz)) {
                val maxId = realm.where(clazz.java).max("id").toInt()
                classesMap[clazz] = AtomicInteger(maxId)
            }
            return classesMap[clazz]!!.incrementAndGet()
        }
    }

}