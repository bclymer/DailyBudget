package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.ThreadManager;
import com.bclymer.dailybudget.utilities.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao.DatabaseOperationFinishedCallback;

/**
 * Created by bclymer on 9/28/2014.
 */
public class EditTransactionFragment extends BaseDialogFragment {

    public static final String TAG = "AddTransactionFragment";
    public static final int NO_TRANSACTION_ID_VALUE = -1;

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";
    private static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";

    @Bind(R.id.fragment_edit_transaction_edittext_amount)
    protected EditText mEditTextAmount;
    @Bind(R.id.fragment_edit_transaction_edittext_amount_other)
    protected EditText mEditTextAmountOther;
    @Bind(R.id.fragment_edit_transaction_edittext_notes)
    protected AutoCompleteTextView mEditTextNotes;
    @Bind(R.id.fragment_edit_transaction_datepicker_date)
    protected DatePicker mDatePicker;
    @Bind(R.id.fragment_edit_transaction_button_add_transaction)
    protected Button mButtonSave;
    @Bind(R.id.fragment_edit_transaction_button_delete_transaction)
    protected Button mButtonDelete;
    @Bind(R.id.fragment_edit_transaction_checkbox_paidforsomeone)
    protected CheckBox mCheckBoxPaidForSomeone;
    @Bind(R.id.fragment_edit_transaction_layout_other)
    protected ViewGroup mLayoutOther;

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
            mTransaction.budget = mBudget;
        } else {
            mTransaction = Transaction.getDao().queryForId(transactionId);
            mEditingTransaction = true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(mBudget.name);
        Calendar cal = Calendar.getInstance();
        if (mEditingTransaction) {
            cal.setTime(mTransaction.date);
            mEditTextAmount.setText(Double.toString(-1 * mTransaction.amount));
            mEditTextAmountOther.setText(Double.toString(-1 * mTransaction.amountOther));
            mLayoutOther.setVisibility(mTransaction.paidForSomeone ? VISIBLE : GONE);
            mEditTextNotes.setText(mTransaction.location);
            mCheckBoxPaidForSomeone.setChecked(mTransaction.paidForSomeone);
            mButtonSave.setText(R.string.update_transaction);
            mButtonDelete.setVisibility(VISIBLE);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);
        mEditTextAmount.requestFocus();
        // show keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mEditTextNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    List<Transaction> trans = Transaction.getDao().queryBuilder()
                            .where()
                            .like(Transaction.Columns.LOCATION, "%" + s.toString() + "%")
                            .query();
                    HashSet<String> unique = new HashSet<>();
                    for (Transaction t : trans) {
                        unique.add(t.location);
                    }
                    for (String location : unique) {
                        Log.v(EditTransactionFragment.this.getClass().getSimpleName(), "Found similar location to " + s.toString() + " - " + location);
                    }
                    List<String> finalUgh = new ArrayList<>(unique);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, finalUgh);
                    mEditTextNotes.setAdapter(adapter);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.fragment_edit_transaction_button_add_transaction)
    protected void addTransaction() {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                if (mEditingTransaction) {
                    mBudget.cachedValue -= mTransaction.getTotalAmount(); // this will be undone later.
                }
                mTransaction.paidForSomeone = mCheckBoxPaidForSomeone.isChecked();
                mTransaction.date = new Date(mDatePicker.getCalendarView().getDate());

                mTransaction.amount = -1 * Double.valueOf(mEditTextAmount.getText().toString());
                if (mTransaction.paidForSomeone) {
                    mTransaction.amountOther = -1 * Double.valueOf(mEditTextAmountOther.getText().toString());
                } else {
                    mTransaction.amountOther = 0;
                }
                mTransaction.location = mEditTextNotes.getText().toString();
                if (mEditingTransaction) {
                    mTransaction.update();
                } else {
                    mBudget.transactions.add(mTransaction);
                }
                mBudget.cachedValue += mTransaction.getTotalAmount();
                mBudget.cachedDate = new Date();
                mBudget.update();
                mEventBus.post(new BudgetUpdatedEvent(mBudget));
            }
        }, new Runnable() {
            @Override
            public void run() {
                Util.toast("Transaction Saved");
                dismissAllowingStateLoss();
            }
        });
    }

    @OnClick(R.id.fragment_edit_transaction_button_delete_transaction)
    protected void deleteTransaction() {
        mTransaction.delete();
        mBudget.transactions.remove(mTransaction);
        mBudget.cachedValue -= mTransaction.getTotalAmount();
        mBudget.updateAsync(new DatabaseOperationFinishedCallback() {
            @Override
            public void onDatabaseOperationFinished(int rows) {
                if (rows > 0) {
                    mEventBus.post(new BudgetUpdatedEvent(mBudget));
                    Util.toast("Transaction Deleted");
                    dismissAllowingStateLoss();
                } else {
                    Util.toast("Delete Failed");
                }
            }
        });
    }

    @OnCheckedChanged(R.id.fragment_edit_transaction_checkbox_paidforsomeone)
    protected void checkedPaidForSomeone() {
        mLayoutOther.setVisibility(mCheckBoxPaidForSomeone.isChecked() ? VISIBLE : GONE);
    }
}
