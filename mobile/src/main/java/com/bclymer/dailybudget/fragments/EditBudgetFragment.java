package com.bclymer.dailybudget.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.database.BudgetRepository;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.utilities.Util;

import butterknife.Bind;
import butterknife.OnClick;
import kotlin.Unit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.view.View.VISIBLE;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof BudgetDoneEditingCallback)) {
            throw new RuntimeException("Activity " + context + " must implement BudgetDoneEditingCallback to display EditBudgetFragment");
        } else {
            mCallback = (BudgetDoneEditingCallback) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_edit_budget;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID, NO_BUDGET_ID_VALUE);
        }
        if (mBudgetId != NO_BUDGET_ID_VALUE) {
            mButtonDelete.setVisibility(VISIBLE);
            BudgetRepository.INSTANCE.getBudget(mBudgetId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Budget>() {
                        @Override
                        public void call(Budget budget) {
                            configureWithBudget(budget);
                        }
                    });
        } else {
            mNewBudget = true;
            configureWithBudget(Budget.createBudget());
        }
    }

    private void configureWithBudget(Budget budget) {
        mEditTextName.setText(budget.name);
        mEditTextDuration.setText(Integer.toString(budget.periodLengthInDays));
        mEditTextAmount.setText(Double.toString(budget.amountPerPeriod));
    }

    @OnClick(R.id.fragment_edit_budget_button_save)
    protected void onSave() {
        saveChanges();
    }

    @OnClick(R.id.fragment_edit_budget_button_delete)
    protected void onDelete() {
        // I eventually need to refactor the events system so I don't need all this jank.
        BudgetRepository.INSTANCE.getBudget(mBudgetId)
                .flatMap(new Func1<Budget, Observable<Budget>>() {
                    @Override
                    public Observable<Budget> call(final Budget budget) {
                        return BudgetRepository.INSTANCE.deleteBudget(mBudgetId).map(new Func1<Unit, Budget>() {
                            @Override
                            public Budget call(Unit unit) {
                                return budget;
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Budget>() {
                    @Override
                    public void call(Budget budget) {
                        mCallback.onBudgetDoneEditing();
                        mEventBus.post(new BudgetUpdatedEvent(budget, true));
                        Util.toast("Delete Successful");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Util.toast("Delete Failed");
                    }
                });
    }

    public void saveChanges() {
        double amountPerPeriod = Double.valueOf(mEditTextAmount.getText().toString());
        int periodLengthInDays = Integer.valueOf(mEditTextDuration.getText().toString());
        String name = mEditTextName.getText().toString();
        if (mNewBudget) {
            BudgetRepository.INSTANCE.createBudget(name, amountPerPeriod, periodLengthInDays)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Budget>() {
                        @Override
                        public void call(Budget budget) {
                            Util.toast("Save Successful");
                            mEventBus.post(new BudgetUpdatedEvent(budget));
                            mCallback.onBudgetDoneEditing();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Util.toast("Save Failed");
                        }
                    });
        } else {
            BudgetRepository.INSTANCE.updateBudget(mBudgetId, name, amountPerPeriod, periodLengthInDays)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Budget>() {
                        @Override
                        public void call(Budget budget) {
                            Util.toast("Save Successful");
                            mEventBus.post(new BudgetUpdatedEvent(budget));
                            mCallback.onBudgetDoneEditing();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Util.toast("Save Failed");
                        }
                    });
        }
    }

    public interface BudgetDoneEditingCallback {
        void onBudgetDoneEditing();
    }
}
