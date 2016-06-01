package com.bclymer.dailybudget.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.views.BudgetView
import com.nhaarman.listviewanimations.appearance.AnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter
import com.travefy.travefy.core.bindView
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
class BudgetsFragment() : BaseFragment(R.layout.fragment_budgets) {

    private val mGridView: GridView by bindView(R.id.fragment_budgets_gridview)
    private val mEmptyView: ViewGroup by bindView(R.id.fragment_budgets_empty)
    private val mButtonCreateBudget: Button by bindView(R.id.fragment_budgets_emptyview_button_new_budget)

    private val mAdapter: AnimationAdapter by lazy {
        ScaleInAnimationAdapter(BudgetAdapter())
    }

    private var mBudgetList: List<Budget> = listOf()

    private var mCallback: BudgetSelectedCallback by Delegates.notNull()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is BudgetSelectedCallback) {
            throw RuntimeException("Activity $context must implement BudgetSelectedCallback to display BudgetsFragment")
        } else {
            mCallback = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BudgetRepository.monitorAll().subscribeOnLifecycle(onNext = {
            it.forEach { BudgetRepository.updateCache(it) }
            mBudgetList = it
            mAdapter.notifyDataSetChanged()
        })

        mAdapter.setAbsListView(mGridView)
        mGridView.adapter = mAdapter
        mGridView.emptyView = mEmptyView

        mGridView.setOnItemClickListener { adapterView, view, position, id ->
            mCallback.onBudgetSelected(mBudgetList[position].id)
        }

        mButtonCreateBudget.setOnClickListener {
            mCallback.onBudgetSelected(EditBudgetFragment.NO_BUDGET_ID_VALUE)
        }
    }

    interface BudgetSelectedCallback {
        fun onBudgetSelected(budgetId: Int)
    }

    private inner class BudgetAdapter : BaseAdapter() {

        private val mInflater: LayoutInflater

        init {
            mInflater = activity.layoutInflater
        }

        override fun getCount() = mBudgetList.size

        override fun getItem(position: Int) = mBudgetList[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val budget = mBudgetList[position]
            val budgetView = BudgetView.createBudgetView(mInflater, view as? BudgetView, viewGroup, budget)

            budgetView.setOnViewTransactionsClickListener {
                fragmentManager.beginTransaction()
                        .add(R.id.main_activity_fragment_main, BudgetTransactionsFragment.newInstance(budget.id), BudgetTransactionsFragment.TAG)
                        .commit()
            }
            budgetView.setOnEditClickListener {
                mCallback.onBudgetSelected(budget.id)
            }
            budgetView.setOnBudgetClickListener {
                fragmentManager.beginTransaction()
                        .add(R.id.main_activity_fragment_main, EditTransactionFragment.newInstance(budget.id), EditTransactionFragment.TAG)
                        .commit()
            }

            return budgetView
        }
    }

    companion object {

        fun newInstance(): BudgetsFragment {
            return BudgetsFragment()
        }
    }
}
