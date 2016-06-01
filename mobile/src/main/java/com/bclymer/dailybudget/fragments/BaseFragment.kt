package com.bclymer.dailybudget.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bclymer.dailybudget.extensions.subscribeOnMainThread
import com.bclymer.dailybudget.utilities.failOnBackgroundThread
import rx.Observable
import rx.Subscription

/**
 * Created by bclymer on 9/26/2014.
 */
abstract class BaseFragment(@LayoutRes private val layoutId: Int) : Fragment() {

    private companion object {
        var subscriberId = 0
        var subActiveCount = 0
    }

    private var subscriptionMap = SparseArray<Subscription>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View
        if (layoutId != -1) {
            view = inflater.inflate(layoutId, container, false)
        } else {
            return null
        }
        return view
    }


    fun <T> Observable<T>.subscribeOnLifecycle(onNext: ((T) -> Unit)? = null, onError: ((Throwable) -> Unit)? = null, onCompleted: (() -> Unit)? = null): Subscription {
        failOnBackgroundThread("subscribeOnLifecycle must be called on the main thread.")
        val currentId = subscriberId
        this.doOnUnsubscribe {
            subActiveCount -= 1
            subscriptionMap.remove(currentId)
        }
        val mainThreadObs = subscribeOnMainThread(onNext = onNext, onError = { error ->
            if (onError != null) {
                onError(error)
            }
            subActiveCount -= 1
            subscriptionMap.remove(currentId)
        }, onCompleted = {
            if (onCompleted != null) {
                onCompleted()
            }
            subActiveCount -= 1
            subscriptionMap.remove(currentId)
        })
        subscriberId += 1
        subActiveCount += 1
        subscriptionMap.put(currentId, mainThreadObs)
        return mainThreadObs
    }
}
