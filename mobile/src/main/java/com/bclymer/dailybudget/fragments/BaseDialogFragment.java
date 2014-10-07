package com.bclymer.dailybudget.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by bclymer on 9/26/2014.
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected EventBus mEventBus;
    protected int mLayoutId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
    }

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
