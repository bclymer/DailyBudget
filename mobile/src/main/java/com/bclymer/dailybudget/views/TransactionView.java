package com.bclymer.dailybudget.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Transaction;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class TransactionView extends LinearLayout {

    @InjectView(R.id.list_item_transaction_textview_date)
    protected TextView mTextViewDate;
    @InjectView(R.id.list_item_transaction_textview_amount)
    protected TextView mTextViewAmount;

    private OnClickListener mOnDateClickListener;
    private OnClickListener mOnAmountClickListener;

    public TransactionView(Context context) {
        super(context);
    }

    public TransactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context) {
        View.inflate(context, R.layout.list_item_transaction, this);
        ButterKnife.inject(this, this);
    }

    public void setOnDateClickListener(OnClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    public void setOnAmountClickListener(OnClickListener onAmountClickListener) {
        mOnAmountClickListener = onAmountClickListener;
    }

    @OnClick(R.id.list_item_transaction_textview_date)
    protected void dateClick() {
        if (mOnDateClickListener != null) {
            mOnDateClickListener.onClick(this);
        }
    }

    @OnClick(R.id.list_item_transaction_textview_amount)
    protected void amountClick() {
        if (mOnAmountClickListener != null) {
            mOnAmountClickListener.onClick(this);
        }
    }

    public static TransactionView createTransactionView(LayoutInflater inflater, TransactionView recycledView, ViewGroup parent, Transaction transaction) {
        ViewHolder holder;
        if (recycledView == null) {
            recycledView = (TransactionView) inflater.inflate(R.layout.list_item_transaction, parent);
            holder = new ViewHolder();
            holder.date = recycledView.mTextViewDate;
            holder.amount = recycledView.mTextViewAmount;
        } else {
            holder = (ViewHolder) recycledView.getTag();
        }
        holder.date.setText(SIMPLE_DATE_FORMAT.format(transaction.date));
        holder.amount.setText(Double.toString(transaction.amount));
        return recycledView;
    }

    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("c MMM d yyyy, K:ma");

    private static class ViewHolder {
        TextView date;
        TextView amount;
    }
}
