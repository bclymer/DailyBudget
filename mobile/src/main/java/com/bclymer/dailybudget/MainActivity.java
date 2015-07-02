package com.bclymer.dailybudget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.bclymer.dailybudget.fragments.BudgetsFragment;
import com.bclymer.dailybudget.fragments.EditBudgetFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclymer.dailybudget.fragments.BudgetsFragment.BudgetSelectedCallback;
import static com.bclymer.dailybudget.fragments.EditBudgetFragment.BudgetDoneEditingCallback;


public class MainActivity extends Activity implements BudgetSelectedCallback, BudgetDoneEditingCallback {

    @Bind(R.id.activity_main_drawerlayout)
    protected DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getFragmentManager().beginTransaction()
                .add(R.id.main_activity_fragment_main, BudgetsFragment.newInstance())
                .commit();
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
        verifyAndShowBudgetFragment(EditBudgetFragment.NO_BUDGET_ID_VALUE);
    }

    private void verifyAndShowBudgetFragment(final int budgetId) {
        final EditBudgetFragment fragment = (EditBudgetFragment) getFragmentManager().findFragmentByTag(EditBudgetFragment.TAG);
        if (fragment != null && fragment.hasUnsavedContent()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.unsaved_changes))
                    .setMessage(getString(R.string.unsaved_changes_message).replace("{0}", fragment.getBudgetName()))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setNeutralButton(getString(R.string.discard_changes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            displayBudgetFragment(budgetId);
                        }
                    })
                    .setPositiveButton(getString(R.string.save_changes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragment.saveChanges();
                            displayBudgetFragment(budgetId);
                        }
                    })
                    .show();
        } else {
            displayBudgetFragment(budgetId);
        }
    }

    private void displayBudgetFragment(final int budgetId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_detail, EditBudgetFragment.newInstance(budgetId), EditBudgetFragment.TAG)
                .commit();
        if (!mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.openDrawer(Gravity.END);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
        }
    }

    @Override
    public void onBudgetSelected(final int budgetId) {
        verifyAndShowBudgetFragment(budgetId);
    }

    @Override
    public void onBudgetDoneEditing() {
        final EditBudgetFragment fragment = (EditBudgetFragment) getFragmentManager().findFragmentByTag(EditBudgetFragment.TAG);
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
            final EditBudgetFragment fragment = (EditBudgetFragment) getFragmentManager().findFragmentByTag(EditBudgetFragment.TAG);
            if (fragment != null && fragment.hasUnsavedContent()) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.unsaved_changes))
                        .setMessage(getString(R.string.unsaved_changes_message).replace("{0}", fragment.getBudgetName()))
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setNeutralButton(getString(R.string.discard_changes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDrawerLayout.closeDrawer(Gravity.END);
                            }
                        })
                        .setPositiveButton(getString(R.string.save_changes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.saveChanges();
                                mDrawerLayout.closeDrawer(Gravity.END);
                            }
                        })
                        .show();
            } else {
                mDrawerLayout.closeDrawer(Gravity.END);
            }
        } else {
            super.onBackPressed();
        }
    }
}
