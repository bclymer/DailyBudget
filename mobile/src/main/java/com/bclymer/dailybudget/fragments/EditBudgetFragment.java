package com.bclymer.dailybudget.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.Util;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao.DatabaseOperationFinishedCallback;

/**
 * Created by bclymer on 9/26/2014.
 */
public class EditBudgetFragment extends BaseFragment {

    public static final String TAG = "EditBudgetFragment";
    public static final int NO_BUDGET_ID_VALUE = -1;

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    @Bind(R.id.fragment_edit_budget_name)
    protected TextView mEditTextName;
    @Bind(R.id.fragment_edit_budget_edittext_duration)
    protected EditText mEditTextDuration;
    @Bind(R.id.fragment_edit_budget_edittext_amount)
    protected EditText mEditTextAmount;
    @Bind(R.id.fragment_edit_budget_button_delete)
    protected Button mButtonDelete;

    private Budget mBudget;
    private int mBudgetId = NO_BUDGET_ID_VALUE;

    private BudgetDoneEditingCallback mCallback;
    private boolean mNewBudget = false;

    public static EditBudgetFragment newInstance(int budgetId) {
        EditBudgetFragment fragment = new EditBudgetFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BudgetDoneEditingCallback)) {
            throw new RuntimeException("Activity " + activity + " must implement BudgetDoneEditingCallback to display EditBudgetFragment");
        } else {
            mCallback = (BudgetDoneEditingCallback) activity;
        }
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
            mNewBudget = true;
            mBudget = Budget.createBudget();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditTextName.setText(mBudget.name);
        mEditTextDuration.setText(Integer.toString(mBudget.periodLengthInDays));
        mEditTextAmount.setText(Double.toString(mBudget.amountPerPeriod));
        if (!mNewBudget) {
            mButtonDelete.setVisibility(VISIBLE);
        }
    }

    public boolean hasUnsavedContent() {
        return Double.valueOf(mEditTextAmount.getText().toString()) != mBudget.amountPerPeriod ||
                Integer.valueOf(mEditTextDuration.getText().toString()) != mBudget.periodLengthInDays ||
                !mEditTextName.getText().toString().equals(mBudget.name);
    }

    public String getBudgetName() {
        return mBudget.name;
    }

    @OnClick(R.id.fragment_edit_budget_button_save)
    protected void onSave() {
        saveChanges();
    }

    @OnClick(R.id.fragment_edit_budget_button_delete)
    protected void onDelete() {
        mBudget.delete();
        mCallback.onBudgetDoneEditing();
        mEventBus.post(new BudgetUpdatedEvent(mBudget, true));
        Util.toast("Delete Successful");
    }

    public void saveChanges() {
        mBudget.amountPerPeriod = Double.valueOf(mEditTextAmount.getText().toString());
        mBudget.periodLengthInDays = Integer.valueOf(mEditTextDuration.getText().toString());
        mBudget.name = mEditTextName.getText().toString();
        if (mNewBudget) {
            mBudget.create();
            Transaction transaction = new Transaction(new Date(), mBudget.amountPerPeriod);
            transaction.budget = mBudget;
            mBudget.transactions.add(transaction);
            mBudget.cachedValue = mBudget.amountPerPeriod;
        }
        mBudget.updateAsync(new DatabaseOperationFinishedCallback() {
            @Override
            public void onDatabaseOperationFinished(int rows) {
                if (rows > 0) {
                    Util.toast("Save Successful");
                    mEventBus.post(new BudgetUpdatedEvent(mBudget));
                    mCallback.onBudgetDoneEditing();
                } else {
                    Util.toast("Save Failed");
                }
            }
        });
    }

    public interface BudgetDoneEditingCallback {
        public void onBudgetDoneEditing();
    }
}
