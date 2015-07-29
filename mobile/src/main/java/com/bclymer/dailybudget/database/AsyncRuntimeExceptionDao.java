package com.bclymer.dailybudget.database;

import com.bclymer.dailybudget.utilities.ThreadManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bclymer on 9/26/2014.
 */
public class AsyncRuntimeExceptionDao<T, ID> extends RuntimeExceptionDao<T, ID> {

    public AsyncRuntimeExceptionDao(Dao<T, ID> dao) {
        super(dao);
    }

    public void createAsync(T data) {
        createAsync(data, null);
    }

    public void createOrUpdateAsync(T data) {
        createOrUpdateAsync(data, null);
    }

    public void updateAsync(T data) {
        updateAsync(data, null);
    }

    public void deleteAsync(T data) {
        deleteAsync(data, null);
    }

    public int tryRefresh(T data) {
        try {
            return super.refresh(data);
        } catch (RuntimeException ex) {
            return -1;
        }
    }

    public void createAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        final AtomicInteger rows = new AtomicInteger(-1);
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                rows.set(create(data));
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished(rows.get());
                }
            }
        });
    }

    public void createOrUpdateAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        final AtomicInteger rows = new AtomicInteger(-1);
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                rows.set(createOrUpdate(data).getNumLinesChanged());
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished(rows.get());
                }
            }
        });
    }

    public void updateAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        final AtomicInteger rows = new AtomicInteger(-1);
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                rows.set(update(data));
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished(rows.get());
                }
            }
        });
    }

    public void deleteAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        final AtomicInteger rows = new AtomicInteger(-1);
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                rows.set(delete(data));
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished(rows.get());
                }
            }
        });
    }

    public interface DatabaseOperationFinishedCallback {
        void onDatabaseOperationFinished(int rows);
    }
}
