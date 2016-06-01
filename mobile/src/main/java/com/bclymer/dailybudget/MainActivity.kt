package com.bclymer.dailybudget

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import com.bclymer.dailybudget.fragments.BudgetsFragment
import com.bclymer.dailybudget.fragments.BudgetsFragment.BudgetSelectedCallback
import com.bclymer.dailybudget.fragments.EditBudgetFragment
import com.bclymer.dailybudget.fragments.EditBudgetFragment.BudgetDoneEditingCallback
import com.travefy.travefy.core.bindView

class MainActivity : Activity(), BudgetSelectedCallback, BudgetDoneEditingCallback {

    private val mDrawerLayout: DrawerLayout by bindView(R.id.activity_main_drawerlayout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.beginTransaction()
                .add(R.id.main_activity_fragment_main, BudgetsFragment.newInstance())
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_budget -> {
                displayBudgetFragment(EditBudgetFragment.NO_BUDGET_ID_VALUE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayBudgetFragment(budgetId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity_fragment_detail, EditBudgetFragment.newInstance(budgetId), EditBudgetFragment.TAG)
                .commit()
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.openDrawer(GravityCompat.END)
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END)
        }
    }

    override fun onBudgetSelected(budgetId: Int) {
        displayBudgetFragment(budgetId)
    }

    override fun onBudgetDoneEditing() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END)
        } else if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
