package com.bclymer.dailybudget.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.Util
import com.bclymer.dailybudget.views.BudgetView
import com.nhaarman.listviewanimations.appearance.AnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by bclymer on 9/26/2014.
 */
class BudgetsFragment() : BaseFragment(R.layout.fragment_budgets) {

    private val mGridView: GridView by bindView(android.R.id.list)
    private val mEmptyView: ViewGroup by bindView(android.R.id.empty)
    private val mButtonNewBudget: Button by bindView(R.id.fragment_budgets_emptyview_button_new_budget)
    private val mTextViewTotalBudget: TextView by bindView(R.id.fragment_budgets_textview_total)

    private var mAdapter: AnimationAdapter = ScaleInAnimationAdapter(BudgetAdapter())
    private var mBudgetList: MutableList<Budget> = arrayListOf()

    private val mCallback: BudgetSelectedCallback by lazy { activity as BudgetSelectedCallback }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity !is BudgetSelectedCallback) {
            throw RuntimeException("Activity $activity must implement BudgetSelectedCallback to display BudgetsFragment")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BudgetRepository.updateAndGetBudgets()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mBudgetList = it.toMutableList()
                    updateTotalBudget()
                    mAdapter.notifyDataSetChanged()
                }

        mAdapter.setAbsListView(mGridView)
        mGridView.adapter = mAdapter
        mGridView.emptyView = mEmptyView
        mEventBus.register(this)

        mGridView.setOnItemClickListener { adapterView, view, position, l ->
            mCallback.onBudgetSelected(mBudgetList[position].id)
        }

        mButtonNewBudget.setOnClickListener {
            mCallback.onBudgetSelected(EditBudgetFragment.NO_BUDGET_ID_VALUE)
        }
    }

    override fun onDestroyView() {
        mEventBus.unregister(this)
        super.onDestroyView()
    }

    fun onEventMainThread(event: BudgetUpdatedEvent) {
        val copiedList = ArrayList(mBudgetList)
        for (budget in copiedList) {
            if (budget.id == event.budget.id) {
                if (event.deleted) {
                    mBudgetList.remove(event.budget)
                } else {
                    BudgetRepository.cloneBudget(from = event.budget, to = budget)
                }
                updateTotalBudget()
                mAdapter.notifyDataSetChanged()
                return
            }
        }
        mBudgetList.add(event.budget)
        updateTotalBudget()
        mAdapter.notifyDataSetChanged()
    }

    private fun updateTotalBudget() {
        mTextViewTotalBudget.text = "Total Budget: ${Util.makeLikeMoney(mBudgetList.sumByDouble { it.amountPerPeriod })}"
    }

    interface BudgetSelectedCallback {
        fun onBudgetSelected(budgetId: Int)
    }

    private inner class BudgetAdapter : BaseAdapter() {

        private val mInflater: LayoutInflater by lazy { activity.layoutInflater }

        override fun getCount() = mBudgetList.size

        override fun getItem(position: Int) = mBudgetList[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
            val budget = mBudgetList[position]
            val budgetView = BudgetView.createBudgetView(mInflater, view as? BudgetView, viewGroup, budget)

            budgetView.setOnViewTransactionsClickListener(View.OnClickListener { BudgetTransactionsFragment.newInstance(budget.id).show(fragmentManager, BudgetTransactionsFragment.TAG) })
            budgetView.setOnEditClickListener(View.OnClickListener { mCallback.onBudgetSelected(budget.id) })
            budgetView.setOnBudgetClickListener(View.OnClickListener { EditTransactionFragment.newInstance(budget.id).show(fragmentManager, EditTransactionFragment.TAG) })

            return budgetView
        }
    }

    companion object {

        fun newInstance(): BudgetsFragment {
            return BudgetsFragment()
        }
    }
}
