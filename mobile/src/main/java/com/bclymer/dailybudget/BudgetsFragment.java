package com.bclymer.dailybudget;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BudgetsFragment extends BaseFragment {

    @InjectView(android.R.id.list)
    protected AbsListView mListView;

    private BudgetAdapter mBudgetAdapter;
    private List<Budget> mBudgetList;

    private BudgetSelectedCallback mCallback;

    public static Fragment newInstance() {
        return new BudgetsFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BudgetSelectedCallback)) {
            throw new RuntimeException("Activity " + activity + " must implement BudgetSelectedCallback to display BudgetsFragment");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_budgets;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Loading indicator. Use Loader to load all budgets from DB. Maybe RxJava?
        mBudgetList = Budget.getDao().queryForAll();
        mBudgetAdapter = new BudgetAdapter();
        mListView.setAdapter(mBudgetAdapter);
    }

    @OnItemClick(android.R.id.list)
    protected void onBudgetClick(int position) {
        mCallback.onBudgetSelected(mBudgetList.get(position).id);
    }

    private class BudgetAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBudgetList != null ? mBudgetList.size() : 0;
        }

        @Override
        public Budget getItem(int position) {
            return mBudgetList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            return null;
        }
    }

    public interface BudgetSelectedCallback {
        public void onBudgetSelected(int budgetId);
    }
}
