package com.bclymer.dailybudget;

import android.app.Application;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BudgetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.init(this);
    }

}
