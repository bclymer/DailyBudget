package com.bclymer.dailybudget.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.database.TransactionRepository
import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.views.TransactionView
import rx.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by bclymer on 9/28/2014.
 */
class BudgetTransactionsFragment() : BaseDialogFragment(R.layout.fragment_budget_transactions) {

    private val mListView: AbsListView by bindView(android.R.id.list)
    private val mEmptyView: ViewGroup by bindView(android.R.id.empty)
    private val mEditTextFilter: EditText by bindView(R.id.fragment_budget_transactions_edittext_filter)
    private val mButtonAddTransaction: Button by bindView(R.id.fragment_budget_transactions_button_new_transaction)
    private val mButtonBudgetStats: Button by bindView(R.id.fragment_budget_transactions_button_stats)

    private var mAdapter = TransactionAdapter()
    private var mTransactionList: List<Transaction> = listOf()
    private var mTransactionListFiltered: MutableList<Transaction> = mutableListOf()
        set(value) {
            field = value
            mAdapter.notifyDataSetChanged()
        }

    private var mBudgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BudgetRepository.getBudget(mBudgetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dialog.setTitle(it.name)
                }

        loadTransactions()

        mListView.adapter = mAdapter
        mListView.emptyView = mEmptyView

        mButtonAddTransaction.setOnClickListener {
            EditTransactionFragment.newInstance(mBudgetId).show(fragmentManager, EditTransactionFragment.TAG)
        }

        mButtonBudgetStats.setOnClickListener {
            BudgetStatsFragment.newInstance(mBudgetId).show(fragmentManager, BudgetStatsFragment.TAG)
        }

        mEditTextFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                filterFullList()
                mAdapter.notifyDataSetChanged()
            }
        })

        mEventBus.register(this)
    }

    override fun onDestroyView() {
        mEventBus.unregister(this)
        super.onDestroyView()
    }

    private fun filterFullList() {
        val filter = mEditTextFilter.text.toString()
        if (filter.length == 0) {
            mTransactionListFiltered = ArrayList(mTransactionList)
        } else {
            mTransactionListFiltered = mTransactionList
                    .filter { it.location?.toLowerCase()?.contains(filter.toLowerCase()) == true }
                    .toMutableList()
        }
    }

    fun onEvent(event: BudgetUpdatedEvent) {
        if (event.budget.id != mBudgetId) return
        loadTransactions()
    }

    private fun loadTransactions() {
        TransactionRepository.getBudgetTransactions(mBudgetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mTransactionList = it
                    filterFullList()
                    mTransactionListFiltered = ArrayList(mTransactionList)
                }
    }

    private inner class TransactionAdapter : BaseAdapter() {

        private val mInflater: LayoutInflater by lazy { activity.layoutInflater }

        override fun getCount() = mTransactionListFiltered.size

        override fun getItem(position: Int) = mTransactionListFiltered[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val transaction = mTransactionListFiltered[position]
            val transactionView = TransactionView.createTransactionView(mInflater, convertView as? TransactionView, parent, transaction)
            transactionView.setOnClickListener {
                EditTransactionFragment.newInstance(mBudgetId, transaction.id).show(fragmentManager, EditTransactionFragment.TAG)
            }
            return transactionView
        }
    }

    companion object {

        val TAG = "BudgetTransactionsFragment"

        private val EXTRA_BUDGET_ID = "extra_budget_id"

        fun newInstance(budgetId: Int): BudgetTransactionsFragment {
            val fragment = BudgetTransactionsFragment()
            val bundle = Bundle(1)
            bundle.putInt(EXTRA_BUDGET_ID, budgetId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
