package com.bclymer.dailybudget.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.views.BudgetView;
import com.bclymer.dailybudget.R;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnItemClick;

import static android.view.View.OnClickListener;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BudgetsFragment extends BaseFragment {

    @InjectView(android.R.id.list)
    protected AbsListView mListView;
    @InjectView(android.R.id.empty)
    protected ViewGroup mEmptyView;

    private BudgetAdapter mAdapter;
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
        } else {
            mCallback = (BudgetSelectedCallback) activity;
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
        mAdapter = new BudgetAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);
        mEventBus.register(this);
    }

    @Override
    public void onDestroyView() {
        mEventBus.unregister(this);
        super.onDestroyView();
    }

    @OnItemClick(android.R.id.list)
    protected void onBudgetClick(int position) {
        mCallback.onBudgetSelected(mBudgetList.get(position).id);
    }

    public void onEventMainThread(BudgetUpdatedEvent event) {
        for (Budget budget : mBudgetList) {
            if (budget.id == event.budgetId) {
                budget.refresh();
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private class BudgetAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public BudgetAdapter() {
            mInflater = getActivity().getLayoutInflater();
        }

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
            final Budget budget = mBudgetList.get(position);
            BudgetView budgetView = BudgetView.createBudgetView(mInflater, (BudgetView) view, viewGroup, budget);

            budgetView.setOnAddTransactionClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddTransactionFragment.newInstance(budget.id).show(getFragmentManager(), AddTransactionFragment.TAG);
                }
            });
            budgetView.setOnEditClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onBudgetSelected(budget.id);
                }
            });
            budgetView.setOnBudgetClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BudgetTransactionsFragment.newInstance(budget.id).show(getFragmentManager(), BudgetTransactionsFragment.TAG);
                }
            });

            return budgetView;
        }
    }

    public interface BudgetSelectedCallback {
        public void onBudgetSelected(int budgetId);
    }
}
