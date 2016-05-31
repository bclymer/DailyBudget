package com.bclymer.dailybudget.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife

/**
 * Created by bclymer on 9/26/2014.
 */
abstract class BaseFragment : Fragment() {

    protected var mLayoutId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View
        if (mLayoutId != -1) {
            view = inflater.inflate(mLayoutId, container, false)
        } else {
            return null
        }
        return view
    }

    override fun onDestroyView() {
        if (mLayoutId != -1) {
            ButterKnife.unbind(this)
        }
        super.onDestroyView()
    }
}
