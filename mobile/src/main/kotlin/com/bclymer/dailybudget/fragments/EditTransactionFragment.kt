package com.bclymer.dailybudget.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.database.TransactionRepository
import com.bclymer.dailybudget.extensions.date
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.utilities.Util
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/28/2014.
 */
class EditTransactionFragment() : BaseDialogFragment(R.layout.fragment_edit_transaction) {

    private val mEditTextAmount: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount)
    private val mEditTextAmountOther: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount_other)
    private val mEditTextNotes: AutoCompleteTextView by bindView(R.id.fragment_edit_transaction_edittext_notes)
    private val mDatePicker: DatePicker by bindView(R.id.fragment_edit_transaction_datepicker_date)
    private val mButtonSave: Button by bindView(R.id.fragment_edit_transaction_button_add_transaction)
    private val mButtonDelete: Button by bindView(R.id.fragment_edit_transaction_button_delete_transaction)
    private val mButtonSplit: Button by bindView(R.id.fragment_edit_transaction_button_split)
    private val mLayoutOther: ViewGroup by bindView(R.id.fragment_edit_transaction_layout_other)

    private var mTransactionId: Int by Delegates.notNull()
    private var mBudgetId: Int by Delegates.notNull()
    private var mEditingTransaction = false
    private var isSplit = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
        val transactionId = arguments.getInt(EXTRA_TRANSACTION_ID, -1)

        BudgetRepository.getBudget(mBudgetId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    dialog.setTitle(it.name)
                }
                .flatMap {
                    val transaction: Transaction
                    if (transactionId == -1) {
                        transaction = Transaction()
                        transaction.budget = it
                        Observable.just(transaction)
                    } else {
                        mEditingTransaction = true
                        TransactionRepository.getTransaction(transactionId)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { transaction ->
                    mTransactionId = transaction.id
                    val cal = Calendar.getInstance()
                    if (mEditingTransaction) {
                        cal.time = transaction.date
                        mEditTextAmount.setText(java.lang.Double.toString(-1 * transaction.amount))
                        mEditTextAmountOther.setText(java.lang.Double.toString(-1 * transaction.amountOther))
                        mLayoutOther.visibility = if (transaction.paidForSomeone) VISIBLE else GONE
                        mEditTextNotes.setText(transaction.location)
                        isSplit = transaction.paidForSomeone
                        mButtonSplit.text = if (isSplit) "Merge" else "Split"
                        mButtonSave.setText(R.string.update_transaction)
                        mButtonDelete.visibility = VISIBLE
                    }
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH)
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    mDatePicker.init(year, month, day, null)
                }
                .subscribe()

        mEditTextNotes.requestFocus()
        // show keyboard
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // TODO should be RxBindings observable, debounced a bit.
        mEditTextNotes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                textChanged(s.toString())
            }
        })

        mButtonSave.setOnClickListener {
            addTransaction()
        }

        mButtonDelete.setOnClickListener {
            deleteTransaction()
        }

        mButtonSplit.setOnClickListener {
            splitEvenly()
        }
    }

    private fun textChanged(query: String) {
        TransactionRepository.searchTransactionLocations(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, it)
                    mEditTextNotes.setAdapter(adapter)
                    mEditTextNotes.showDropDown()
                }
    }

    private fun addTransaction() {
        val amount = -1 * valueFromEditText(mEditTextAmount)
        val amountOther = -1 * valueFromEditText(mEditTextAmountOther)
        val location = mEditTextNotes.text.toString()
        val date = mDatePicker.date()
        if (mEditingTransaction) {
            TransactionRepository.updateTransaction(mTransactionId, amount, amountOther, date, location)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Util.toast("Transaction Saved")
                        dismissAllowingStateLoss()
                    }
        } else {
            TransactionRepository.createTransaction(mBudgetId, amount, amountOther, date, location)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Util.toast("Transaction Saved")
                        dismissAllowingStateLoss()
                    }
        }
    }

    private fun deleteTransaction() {
        TransactionRepository.deleteTransaction(mTransactionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Util.toast("Transaction Deleted")
                    dismissAllowingStateLoss()
                }, {
                    Util.toast("Delete Failed")
                })
    }

    private fun splitEvenly() {
        if (isSplit) {
            val currentValue = valueFromEditText(mEditTextAmount)
            val otherValue = valueFromEditText(mEditTextAmountOther)
            val sum = Math.round((currentValue + otherValue) * 100).toDouble() / 100.0
            mEditTextAmount.setText(java.lang.Double.toString(sum))
            mEditTextAmountOther.text = null
        } else {
            val currentValue = valueFromEditText(mEditTextAmount)
            val myValue = Math.ceil(currentValue * 50) / 100
            val herValue = Math.floor(currentValue * 50) / 100
            mEditTextAmount.setText(java.lang.Double.toString(myValue))
            mEditTextAmountOther.setText(java.lang.Double.toString(herValue))
        }
        isSplit = !isSplit
        mLayoutOther.visibility = if (isSplit) VISIBLE else GONE
        mButtonSplit.text = if (isSplit) "Merge" else "Split"
    }

    private fun valueFromEditText(editText: EditText): Double {
        try {
            return java.lang.Double.valueOf(editText.text.toString())!!
        } catch (e: Exception) {
            return 0.0
        }

    }

    companion object {

        val TAG = "AddTransactionFragment"
        val NO_TRANSACTION_ID_VALUE = -1

        private val EXTRA_BUDGET_ID = "extra_budget_id"
        private val EXTRA_TRANSACTION_ID = "extra_transaction_id"

        @JvmOverloads fun newInstance(budgetId: Int, transactionId: Int = NO_TRANSACTION_ID_VALUE): EditTransactionFragment {
            val fragment = EditTransactionFragment()
            val bundle = Bundle(2)
            bundle.putInt(EXTRA_BUDGET_ID, budgetId)
            bundle.putInt(EXTRA_TRANSACTION_ID, transactionId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
