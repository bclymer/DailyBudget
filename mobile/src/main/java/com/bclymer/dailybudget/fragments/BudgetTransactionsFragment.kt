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
import com.bclymer.dailybudget.MainActivity
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.database.TransactionRepository
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.views.TransactionView
import com.travefy.travefy.core.bindView
import java.util.*

/**
 * Created by bclymer on 9/28/2014.
 */
class BudgetTransactionsFragment() : BaseFragment(R.layout.fragment_budget_transactions) {

    private val mListView: AbsListView by bindView(R.id.fragment_budget_transactions_listview)
    private val mEmptyView: ViewGroup by bindView(R.id.include_no_transactions_empty)
    private val mEditTextFilter: EditText by bindView(R.id.fragment_budget_transactions_edittext_filter)
    private val mButtonNewTransaction: Button by bindView(R.id.fragment_budget_transactions_button_new_transaction)
    private val mButtonStats: Button by bindView(R.id.fragment_budget_transactions_button_stats)

    private val mAdapter = TransactionAdapter()

    private var mTransactionList: List<Transaction> = listOf()
    private var mTransactionListFiltered: MutableList<Transaction> = mutableListOf()

    private var mBudgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).actionBar.title = BudgetRepository.getById(mBudgetId)?.name

        TransactionRepository.monitorTransactions(mBudgetId).subscribeOnLifecycle(onNext = {
            mTransactionList = it
            mTransactionListFiltered = ArrayList(mTransactionList)
        })

        mListView.adapter = mAdapter
        mListView.emptyView = mEmptyView

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

        mButtonNewTransaction.setOnClickListener {
            fragmentManager.beginTransaction()
                    .add(R.id.main_activity_fragment_main, EditTransactionFragment.newInstance(mBudgetId), EditTransactionFragment.TAG)
                    .commit()
        }

        mButtonStats.setOnClickListener {
            fragmentManager.beginTransaction()
                    .add(R.id.main_activity_fragment_main, BudgetStatsFragment.newInstance(mBudgetId), BudgetStatsFragment.TAG)
                    .commit()
        }
    }

    private fun filterFullList() {
        val filter = mEditTextFilter.text.toString()
        if (filter.length == 0) {
            mTransactionListFiltered = ArrayList(mTransactionList)
        }
        mTransactionListFiltered.clear()
        for (t in mTransactionList) {
            if (t.location == null) continue
            if (t.location.toLowerCase().contains(filter.toLowerCase())) {
                mTransactionListFiltered.add(t)
            }
        }
    }

    private inner class TransactionAdapter : BaseAdapter() {

        private val mInflater: LayoutInflater by lazy {
            activity.layoutInflater
        }

        override fun getCount(): Int {
            return mTransactionListFiltered.size
        }

        override fun getItem(position: Int): Transaction {
            return mTransactionListFiltered[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val transaction = mTransactionListFiltered[position]
            val transactionView = TransactionView.createTransactionView(mInflater, convertView as? TransactionView, parent, transaction)
            transactionView.setOnClickListener {
                fragmentManager.beginTransaction()
                        .add(R.id.main_activity_fragment_main, EditTransactionFragment.newInstance(mBudgetId, transaction.id), EditTransactionFragment.TAG)
                        .commit()
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
