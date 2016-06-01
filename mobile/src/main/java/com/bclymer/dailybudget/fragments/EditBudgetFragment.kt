package com.bclymer.dailybudget.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.database.TransactionRepository
import com.bclymer.dailybudget.extensions.filterOutNulls
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.Util
import com.travefy.travefy.core.bindView
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
class EditBudgetFragment : BaseFragment(R.layout.fragment_edit_budget) {

    private val mEditTextName: TextView by bindView(R.id.fragment_edit_budget_name)
    private val mEditTextDuration: EditText by bindView(R.id.fragment_edit_budget_edittext_duration)
    private val mEditTextAmount: EditText by bindView(R.id.fragment_edit_budget_edittext_amount)
    private val mButtonDelete: Button by bindView(R.id.fragment_edit_budget_button_delete)
    private val mButtonSave: Button by bindView(R.id.fragment_edit_budget_button_save)

    private var mBudgetId = NO_BUDGET_ID_VALUE
    private var mBudget: Budget by Delegates.notNull()

    private var mCallback: BudgetDoneEditingCallback? = null
    private var mNewBudget = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is BudgetDoneEditingCallback) {
            mCallback = context as BudgetDoneEditingCallback
        } else {
            throw RuntimeException("Activity $context must implement BudgetDoneEditingCallback to display EditBudgetFragment")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mBudgetId = arguments.getInt(EXTRA_BUDGET_ID, NO_BUDGET_ID_VALUE)
        }
        if (mBudgetId != NO_BUDGET_ID_VALUE) {
            BudgetRepository.monitorById(mBudgetId)
                    .filterOutNulls()
                    .subscribeOnLifecycle(onNext = {
                        mBudget = it
                        updateBudget()
                    })
        } else {
            mNewBudget = true
            mBudget = BudgetRepository.createBudget()
            updateBudget()
        }

        mButtonSave.setOnClickListener {
            saveChanges()
        }

        mButtonDelete.setOnClickListener {
            BudgetRepository.deleteBudget(mBudget)
            mCallback!!.onBudgetDoneEditing()
            Util.toast("Delete Successful")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!mNewBudget) {
            mButtonDelete.visibility = VISIBLE
        }
    }

    private fun updateBudget() {
        mEditTextName.text = mBudget.name
        mEditTextDuration.setText(Integer.toString(mBudget.periodLengthInDays))
        mEditTextAmount.setText(java.lang.Double.toString(mBudget.amountPerPeriod))
    }

    fun saveChanges() {
        mBudget.amountPerPeriod = java.lang.Double.valueOf(mEditTextAmount.text.toString())!!
        mBudget.periodLengthInDays = Integer.valueOf(mEditTextDuration.text.toString())!!
        mBudget.name = mEditTextName.text.toString()
        if (mNewBudget) {
            // TODO mBudget.create();
            val transaction = TransactionRepository.createAllowance(Date(), mBudget.amountPerPeriod)
            transaction.budget = mBudget
            mBudget.transactions.add(transaction)
            mBudget.cachedValue = mBudget.amountPerPeriod
        }
        /* TODO mBudget.updateAsync(new DatabaseOperationFinishedCallback() {
            @Override
            public void onDatabaseOperationFinished(int rows) {
                if (rows > 0) {
                    Util.toast("Save Successful");
                    mCallback.onBudgetDoneEditing();
                } else {
                    Util.toast("Save Failed");
                }
            }
        }); */
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