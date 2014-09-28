package com.bclymer.dailybudget;

import android.widget.Toast;

/**
 * Created by bclymer on 9/28/2014.
 */
public class Util {

    public static void toast(final String text, final int duration) {
        ThreadManager.runOnUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BudgetApplication.getApplication(), text, duration).show();
            }
        });
    }

    public static void toast(int recId, int duration) {
        String text = BudgetApplication.getApplication().getResources().getString(recId);
        toast(text, duration);
    }

    public static void toast(final String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    public static void toast(int recId) {
        String text = BudgetApplication.getApplication().getResources().getString(recId);
        toast(text, Toast.LENGTH_SHORT);
    }

}
