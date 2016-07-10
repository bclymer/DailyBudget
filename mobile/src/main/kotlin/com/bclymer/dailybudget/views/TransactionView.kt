package com.bclymer.dailybudget.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.models.Transaction
import com.bclymer.dailybudget.utilities.Util

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by bclymer on 9/28/2014.
 */
class TransactionView : LinearLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        if (SIMPLE_DATE_FORMAT == null && !isInEditMode) {
            SIMPLE_DATE_FORMAT = SimpleDateFormat("c MMM d yyyy", Locale.getDefault())
        }
    }

    private class ViewHolder {
        internal var date: TextView? = null
        internal var amount: TextView? = null
        internal var amountOther: TextView? = null
        internal var notes: TextView? = null
        internal var paidForSomeone: TextView? = null
    }

    companion object {

        private var SIMPLE_DATE_FORMAT: SimpleDateFormat? = null

        fun createTransactionView(inflater: LayoutInflater, recycledView: TransactionView?, parent: ViewGroup, transaction: Transaction): TransactionView {
            var convertView = recycledView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_transaction, parent, false) as TransactionView
                holder = ViewHolder()
                holder.date = convertView.findViewById(R.id.list_item_transaction_textview_date) as TextView
                holder.amount = convertView.findViewById(R.id.list_item_transaction_textview_amount) as TextView
                holder.amountOther = convertView.findViewById(R.id.list_item_transaction_textview_amount_other) as TextView
                holder.notes = convertView.findViewById(R.id.list_item_transaction_textview_notes) as TextView
                holder.paidForSomeone = convertView.findViewById(R.id.list_item_transaction_textview_paidforsomeone) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder.date!!.text = SIMPLE_DATE_FORMAT!!.format(transaction.date)
            holder.amount!!.text = Util.makeLikeMoney(transaction.amount)
            if (TextUtils.isEmpty(transaction.location)) {
                holder.notes!!.visibility = View.GONE
            } else {
                holder.notes!!.visibility = View.VISIBLE
                holder.notes!!.text = transaction.location
            }
            holder.amountOther!!.visibility = View.VISIBLE
            holder.amountOther!!.text = Util.makeLikeMoney(transaction.amountOther)
            holder.paidForSomeone!!.visibility = if (transaction.paidForSomeone) View.VISIBLE else View.GONE
            return convertView
        }
    }
}
