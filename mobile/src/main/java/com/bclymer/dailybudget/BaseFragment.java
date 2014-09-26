package com.bclymer.dailybudget;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by bclymer on 9/26/2014.
 */
public class BaseFragment extends Fragment {

    protected int mLayoutId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view;
        if (mLayoutId != -1) {
            view = inflater.inflate(mLayoutId, container, false);
            ButterKnife.inject(this, view);
        } else {
            return null;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLayoutId != -1) {
            ButterKnife.reset(this);
        }
    }
}
