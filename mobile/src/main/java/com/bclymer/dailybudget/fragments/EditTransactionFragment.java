package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
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
    @Bind(R.id.fragment_edit_transaction_button_split)
    protected Button mButtonSplit;
    @Bind(R.id.fragment_edit_transaction_layout_other)
    protected ViewGroup mLayoutOther;

    private Budget mBudget;
    private Transaction mTransaction;
    private boolean mEditingTransaction = false;
    private boolean isSplit = false;

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
        setMLayoutId(R.layout.fragment_edit_transaction);

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
            isSplit = mTransaction.paidForSomeone;
            mButtonSplit.setText(isSplit ? "Merge" : "Split");
            mButtonSave.setText(R.string.update_transaction);
            mButtonDelete.setVisibility(VISIBLE);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);
        mEditTextNotes.requestFocus();
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
                            .selectColumns(
                                    Transaction.Columns.LOCATION,
                                    Transaction.Columns.AMOUNT,
                                    Transaction.Columns.AMOUNT_OTHER)
                            .where()
                            .like(Transaction.Columns.LOCATION, "%" + s.toString() + "%")
                            .query();
                    HashMap<String, Double> unique = new HashMap<>();
                    for (Transaction t : trans) {
                        if (t.location == null) continue;
                        if (unique.containsKey(t.location)) {
                            double oldValue = unique.get(t.location);
                            unique.put(t.location, oldValue + t.getTotalAmount());
                        } else {
                            unique.put(t.location, t.getTotalAmount());
                        }
                    }
                    List<Map.Entry<String, Double>> list = new ArrayList<>(unique.entrySet());
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        @Override
                        public int compare(Map.Entry<String, Double> lhs, Map.Entry<String, Double> rhs) {
                            if (lhs.getValue() < rhs.getValue()) {
                                return -1;
                            } else if (lhs.getValue() > rhs.getValue()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    List<String> finalUgh = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : list) {
                        finalUgh.add(entry.getKey());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, finalUgh);
                    mEditTextNotes.setAdapter(adapter);
                    mEditTextNotes.showDropDown();
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
                mTransaction.paidForSomeone = isSplit;
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
                getMEventBus().post(new BudgetUpdatedEvent(mBudget, false));
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
                    getMEventBus().post(new BudgetUpdatedEvent(mBudget, false));
                    Util.toast("Transaction Deleted");
                    dismissAllowingStateLoss();
                } else {
                    Util.toast("Delete Failed");
                }
            }
        });
    }

    @OnClick(R.id.fragment_edit_transaction_button_split)
    protected void splitEvenly() {
        if (isSplit) {
            double currentValue = valueFromEditText(mEditTextAmount);
            double otherValue = valueFromEditText(mEditTextAmountOther);
            double sum = (double) Math.round((currentValue + otherValue) * 100) / 100d;
            mEditTextAmount.setText(Double.toString(sum));
            mEditTextAmountOther.setText(null);
        } else {
            double currentValue = valueFromEditText(mEditTextAmount);
            double myValue = Math.ceil(currentValue * 50) / 100;
            double herValue = Math.floor(currentValue * 50) / 100;
            mEditTextAmount.setText(Double.toString(myValue));
            mEditTextAmountOther.setText(Double.toString(herValue));
        }
        isSplit = !isSplit;
        mLayoutOther.setVisibility(isSplit ? VISIBLE : GONE);
        mButtonSplit.setText(isSplit ? "Merge" : "Split");
    }

    private double valueFromEditText(EditText editText) {
        try {
            return Double.valueOf(editText.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }
}
