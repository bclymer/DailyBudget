package com.bclymer.dailybudget;

import android.app.Application;

import com.bclymer.dailybudget.database.DatabaseManager;

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
    }

}
