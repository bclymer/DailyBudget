package com.bclymer.dailybudget;

import android.os.Bundle;
import android.view.View;

/**
 * Created by bclymer on 9/26/2014.
 */
public class EditBudgetFragment extends BaseFragment {

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    private int mBudgetId = -1;
    private Budget mBudget;

    public static EditBudgetFragment newInstanceForNewBudget() {
        return new EditBudgetFragment();
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
            mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID, -1);
        }
        if (mBudgetId != -1) {
            mBudget = Budget.getDao().queryForId(mBudgetId); // should be very very fast.
        } else {
            mBudget = new Budget();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Display Budget
    }
}
