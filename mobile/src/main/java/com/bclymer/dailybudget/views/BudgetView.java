package com.bclymer.dailybudget.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.utilities.Util;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetView extends RelativeLayout {

    private OnClickListener mOnEditClickListener;
    private OnClickListener mOnAddTransactionClickListener;
    private OnClickListener mOnBudgetClickListener;

    public BudgetView(Context context) {
        super(context);
        init();
    }

    public BudgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BudgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ButterKnife.inject(this, this);
    }

    public void setOnEditClickListener(OnClickListener onEditClickListener) {
        mOnEditClickListener = onEditClickListener;
    }

    public void setOnAddTransactionClickListener(OnClickListener onAddTransactionClickListener) {
        mOnAddTransactionClickListener = onAddTransactionClickListener;
    }

    public void setOnBudgetClickListener(OnClickListener onBudgetClickListener) {
        mOnBudgetClickListener = onBudgetClickListener;
    }

    public static BudgetView createBudgetView(LayoutInflater inflater, BudgetView recycledView, ViewGroup parent, Budget budget) {
        ViewHolder holder;
        if (recycledView == null) {
            recycledView = (BudgetView) inflater.inflate(R.layout.list_item_budget, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) recycledView.findViewById(R.id.list_item_budget_textview_name);
            holder.amount = (TextView) recycledView.findViewById(R.id.list_item_budget_textview_amount);
            final BudgetView finalRecycledView = recycledView;
            recycledView.findViewById(R.id.list_item_budget_imagebutton_addtransaction).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalRecycledView.mOnAddTransactionClickListener != null) {
                        finalRecycledView.mOnAddTransactionClickListener.onClick(finalRecycledView);
                    }
                }
            });
            recycledView.findViewById(R.id.list_item_budget_imagebutton_edit).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalRecycledView.mOnEditClickListener != null) {
                        finalRecycledView.mOnEditClickListener.onClick(finalRecycledView);
                    }
                }
            });
            recycledView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalRecycledView.mOnBudgetClickListener != null) {
                        finalRecycledView.mOnBudgetClickListener.onClick(finalRecycledView);
                    }
                }
            });
            recycledView.setTag(holder);
        } else {
            holder = (ViewHolder) recycledView.getTag();
        }
        holder.name.setText(budget.name);
        holder.amount.setText(Util.makeLikeMoney(budget.cachedValue));
        return recycledView;
    }

    private static class ViewHolder {
        TextView name;
        TextView amount;
    }
}
