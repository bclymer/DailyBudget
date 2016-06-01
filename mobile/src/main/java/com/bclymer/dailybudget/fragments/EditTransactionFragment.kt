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
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BaseRepository
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.database.TransactionRepository
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.utilities.Util
import com.travefy.travefy.core.bindView
import java.sql.SQLException
import java.util.*

/**
 * Created by bclymer on 9/28/2014.
 */
class EditTransactionFragment() : BaseFragment(R.layout.fragment_edit_transaction) {

    private val mEditTextAmount: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount)
    private val mEditTextAmountOther: EditText by bindView(R.id.fragment_edit_transaction_edittext_amount_other)
    private val mEditTextNotes: AutoCompleteTextView by bindView(R.id.fragment_edit_transaction_edittext_notes)
    private val mDatePicker: DatePicker by bindView(R.id.fragment_edit_transaction_datepicker_date)
    private val mButtonSave: Button by bindView(R.id.fragment_edit_transaction_button_add_transaction)
    private val mButtonDelete: Button by bindView(R.id.fragment_edit_transaction_button_delete_transaction)
    private val mButtonSplit: Button by bindView(R.id.fragment_edit_transaction_button_split)
    private val mLayoutOther: ViewGroup by bindView(R.id.fragment_edit_transaction_layout_other)

    private var mBudgetId: Int = -1
    private var mTransactionId: Int = -1
    private var mEditingTransaction = false
    private var isSplit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
        mTransactionId = arguments.getInt(EXTRA_TRANSACTION_ID, -1)
        mEditingTransaction = (mTransactionId != -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.actionBar.title = BudgetRepository.getById(mBudgetId)?.name

        val cal = Calendar.getInstance()
        if (mEditingTransaction) {
            val transaction = TransactionRepository.getById(mTransactionId)!!
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

        mButtonDelete.setOnClickListener {
            deleteTransaction()
        }

        mButtonSplit.setOnClickListener {
            splitEvenly()
        }

        mButtonSave.setOnClickListener {
            addTransaction()
        }
    }

    private fun textChanged(query: String) {
        try {
            val trans = TransactionRepository.searchByLocation(query)
            val unique = HashMap<String, Double>()
            for (t in trans) {
                if (t.location == null) continue
                if (unique.containsKey(t.location)) {
                    val oldValue = unique[t.location]!!
                    unique.put(t.location, oldValue + t.totalAmount)
                } else {
                    unique.put(t.location, t.totalAmount)
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
            val finalUgh = ArrayList<String>()
            for (entry in list) {
                finalUgh.add(entry.key)
            }
            val adapter = ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, finalUgh)
            mEditTextNotes.setAdapter(adapter)
            mEditTextNotes.showDropDown()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun addTransaction() {
        val amountMe = mEditTextAmount.text.toString().toDouble()
        val amountOther = mEditTextAmountOther.text.toString().toDouble()
        val date = Date(mDatePicker.calendarView.date)
        TransactionRepository.updateTransaction(mTransactionId, amountMe, amountOther, date, isSplit, mEditTextAmount.text.toString())

        BaseRepository.mainRealm.executeTransaction {

            val transaction = TransactionRepository.getById(mTransactionId) ?: it.createObject(Transaction::class.java)
            val budget = BudgetRepository.getById(mBudgetId)!!

            if (mEditingTransaction) {
                budget.cachedValue -= transaction.totalAmount // this will be undone later.
            }

            transaction.paidForSomeone = isSplit
            transaction.date = null

            transaction.amount = -1 * java.lang.Double.valueOf(mEditTextAmount.text.toString())!!
            if (transaction.paidForSomeone) {
                transaction.amountOther = -1 * java.lang.Double.valueOf(mEditTextAmountOther.text.toString())!!
            } else {
                transaction.amountOther = 0.0
            }
            transaction.location = mEditTextNotes.text.toString()
            if (!mEditingTransaction) {
                budget.transactions.add(transaction)
                transaction.budget = budget
            }
            budget.cachedValue += transaction.totalAmount
            budget.cachedDate = Date()
        }

        Util.toast("Transaction Saved")
        fragmentManager.popBackStack()
    }

    private fun deleteTransaction() {
        val transaction = TransactionRepository.getById(mTransactionId)!!
        val budget = BudgetRepository.getById(mBudgetId)!!

        BaseRepository.mainRealm.executeTransaction {
            budget.cachedValue -= transaction.totalAmount
        }

        TransactionRepository.delete(transaction)

        Util.toast("Transaction Deleted");
        fragmentManager.popBackStack()
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

        fun newInstance(budgetId: Int, transactionId: Int = NO_TRANSACTION_ID_VALUE): EditTransactionFragment {
            val fragment = EditTransactionFragment()
            val bundle = Bundle(2)
            bundle.putInt(EXTRA_BUDGET_ID, budgetId)
            bundle.putInt(EXTRA_TRANSACTION_ID, transactionId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
