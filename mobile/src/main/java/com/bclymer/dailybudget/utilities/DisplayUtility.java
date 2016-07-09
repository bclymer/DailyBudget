package com.bclymer.dailybudget.utilities;

import com.bclymer.dailybudget.BudgetApplication;

/**
 * Created by Brian on 10/7/2014.
 */
public class DisplayUtility {

    public static int dpToPixels(int dp) {
        return (int) (BudgetApplication.Companion.getApplication().getResources().getDisplayMetrics().density * (float) dp + 0.5f);
    }

}
