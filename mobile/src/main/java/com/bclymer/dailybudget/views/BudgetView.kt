package com.bclymer.dailybudget.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.ButterKnife
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.utilities.Util

/**
 * Created by bclymer on 9/28/2014.
 */
class BudgetView : RelativeLayout {

    private var mOnEditClickListener: View.OnClickListener? = null
    private var mOnViewTransactionsClickListener: View.OnClickListener? = null
    private var mOnBudgetClickListener: View.OnClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        ButterKnife.bind(this, this)
    }

    fun setOnEditClickListener(onEditClickListener: View.OnClickListener) {
        mOnEditClickListener = onEditClickListener
    }

    fun setOnViewTransactionsClickListener(onViewTransactionsClickListener: View.OnClickListener) {
        mOnViewTransactionsClickListener = onViewTransactionsClickListener
    }

    fun setOnBudgetClickListener(onBudgetClickListener: View.OnClickListener) {
        mOnBudgetClickListener = onBudgetClickListener
    }

    // TODO recycler view holder with inits and stuff.
    private class ViewHolder {
        internal var name: TextView? = null
        internal var amount: TextView? = null
    }

    companion object {

        fun createBudgetView(inflater: LayoutInflater, recycledView: BudgetView?, parent: ViewGroup, budget: Budget): BudgetView {
            var recycledView = recycledView
            val holder: ViewHolder
            if (recycledView == null) {
                recycledView = inflater.inflate(R.layout.list_item_budget, parent, false) as BudgetView
                holder = ViewHolder()
                holder.name = recycledView.findViewById(R.id.list_item_budget_textview_name) as TextView
                holder.amount = recycledView.findViewById(R.id.list_item_budget_textview_amount) as TextView
                val finalRecycledView = recycledView
                recycledView.findViewById(R.id.list_item_budget_imagebutton_viewtransactions).setOnClickListener {
                    if (finalRecycledView.mOnViewTransactionsClickListener != null) {
                        finalRecycledView.mOnViewTransactionsClickListener!!.onClick(finalRecycledView)
                    }
                }
                recycledView.findViewById(R.id.list_item_budget_imagebutton_edit).setOnClickListener {
                    if (finalRecycledView.mOnEditClickListener != null) {
                        finalRecycledView.mOnEditClickListener!!.onClick(finalRecycledView)
                    }
                }
                recycledView.setOnClickListener(View.OnClickListener {
                    if (finalRecycledView.mOnBudgetClickListener != null) {
                        finalRecycledView.mOnBudgetClickListener!!.onClick(finalRecycledView)
                    }
                })
                recycledView.tag = holder
            } else {
                holder = recycledView.tag as ViewHolder
            }
            holder.name?.text = budget.name
            holder.amount?.text = Util.makeLikeMoney(budget.cachedValue)
            return recycledView
        }
    }
}
