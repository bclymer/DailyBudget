package com.bclymer.dailybudget.utilities;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bclymer on 9/26/2014.
 */
public class ThreadManager {

    private static final String TAG = ThreadManager.class.getSimpleName();

    private static final int MAX_POOL_SIZE = 16;
    private static final int KEEP_ALIVE = 1;
    private static final AtomicInteger threadId = new AtomicInteger();
    private static final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<>();
    private static final Thread.UncaughtExceptionHandler mExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(TAG, "uncaughtException", ex);
        }
    };
    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("ThreadManager-" + threadId.getAndIncrement());
            t.setUncaughtExceptionHandler(mExceptionHandler);
            return t;
        }
    };
    private static final ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, mPoolWorkQueue, mThreadFactory);
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Can be used to modify properties of mHandler, mExecutor, or any of the private vars in this class.
     */
    static {
        mExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * Run bits on the UI thread
     *
     * @param runnable bits to run on the UI thread
     */
    public static void runOnUi(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    /**
     * Run bits on a background thread
     *
     * @param runnable bits to run in the background
     */
    public static void runInBackground(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * Run some bits on the UI thread after a non-blocking delay.
     *
     * @param runnable bits to run on the UI
     * @param delayMs  time in ms to wait before running UI bits
     */
    public static void delayOnMainThread(final Runnable runnable, final int delayMs) {
        mHandler.postDelayed(runnable, delayMs);
    }

    /**
     * Run some bits on the UI thread after the current UI queue is finished.
     *
     * @param runnable bits to run on the UI
     */
    public static void postToMainThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    /**
     * Runs bits on a background thread, then different bits on the UI thread.
     *
     * @param backgroundRunnable bits to run in background
     * @param uiRunnable         bits to run on UI after background bits are done.
     */
    public static void runInBackgroundThenUi(final Runnable backgroundRunnable, final Runnable uiRunnable) {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                backgroundRunnable.run();
                runOnUi(uiRunnable);
            }
        });
    }

    public static ThreadPoolExecutor newThreadExecutor(int numberOfThreads) {
        return new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

}
