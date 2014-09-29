package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.ThreadManager;

import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class AddTransactionFragment extends BaseDialogFragment {

    public static final String TAG = "AddTransactionFragment";

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    @InjectView(R.id.fragment_add_transaction_edittext_amount)
    protected EditText mEditTextAmount;
    @InjectView(R.id.fragment_add_transaction_datepicker_date)
    protected DatePicker mDatePicker;

    private int mBudgetId;

    public static AddTransactionFragment newInstance(int budgetId) {
        AddTransactionFragment fragment = new AddTransactionFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_add_transaction;
        mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);
        mEditTextAmount.requestFocus();
    }

    @OnClick(R.id.fragment_add_transaction_button_add_transaction)
    protected void addTransaction() {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                Transaction transaction = new Transaction(new Date(mDatePicker.getCalendarView().getDate()), -1 * Double.valueOf(mEditTextAmount.getText().toString()));
                Budget budget = Budget.getDao().queryForId(mBudgetId);
                budget.transactions.add(transaction);
                budget.cachedValue += transaction.amount;
                budget.cachedDate = new Date();
                budget.update();
            }
        }, new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
            }
        });
    }
}
