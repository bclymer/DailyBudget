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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetView extends RelativeLayout {

    @InjectView(R.id.list_item_budget_textview_name)
    protected TextView mTextViewName;
    @InjectView(R.id.list_item_budget_textview_amount)
    protected TextView mTextViewAmount;

    private OnClickListener mOnEditClickListener;
    private OnClickListener mOnAddTransactionClickListener;
    private OnClickListener mOnBudgetClickListener;

    public BudgetView(Context context) {
        super(context);
    }

    public BudgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BudgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context) {
        View.inflate(context, R.layout.list_item_budget, this);
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

    @OnClick(R.id.list_item_budget_imagebutton_addtransaction)
    protected void addTransaction() {
        if (mOnAddTransactionClickListener != null) {
            mOnAddTransactionClickListener.onClick(this);
        }
    }

    @OnClick(R.id.list_item_budget_imagebutton_edit)
    protected void edit() {
        if (mOnEditClickListener != null) {
            mOnEditClickListener.onClick(this);
        }
    }

    @OnClick(R.id.list_item_budget)
    protected void budgetClicked() {
        if (mOnBudgetClickListener != null) {
            mOnBudgetClickListener.onClick(this);
        }
    }

    public static BudgetView createBudgetView(LayoutInflater inflater, BudgetView recycledView, ViewGroup parent, Budget budget) {
        ViewHolder holder;
        if (recycledView == null) {
            recycledView = (BudgetView) inflater.inflate(R.layout.list_item_budget, parent);
            holder = new ViewHolder();
            holder.name = recycledView.mTextViewName;
            holder.amount = recycledView.mTextViewAmount;
        } else {
            holder = (ViewHolder) recycledView.getTag();
        }
        holder.name.setText(budget.name);
        holder.amount.setText(Double.toString(budget.cachedValue));
        return recycledView;
    }

    private static class ViewHolder {
        TextView name;
        TextView amount;
    }
}
