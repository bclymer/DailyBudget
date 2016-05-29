package com.bclymer.dailybudget

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife
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
        ButterKnife.bind(this)

        fragmentManager.beginTransaction().add(R.id.main_activity_fragment_main, BudgetsFragment.newInstance()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_budget -> {
                verifyAndShowNewBudgetFragment()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun verifyAndShowNewBudgetFragment() {
        verifyAndShowBudgetFragment(EditBudgetFragment.NO_BUDGET_ID_VALUE)
    }

    private fun verifyAndShowBudgetFragment(budgetId: Int) {
        val fragment = fragmentManager.findFragmentByTag(EditBudgetFragment.TAG) as? EditBudgetFragment
        if (fragment != null && fragment.hasUnsavedContent()) {
            AlertDialog.Builder(this).setTitle(getString(R.string.unsaved_changes)).setMessage(getString(R.string.unsaved_changes_message).replace("{0}", fragment.budgetName)).setNegativeButton(getString(R.string.cancel), null).setNeutralButton(getString(R.string.discard_changes)) { dialog, which -> displayBudgetFragment(budgetId) }.setPositiveButton(getString(R.string.save_changes)) { dialog, which ->
                fragment.saveChanges()
                displayBudgetFragment(budgetId)
            }.show()
        } else {
            displayBudgetFragment(budgetId)
        }
    }

    private fun displayBudgetFragment(budgetId: Int) {
        fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_detail, EditBudgetFragment.newInstance(budgetId), EditBudgetFragment.TAG).commit()
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.openDrawer(GravityCompat.END)
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END)
        }
    }

    override fun onBudgetSelected(budgetId: Int) {
        verifyAndShowBudgetFragment(budgetId)
    }

    override fun onBudgetDoneEditing() {
        val fragment = fragmentManager.findFragmentByTag(EditBudgetFragment.TAG) as? EditBudgetFragment
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            val fragment = fragmentManager.findFragmentByTag(EditBudgetFragment.TAG) as? EditBudgetFragment
            if (fragment != null && fragment.hasUnsavedContent()) {
                AlertDialog.Builder(this).setTitle(getString(R.string.unsaved_changes)).setMessage(getString(R.string.unsaved_changes_message).replace("{0}", fragment.budgetName)).setNegativeButton(getString(R.string.cancel), null).setNeutralButton(getString(R.string.discard_changes)) { dialog, which -> mDrawerLayout.closeDrawer(GravityCompat.END) }.setPositiveButton(getString(R.string.save_changes)) { dialog, which ->
                    fragment.saveChanges()
                    mDrawerLayout.closeDrawer(GravityCompat.END)
                }.show()
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.END)
            }
        } else {
            super.onBackPressed()
        }
    }
}
