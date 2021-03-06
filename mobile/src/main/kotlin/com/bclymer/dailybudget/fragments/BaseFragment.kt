package com.bclymer.dailybudget.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.ButterKnife
import de.greenrobot.event.EventBus

/**
 * Created by bclymer on 9/26/2014.
 */
abstract class BaseFragment(private @LayoutRes val layoutId: Int = -1) : Fragment() {

    var mEventBus = EventBus.getDefault()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View
        if (layoutId != -1) {
            view = inflater.inflate(layoutId, container, false)
            ButterKnife.bind(this, view)
        } else {
            return null
        }
        return view
    }

    override fun onDestroyView() {
        if (layoutId != -1) {
            ButterKnife.unbind(this)
        }
        super.onDestroyView()
    }
}
