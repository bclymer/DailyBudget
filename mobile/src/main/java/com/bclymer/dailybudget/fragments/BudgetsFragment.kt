package com.bclymer.dailybudget.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.events.BudgetUpdatedEvent
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.views.BudgetView
import com.nhaarman.listviewanimations.appearance.AnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
class BudgetsFragment() : BaseFragment() {

    private val mGridView: GridView by bindView(android.R.id.list)
    private val mEmptyView: ViewGroup by bindView(android.R.id.empty)
    private val mButtonNewBudget: Button by bindView(R.id.fragment_budgets_emptyview_button_new_budget)

    private var mAdapter: AnimationAdapter = ScaleInAnimationAdapter(BudgetAdapter())
    private var mBudgetList: MutableList<Budget> = arrayListOf()

    private var mCallback: BudgetSelectedCallback by Delegates.notNull()

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity !is BudgetSelectedCallback) {
            throw RuntimeException("Activity $activity must implement BudgetSelectedCallback to display BudgetsFragment")
        } else {
            mCallback = activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutId = R.layout.fragment_budgets
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BudgetRepository.getBudgets()
                .doOnNext { it.forEach { it.updateCache() } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mBudgetList = it.toMutableList()
                    mAdapter.notifyDataSetChanged()
                })

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
                    event.budget.cloneInto(budget)
                }
                mAdapter.notifyDataSetChanged()
                return
            }
        }

        mBudgetList.add(event.budget)
        mAdapter.notifyDataSetChanged()
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

            budgetView.setOnViewTransactionsClickListener { BudgetTransactionsFragment.newInstance(budget.id).show(fragmentManager, BudgetTransactionsFragment.TAG) }
            budgetView.setOnEditClickListener { mCallback.onBudgetSelected(budget.id) }
            budgetView.setOnBudgetClickListener { EditTransactionFragment.newInstance(budget.id).show(fragmentManager, EditTransactionFragment.TAG) }

            return budgetView
        }
    }

    companion object {

        fun newInstance(): BudgetsFragment {
            return BudgetsFragment()
        }
    }
}
