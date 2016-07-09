package com.bclymer.dailybudget.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.bindView
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.database.BudgetRepository
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.Util
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by bclymer on 12/8/14.
 * Things I want from this class:
 * 1) A weekly, monthly, and overall list of places, sortable by:
 * a) number of visits
 * b) total money spent
 * c) alphabetical
 * d) average spent per visit
 * 2) A graph of money spent per day, week, and month.
 * 3) Be able to select any place and bring up a list of only those transactions.
 */
class BudgetStatsFragment : BaseDialogFragment() {

    private val mTextViewTotalAmount: TextView by bindView(R.id.fragment_budget_stats_textview_totalamount)
    private val mTextViewAmountPerDay: TextView by bindView(R.id.fragment_budget_stats_textview_amountperday)
    private val mTextViewSortedPlaces: TextView by bindView(R.id.fragment_budget_stats_textview_sortedplaces)

    private var mBudgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutId = R.layout.fragment_budget_stats
        mBudgetId = arguments.getInt(EXTRA_BUDGET_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val budget = Budget.getDao().queryForId(mBudgetId)
        dialog.setTitle(budget.name)

        BudgetRepository.getBudgetStats(mBudgetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { stats ->
                    mTextViewTotalAmount.text = "Total: " + Util.makeLikeMoney(stats.totalSpent)
                    mTextViewAmountPerDay.text = "Per Day: " + Util.makeLikeMoney(stats.spentPerDay)
                    val stringBuilder = StringBuilder("Favorite Places\n")
                    for ((key, value) in stats.places) {
                        stringBuilder.append(key).append(": ").append(Util.makeLikeMoney(value)).append("\n")
                    }
                    mTextViewSortedPlaces.text = stringBuilder.toString()
                }

    }

    companion object {

        val TAG = "BudgetStatsFragment"

        private val EXTRA_BUDGET_ID = "extra_budget_id"

        fun newInstance(budgetId: Int): BudgetStatsFragment {
            val fragment = BudgetStatsFragment()
            val bundle = Bundle(1)
            bundle.putInt(EXTRA_BUDGET_ID, budgetId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
