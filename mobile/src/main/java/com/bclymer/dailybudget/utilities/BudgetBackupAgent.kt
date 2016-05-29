package com.bclymer.dailybudget.utilities

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper
import android.util.Log

import com.bclymer.dailybudget.database.DatabaseManager

import java.io.File

/**
 * Created by brianclymer on 12/25/15.
 * Copyright Travefy, Inc.
 */
class BudgetBackupAgent : BackupAgentHelper() {

    override fun onCreate() {
        Log.v(javaClass.simpleName, "Requesting backup of DB file")
        val dbs = FileBackupHelper(this, DatabaseManager.DATABASE_NAME)
        addHelper("dbs", dbs)
    }

    override fun getFilesDir(): File {
        val path = getDatabasePath(DatabaseManager.DATABASE_NAME)
        return path.parentFile
    }
}
