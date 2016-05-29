package com.bclymer.dailybudget.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.EditText
import butterknife.OnClick
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.utilities.ThreadManager
import com.bclymer.dailybudget.views.TransactionView
import com.travefy.travefy.core.bindView
import java.util.*

/**
 * Created by bclymer on 9/28/2014.
 */
class BudgetTransactionsFragment : BaseDialogFragment() {

    private val mListView: AbsListView by bindView(R.id.fragment_budget_transactions_listview)
    private val mEmptyView: ViewGroup by bindView(R.id.include_no_transactions_empty)
    private val mEditTextFilter: EditText by bindView(R.id.fragment_budget_transactions_edittext_filter)

    private var mAdapter: TransactionAdapter? = null
    private var mTransactionList: List<Transaction>? = null
    private var mTransactionListFiltered: MutableList<Transaction>? = null

    private var mBudgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutId = R.layout.fragment_budget_transactions
        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val budget = Budget.getDao().queryForId(mBudgetId)

        dialog.setTitle(budget.name)
        mTransactionList = budget.sortedTransactions
        mTransactionListFiltered = ArrayList(mTransactionList)
        mAdapter = TransactionAdapter()
        mListView.adapter = mAdapter
        mListView.emptyView = mEmptyView

        mEditTextFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                filterFullList()
                mAdapter!!.notifyDataSetChanged()
            }
        })
    }

    @OnClick(R.id.fragment_budget_transactions_button_new_transaction)
    protected fun clickedAddTransaction() {
        EditTransactionFragment.newInstance(mBudgetId).show(fragmentManager, EditTransactionFragment.TAG)
    }

    @OnClick(R.id.fragment_budget_transactions_button_stats)
    protected fun clickedBudgetStats() {
        BudgetStatsFragment.newInstance(mBudgetId).show(fragmentManager, BudgetStatsFragment.TAG)
    }

    private fun filterFullList() {
        val filter = mEditTextFilter.text.toString()
        if (filter.length == 0) {
            mTransactionListFiltered = ArrayList(mTransactionList)
        }
        mTransactionListFiltered!!.clear()
        for (t in mTransactionList!!) {
            if (t.location == null) continue
            if (t.location.toLowerCase().contains(filter.toLowerCase())) {
                mTransactionListFiltered!!.add(t)
            }
        }
    }

    fun onEvent(event: BudgetUpdatedEvent) {
        if (event.budget.id != mBudgetId) return

        ThreadManager.runInBackgroundThenUi(Runnable {
            mTransactionList = event.budget.sortedTransactions
            filterFullList()
        }, Runnable {
            if (!isVisible || mListView == null) return@Runnable

            mAdapter!!.notifyDataSetChanged()
        })
    }

    private inner class TransactionAdapter : BaseAdapter() {

        private val mInflater: LayoutInflater

        init {
            mInflater = activity.layoutInflater
        }

        override fun getCount(): Int {
            return if (mTransactionListFiltered != null) mTransactionListFiltered!!.size else 0
        }

        override fun getItem(position: Int): Transaction {
            return mTransactionListFiltered!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val transaction = mTransactionListFiltered!![position]
            val transactionView = TransactionView.createTransactionView(mInflater, convertView as TransactionView, parent, transaction)
            transactionView.setOnClickListener { EditTransactionFragment.newInstance(mBudgetId, transaction.id).show(fragmentManager, EditTransactionFragment.TAG) }
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
