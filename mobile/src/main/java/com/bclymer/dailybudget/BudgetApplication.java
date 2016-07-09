package com.bclymer.dailybudget;

import android.app.Application;

import com.bclymer.dailybudget.database.DatabaseManager;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BudgetApplication extends Application {

    private static BudgetApplication instance;

    public static Application getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DatabaseManager.init(this);
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(BudgetUpdatedEvent event) {
        //Log.v(getClass().getSimpleName(), "Requesting Backup");
        //BackupManager.dataChanged(getPackageName());
    }

}
