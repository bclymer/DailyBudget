package com.bclymer.dailybudget.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.Util
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by bclymer on 9/26/2014.
 */
class EditBudgetFragment() : BaseFragment() {

    private val mEditTextName: TextView by bindView(R.id.fragment_edit_budget_name)
    private val mEditTextDuration: EditText by bindView(R.id.fragment_edit_budget_edittext_duration)
    private val mEditTextAmount: EditText by bindView(R.id.fragment_edit_budget_edittext_amount)
    private val mButtonSave: Button by bindView(R.id.fragment_edit_budget_button_save)
    private val mButtonDelete: Button by bindView(R.id.fragment_edit_budget_button_delete)

    private var mBudgetId = NO_BUDGET_ID_VALUE

    private val mCallback: BudgetDoneEditingCallback by lazy { activity as BudgetDoneEditingCallback }
    private var mNewBudget = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity !is BudgetDoneEditingCallback) {
            throw RuntimeException("Activity $activity must implement BudgetDoneEditingCallback to display EditBudgetFragment")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutId = R.layout.fragment_edit_budget
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            mBudgetId = arguments.getInt(EXTRA_BUDGET_ID, NO_BUDGET_ID_VALUE)
        }
        if (mBudgetId != NO_BUDGET_ID_VALUE) {
            mButtonDelete.visibility = VISIBLE
            BudgetRepository.getBudget(mBudgetId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { budget -> configureWithBudget(budget) }
        } else {
            mNewBudget = true
            configureWithBudget(Budget.createBudget())
        }

        mButtonSave.setOnClickListener {
            saveChanges()
        }

        mButtonDelete.setOnClickListener {
            deleteChanges()
        }
    }

    private fun configureWithBudget(budget: Budget) {
        mEditTextName.text = budget.name
        mEditTextDuration.setText(Integer.toString(budget.periodLengthInDays))
        mEditTextAmount.setText(java.lang.Double.toString(budget.amountPerPeriod))
    }

    private fun deleteChanges() {
        // I eventually need to refactor the events system so I don't need all this jank.
        BudgetRepository.getBudget(mBudgetId)
                .flatMap { budget -> BudgetRepository.deleteBudget(mBudgetId).map { budget } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ budget ->
                    mCallback.onBudgetDoneEditing()
                    mEventBus.post(BudgetUpdatedEvent(budget, true))
                    Util.toast("Delete Successful")
                }) {
                    Util.toast("Delete Failed")
                }
    }

    private fun saveChanges() {
        val amountPerPeriod = java.lang.Double.valueOf(mEditTextAmount.text.toString())!!
        val periodLengthInDays = Integer.valueOf(mEditTextDuration.text.toString())!!
        val name = mEditTextName.text.toString()
        if (mNewBudget) {
            BudgetRepository.createBudget(name, amountPerPeriod, periodLengthInDays)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ budget ->
                        Util.toast("Save Successful")
                        mEventBus.post(BudgetUpdatedEvent(budget))
                        mCallback.onBudgetDoneEditing()
                    }) {
                        Util.toast("Save Failed")
                    }
        } else {
            BudgetRepository.updateBudget(mBudgetId, name, amountPerPeriod, periodLengthInDays)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ budget ->
                        Util.toast("Save Successful")
                        mEventBus.post(BudgetUpdatedEvent(budget))
                        mCallback.onBudgetDoneEditing()
                    }) {
                        Util.toast("Save Failed")
                    }
        }
    }

    interface BudgetDoneEditingCallback {
        fun onBudgetDoneEditing()
    }

    companion object {

        val TAG = "EditBudgetFragment"
        val NO_BUDGET_ID_VALUE = -1

        private val EXTRA_BUDGET_ID = "extra_budget_id"

        fun newInstance(budgetId: Int): EditBudgetFragment {
            val fragment = EditBudgetFragment()
            val bundle = Bundle(1)
            bundle.putInt(EXTRA_BUDGET_ID, budgetId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
