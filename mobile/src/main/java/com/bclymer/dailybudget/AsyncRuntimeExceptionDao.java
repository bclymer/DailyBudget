package com.bclymer.dailybudget;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

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
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                create(data);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished();
                }
            }
        });
    }

    public void createOrUpdateAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                createOrUpdate(data);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished();
                }
            }
        });
    }

    public void updateAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                update(data);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished();
                }
            }
        });
    }

    public void deleteAsync(final T data, final DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        ThreadManager.runInBackgroundThenUi(new Runnable() {
            @Override
            public void run() {
                delete(data);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (databaseOperationFinishedCallback != null) {
                    databaseOperationFinishedCallback.onDatabaseOperationFinished();
                }
            }
        });
    }
    public interface DatabaseOperationFinishedCallback {
        public void onDatabaseOperationFinished();
    }
}
