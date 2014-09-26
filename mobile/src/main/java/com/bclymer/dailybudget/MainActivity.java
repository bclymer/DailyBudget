package com.bclymer.dailybudget;

import android.app.Activity;
import android.os.Bundle;

import static com.bclymer.dailybudget.BudgetsFragment.BudgetSelectedCallback;


public class MainActivity extends Activity implements BudgetSelectedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .add(R.id.main_activity_fragment_main, BudgetsFragment.newInstance())
                .commit();
    }

    @Override
    public void onBudgetSelected(int budgetId) {
        // TODO: If there is a fragment already there, check if user wants to save.
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_detail, EditBudgetFragment.newInstance(budgetId))
                .commit();
    }
}
