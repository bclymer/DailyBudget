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
import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.extensions.date
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.utilities.ThreadManager
import com.bclymer.dailybudget.utilities.Util
import java.sql.SQLException
import java.util.*

/**
 * Created by bclymer on 9/28/2014.
 */
class EditTransactionFragment() : BaseDialogFragment() {

    private val mEditTextAmount: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount)
    private val mEditTextAmountOther: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount_other)
    private val mEditTextNotes: AutoCompleteTextView by bindView(R.id.fragment_edit_transaction_edittext_notes)
    private val mDatePicker: DatePicker by bindView(R.id.fragment_edit_transaction_datepicker_date)
    private val mButtonSave: Button by bindView(R.id.fragment_edit_transaction_button_add_transaction)
    private val mButtonDelete: Button by bindView(R.id.fragment_edit_transaction_button_delete_transaction)
    private val mButtonSplit: Button by bindView(R.id.fragment_edit_transaction_button_split)
    private val mLayoutOther: ViewGroup by bindView(R.id.fragment_edit_transaction_layout_other)

    private var mBudget: Budget? = null
    private var mTransaction: Transaction? = null
    private var mEditingTransaction = false
    private var isSplit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutId = R.layout.fragment_edit_transaction

        val budgetId = arguments.getInt(EXTRA_BUDGET_ID)
        val transactionId = arguments.getInt(EXTRA_TRANSACTION_ID, -1)
        mBudget = Budget.getDao().queryForId(budgetId)
        if (transactionId == -1) {
            mTransaction = Transaction()
            mTransaction!!.budget = mBudget
        } else {
            mTransaction = Transaction.getDao().queryForId(transactionId)
            mEditingTransaction = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.setTitle(mBudget!!.name)
        val cal = Calendar.getInstance()
        if (mEditingTransaction) {
            cal.time = mTransaction!!.date
            mEditTextAmount.setText(java.lang.Double.toString(-1 * mTransaction!!.amount))
            mEditTextAmountOther.setText(java.lang.Double.toString(-1 * mTransaction!!.amountOther))
            mLayoutOther.visibility = if (mTransaction!!.paidForSomeone) VISIBLE else GONE
            mEditTextNotes.setText(mTransaction!!.location)
            isSplit = mTransaction!!.paidForSomeone
            mButtonSplit.text = if (isSplit) "Merge" else "Split"
            mButtonSave.setText(R.string.update_transaction)
            mButtonDelete.visibility = VISIBLE
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        mDatePicker.init(year, month, day, null)
        mEditTextNotes.requestFocus()
        // show keyboard
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

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
        try {
            val trans = Transaction.getDao().queryBuilder().selectColumns(
                    Transaction.Columns.LOCATION,
                    Transaction.Columns.AMOUNT,
                    Transaction.Columns.AMOUNT_OTHER).where().like(Transaction.Columns.LOCATION, "%$query%").query()

            val unique = HashMap<String, Double>()
            trans.filter { (it.location?.length ?: 0) > 0 }
                    .forEach {
                        if (unique.containsKey(it.location)) {
                            val oldValue = unique[it.location]!!
                            unique.put(it.location, oldValue + it.totalAmount)
                        } else {
                            unique.put(it.location, it.totalAmount)
                        }
                    }

            val list = ArrayList(unique.entries)
            Collections.sort(list) { lhs, rhs ->
                if (lhs.value < rhs.value) {
                    -1
                } else if (lhs.value > rhs.value) {
                    1
                } else {
                    0
                }
            }
            val finalUgh = ArrayList<String>(list.map { it.key })
            val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, finalUgh)
            mEditTextNotes.setAdapter(adapter)
            mEditTextNotes.showDropDown()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun addTransaction() {
        ThreadManager.runInBackgroundThenUi({
            if (mEditingTransaction) {
                mBudget!!.cachedValue -= mTransaction!!.totalAmount // this will be undone later.
            }
            mTransaction!!.paidForSomeone = isSplit
            Date()
            mTransaction!!.date = mDatePicker.date()

            mTransaction!!.amount = -1 * java.lang.Double.valueOf(mEditTextAmount.text.toString())!!
            if (mTransaction!!.paidForSomeone) {
                mTransaction!!.amountOther = -1 * java.lang.Double.valueOf(mEditTextAmountOther.text.toString())!!
            } else {
                mTransaction!!.amountOther = 0.0
            }
            mTransaction!!.location = mEditTextNotes.text.toString()
            if (mEditingTransaction) {
                mTransaction!!.update()
            } else {
                mBudget!!.transactions.add(mTransaction)
            }
            mBudget!!.cachedValue += mTransaction!!.totalAmount
            mBudget!!.cachedDate = Date()
            mBudget!!.update()
            mEventBus.post(BudgetUpdatedEvent(mBudget!!, false))
        }) {
            Util.toast("Transaction Saved")
            dismissAllowingStateLoss()
        }
    }

    private fun deleteTransaction() {
        mTransaction!!.delete()
        mBudget!!.transactions.remove(mTransaction)
        mBudget!!.cachedValue -= mTransaction!!.totalAmount
        mBudget!!.updateAsync { rows ->
            if (rows > 0) {
                mEventBus.post(BudgetUpdatedEvent(mBudget!!, false))
                Util.toast("Transaction Deleted")
                dismissAllowingStateLoss()
            } else {
                Util.toast("Delete Failed")
            }
        }
    }

    private fun splitEvenly() {
        if (isSplit) {
            val currentValue = valueFromEditText(mEditTextAmount)
            val otherValue = valueFromEditText(mEditTextAmountOther)
            val sum = Math.round((currentValue + otherValue) * 100).toDouble() / 100.0
            mEditTextAmount.setText(java.lang.Double.toString(sum))
            mEditTextAmountOther.setText(null)
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
