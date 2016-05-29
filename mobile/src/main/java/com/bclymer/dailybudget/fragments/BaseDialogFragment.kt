package com.bclymer.dailybudget.fragments

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.ButterKnife
import de.greenrobot.event.EventBus
import kotlin.properties.Delegates

/**
 * Created by bclymer on 9/26/2014.
 */
abstract class BaseDialogFragment : DialogFragment() {

    protected var mEventBus: EventBus by Delegates.notNull()
    protected var mLayoutId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEventBus = EventBus.getDefault()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View
        if (mLayoutId != -1) {
            view = inflater.inflate(mLayoutId, container, false)
            ButterKnife.bind(this, view)
        } else {
            return null
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mLayoutId != -1) {
            ButterKnife.unbind(this)
        }
    }
}
