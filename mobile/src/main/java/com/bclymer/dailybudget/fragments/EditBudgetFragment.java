package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.utilities.Util;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao.DatabaseOperationFinishedCallback;

/**
 * Created by bclymer on 9/26/2014.
 */
public class EditBudgetFragment extends BaseFragment {

    public static final String TAG = "EditBudgetFragment";
    public static final int NO_BUDGET_ID_VALUE = -1;

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    @InjectView(R.id.fragment_edit_budget_name)
    protected TextView mEditTextName;
    @InjectView(R.id.fragment_edit_budget_numberpicker_duration)
    protected EditText mEditTextDuration;
    @InjectView(R.id.fragment_edit_budget_numberpicker_amount)
    protected EditText mEditTextAmount;

    private int mBudgetId = NO_BUDGET_ID_VALUE;
    private Budget mBudget;

    public static EditBudgetFragment newInstanceForNewBudget() {
        return newInstance(NO_BUDGET_ID_VALUE);
    }

    public static EditBudgetFragment newInstance(int budgetId) {
        EditBudgetFragment fragment = new EditBudgetFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_edit_budget;
        if (getArguments() != null) {
            mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID, NO_BUDGET_ID_VALUE);
        }
        if (mBudgetId != NO_BUDGET_ID_VALUE) {
            mBudget = Budget.getDao().queryForId(mBudgetId); // should be very very fast. Can run on main thread.
        } else {
            mBudget = Budget.createBudget();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditTextName.setText(mBudget.name);
        mEditTextDuration.setText(Double.toString(mBudget.amountPerPeriod));
        mEditTextAmount.setText(Double.toString(mBudget.periodLengthInDays));
    }

    public boolean hasUnsavedContent() {
        return Double.valueOf(mEditTextAmount.getText().toString()) != mBudget.amountPerPeriod ||
                Integer.valueOf(mEditTextDuration.getText().toString()) != mBudget.periodLengthInDays ||
                !mEditTextName.getText().toString().equals(mBudget.name);
    }

    @OnClick(R.id.fragment_edit_budget_button_save)
    protected void onSave() {
        saveChanges();
    }

    public void saveChanges() {
        mBudget.amountPerPeriod = Double.valueOf(mEditTextAmount.getText().toString());
        mBudget.periodLengthInDays = Integer.valueOf(mEditTextDuration.getText().toString());
        mBudget.name = mEditTextName.getText().toString();
        mBudget.createOrUpdateAsync(new DatabaseOperationFinishedCallback() {
            @Override
            public void onDatabaseOperationFinished(int rows) {
                if (rows > 0) {
                    Util.toast("Save Successful");
                } else {
                    Util.toast("Save Failed");
                }
            }
        });
    }
}
