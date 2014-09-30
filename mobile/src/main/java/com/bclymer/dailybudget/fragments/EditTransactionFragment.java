package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.ThreadManager;
import com.bclymer.dailybudget.utilities.Util;

import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class EditTransactionFragment extends BaseDialogFragment {

    public static final String TAG = "AddTransactionFragment";
    public static final int NO_TRANSACTION_ID_VALUE = -1;

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";
    private static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";

    @InjectView(R.id.fragment_edit_transaction_edittext_amount)
    protected EditText mEditTextAmount;
    @InjectView(R.id.fragment_edit_transaction_datepicker_date)
    protected DatePicker mDatePicker;

    private Budget mBudget;
    private Transaction mTransaction;
    private boolean mEditingTransaction = false;

    public static EditTransactionFragment newInstance(int budgetId) {
        return newInstance(budgetId, NO_TRANSACTION_ID_VALUE);
    }

    public static EditTransactionFragment newInstance(int budgetId, int transactionId) {
        EditTransactionFragment fragment = new EditTransactionFragment();
        Bundle bundle = new Bundle(2);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        bundle.putInt(EXTRA_TRANSACTION_ID, transactionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_edit_transaction;

        int budgetId = getArguments().getInt(EXTRA_BUDGET_ID);
        int transactionId = getArguments().getInt(EXTRA_TRANSACTION_ID, -1);
        mBudget = Budget.getDao().queryForId(budgetId);
        if (transactionId == -1) {
            mTransaction = new Transaction();
        } else {
            mTransaction = Transaction.getDao().queryForId(transactionId);
            mEditingTransaction = true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar cal = Calendar.getInstance();
        if (mEditingTransaction) {
            cal.setTime(mTransaction.date);
            mEditTextAmount.setText(Double.toString(mTransaction.amount));
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);
        mEditTextAmount.requestFocus();
    }

    @OnClick(R.id.fragment_edit_transaction_button_add_transaction)
    protected void addTransaction() {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                mTransaction.date = new Date(mDatePicker.getCalendarView().getDate());
                mTransaction.amount = -1 * Double.valueOf(mEditTextAmount.getText().toString());
                if (mEditingTransaction) {
                    mTransaction.update();
                } else {
                    mBudget.transactions.add(mTransaction);
                }
                mBudget.cachedValue += mTransaction.amount;
                mBudget.cachedDate = new Date();
                mBudget.update();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Util.toast("Transaction Saved");
                dismissAllowingStateLoss();
            }
        });
    }
}
