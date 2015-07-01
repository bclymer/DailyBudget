package com.bclymer.dailybudget.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.DisplayUtility;
import com.bclymer.dailybudget.utilities.Util;

import java.text.SimpleDateFormat;

/**
 * Created by bclymer on 9/28/2014.
 */
public class TransactionView extends LinearLayout {

    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("c MMM d yyyy");
    private float firstY = Integer.MIN_VALUE;

    public TransactionView(Context context) {
        super(context);
    }

    public TransactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static TransactionView createTransactionView(LayoutInflater inflater, TransactionView recycledView, ViewGroup parent, Transaction transaction) {
        ViewHolder holder;
        if (recycledView == null) {
            recycledView = (TransactionView) inflater.inflate(R.layout.list_item_transaction, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_date);
            holder.amount = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_amount);
            holder.notes = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_notes);
            holder.paidForSomeone = (TextView) recycledView.findViewById(R.id.list_item_transaction_textview_paidforsomeone);
            recycledView.setTag(holder);
        } else {
            holder = (ViewHolder) recycledView.getTag();
        }
        holder.date.setText(SIMPLE_DATE_FORMAT.format(transaction.date));
        holder.amount.setText(Util.makeLikeMoney(transaction.amount));
        if (TextUtils.isEmpty(transaction.notes)) {
            holder.notes.setVisibility(GONE);
        } else {
            holder.notes.setVisibility(VISIBLE);
            holder.notes.setText(transaction.notes);
        }
        holder.paidForSomeone.setVisibility(transaction.paidForSomeone ? VISIBLE : GONE);
        return recycledView;
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if (firstY == Integer.MIN_VALUE) {
            firstY = getY();
        }
        setY(firstY + (pressed ? DisplayUtility.dpToPixels(2) : 0));
    }

    private static class ViewHolder {
        TextView date;
        TextView amount;
        TextView notes;
        TextView paidForSomeone;
    }
}
