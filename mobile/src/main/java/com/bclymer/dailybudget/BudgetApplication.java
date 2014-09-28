package com.bclymer.dailybudget;

import android.app.Application;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BudgetApplication extends Application {

    private static BudgetApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DatabaseHelper.init(this);
    }

    public static Application getApplication() {
        return instance;
    }

}
