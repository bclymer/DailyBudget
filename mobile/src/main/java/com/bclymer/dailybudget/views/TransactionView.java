package com.bclymer.dailybudget.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by bclymer on 9/28/2014.
 */
public class TransactionView extends LinearLayout {

    private static SimpleDateFormat SIMPLE_DATE_FORMAT;

    public TransactionView(Context context) {
        super(context);
        init();
    }

    public TransactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransactionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public static TransactionView createTransactionView(LayoutInflater inflater, TransactionView recycledView, ViewGroup parent, Transaction transaction) {
        ViewHolder holder;
        if (recycledView == null) {
            recycledView = (TransactionView) inflater.inflate(R.layout.list_item_transaction, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_date);
            holder.amount = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_amount);
            holder.amountOther = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_amount_other);
            holder.notes = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_notes);
            holder.paidForSomeone = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_paidforsomeone);
            recycledView.setTag(holder);
        } else {
            holder = (ViewHolder) recycledView.getTag();
        }
        holder.date.setText(SIMPLE_DATE_FORMAT.format(transaction.date));
        holder.amount.setText(Util.INSTANCE.makeLikeMoney(transaction.amount));
        if (TextUtils.isEmpty(transaction.location)) {
            holder.notes.setVisibility(GONE);
            holder.amountOther.setVisibility(GONE);
        } else {
            holder.notes.setVisibility(VISIBLE);
            holder.notes.setText(transaction.location);
            holder.amountOther.setVisibility(VISIBLE);
            holder.amountOther.setText(Util.INSTANCE.makeLikeMoney(transaction.amountOther));
        }
        holder.paidForSomeone.setVisibility(transaction.paidForSomeone ? VISIBLE : GONE);
        return recycledView;
    }

    private void init() {
        if (SIMPLE_DATE_FORMAT == null && !isInEditMode()) {
            SIMPLE_DATE_FORMAT = new SimpleDateFormat("c MMM d yyyy", Locale.getDefault());
        }
    }

    private static class ViewHolder {
        TextView date;
        TextView amount;
        TextView amountOther;
        TextView notes;
        TextView paidForSomeone;
    }
}
