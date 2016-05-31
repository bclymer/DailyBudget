package com.bclymer.dailybudget.models

import android.content.Context
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.annotations.RealmModule

/**
 * Created by brianclymer on 5/29/16.
 * Copyright Travefy, Inc.
 */

@RealmModule(classes = arrayOf(
        Budget::class,
        Transaction::class
))
class RealmModule {

}

object DatabaseManager {

    // Realm has in `inMemory` option for unit tests.
    fun setup(context: Context) {
        val realmConfiguration = RealmConfiguration.Builder(context)
                .schemaVersion(1)
                .migration(Migration())
                .modules(com.bclymer.dailybudget.models.RealmModule())
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        Realm.getDefaultInstance()
    }

}

private class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        if (realm == null) {
            return // uhhh?
        }

    }
}