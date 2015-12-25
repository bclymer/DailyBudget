package com.bclymer.dailybudget.utilities;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.util.Log;

import com.bclymer.dailybudget.database.DatabaseManager;

import java.io.File;

/**
 * Created by brianclymer on 12/25/15.
 * Copyright Travefy, Inc.
 */
public class BudgetBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
        Log.v(getClass().getSimpleName(), "Requesting backup of DB file");
        FileBackupHelper dbs = new FileBackupHelper(this, DatabaseManager.DATABASE_NAME);
        addHelper("dbs", dbs);
    }

    @Override
    public File getFilesDir() {
        File path = getDatabasePath(DatabaseManager.DATABASE_NAME);
        return path.getParentFile();
    }
}
