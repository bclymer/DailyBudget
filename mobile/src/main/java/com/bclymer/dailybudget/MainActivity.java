package com.bclymer.dailybudget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bclymer.dailybudget.fragments.BudgetsFragment;
import com.bclymer.dailybudget.fragments.EditBudgetFragment;

import static com.bclymer.dailybudget.fragments.BudgetsFragment.BudgetSelectedCallback;
import static com.bclymer.dailybudget.fragments.EditBudgetFragment.*;


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
    public void onBudgetSelected(final int budgetId) {
        verifyAndShowBudgetFragment(budgetId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_budget:
                verifyAndShowNewBudgetFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyAndShowNewBudgetFragment() {
        verifyAndShowBudgetFragment(NO_BUDGET_ID_VALUE);
    }

    private void verifyAndShowBudgetFragment(final int budgetId) {
        final EditBudgetFragment fragment = (EditBudgetFragment) getFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment.hasUnsavedContent()) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("Your budget {0} has unsaved changes. Would you like to save them?")
                    .setNegativeButton("Cancel", null)
                    .setNeutralButton("Discard Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            displayBudgetFragment(budgetId);
                        }
                    })
                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragment.saveChanges();
                            displayBudgetFragment(budgetId);
                        }
                    })
                    .show();
        } else {
            verifyAndShowBudgetFragment(budgetId);
        }
    }

    private void displayBudgetFragment(final int budgetId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_detail, newInstance(budgetId), TAG)
                .commit();
    }
}
