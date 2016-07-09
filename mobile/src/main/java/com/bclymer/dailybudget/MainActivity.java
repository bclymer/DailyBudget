package com.bclymer.dailybudget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
                .add(R.id.main_activity_fragment_main, BudgetsFragment.Companion.newInstance())
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
                displayBudgetFragment(EditBudgetFragment.Companion.getNO_BUDGET_ID_VALUE());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayBudgetFragment(final int budgetId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_detail, EditBudgetFragment.Companion.newInstance(budgetId), EditBudgetFragment.Companion.getTAG())
                .commit();
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.openDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        }
    }

    @Override
    public void onBudgetSelected(final int budgetId) {
        displayBudgetFragment(budgetId);
    }

    @Override
    public void onBudgetDoneEditing() {
        final EditBudgetFragment fragment = (EditBudgetFragment) getFragmentManager().findFragmentByTag(EditBudgetFragment.Companion.getTAG());
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
