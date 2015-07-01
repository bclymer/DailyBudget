package com.bclymer.dailybudget.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.bclymer.dailybudget.R;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.bclymer.dailybudget.utilities.ThreadManager;
import com.bclymer.dailybudget.views.TransactionView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by bclymer on 9/28/2014.
 */
public class BudgetTransactionsFragment extends BaseDialogFragment {

    public static final String TAG = "BudgetTransactionsFragment";

    private static final String EXTRA_BUDGET_ID = "extra_budget_id";

    @InjectView(android.R.id.list)
    protected AbsListView mListView;
    @InjectView(android.R.id.empty)
    protected ViewGroup mEmptyView;

    private TransactionAdapter mAdapter;
    private List<Transaction> mTransactionList;

    private int mBudgetId;

    public BudgetTransactionsFragment() {
    }

    public static BudgetTransactionsFragment newInstance(int budgetId) {
        BudgetTransactionsFragment fragment = new BudgetTransactionsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_BUDGET_ID, budgetId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = R.layout.fragment_budget_transactions;
        mBudgetId = getArguments().getInt(EXTRA_BUDGET_ID);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Budget budget = Budget.getDao().queryForId(mBudgetId);
        getDialog().setTitle(budget.name);
        mTransactionList = budget.getSortedTransactions();
        mAdapter = new TransactionAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);
        mEventBus.register(this);
    }

    @Override
    public void onDestroyView() {
        mEventBus.unregister(this);
        super.onDestroyView();
    }

    @OnClick(R.id.fragment_budget_transactions_button_new_transaction)
    protected void clickedAddTransaction() {
        EditTransactionFragment.newInstance(mBudgetId).show(getFragmentManager(), EditTransactionFragment.TAG);
    }

    @OnClick(R.id.fragment_budget_transactions_button_stats)
    protected void clickedBudgetStats() {
        BudgetStatsFragment.newInstance(mBudgetId).show(getFragmentManager(), BudgetStatsFragment.TAG);
    }

    public void onEvent(final BudgetUpdatedEvent event) {
        if (event.budget.id != mBudgetId) return;

        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                mTransactionList = event.budget.getSortedTransactions();
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (!isVisible() || mListView == null) return;

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private class TransactionAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        public TransactionAdapter() {
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mTransactionList != null ? mTransactionList.size() : 0;
        }

        @Override
        public Transaction getItem(int position) {
            return mTransactionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Transaction transaction = mTransactionList.get(position);
            final TransactionView transactionView = TransactionView.createTransactionView(mInflater, (TransactionView) convertView, parent, transaction);
            transactionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTransactionFragment.newInstance(mBudgetId, transaction.id).show(getFragmentManager(), EditTransactionFragment.TAG);
                }
            });
            return transactionView;
        }
    }
}
