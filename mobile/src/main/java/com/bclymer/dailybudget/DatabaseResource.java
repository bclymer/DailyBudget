package com.bclymer.dailybudget;

import com.j256.ormlite.dao.Dao;

import static com.bclymer.dailybudget.AsyncRuntimeExceptionDao.DatabaseOperationFinishedCallback;

/**
 * Created by bclymer on 9/28/2014.
 */
public abstract class DatabaseResource<T, ID> {

    private AsyncRuntimeExceptionDao<T, ID> baseDao;
    public AsyncRuntimeExceptionDao<T, ID> getBaseDao() {
        if (baseDao == null) {
            baseDao = (AsyncRuntimeExceptionDao<T, ID>) DatabaseHelper.getBaseDao(this.getClass());
        }
        return baseDao;
    }

    public int update() {
        return getBaseDao().update((T) this);
    }

    public int create() {
        return getBaseDao().create((T) this);
    }

    public Dao.CreateOrUpdateStatus createOrUpdate() {
        return getBaseDao().createOrUpdate((T) this);
    }

    public int delete() {
        return getBaseDao().delete((T) this);
    }

    public int refresh() {
        return getBaseDao().refresh((T) this);
    }

    public int tryRefresh() {
        return getBaseDao().tryRefresh((T) this);
    }

    public void updateAsync() {
        updateAsync(null);
    }

    public void createAsync() {
        createAsync(null);
    }

    public void createOrUpdateAsync() {
        createOrUpdateAsync(null);
    }

    public void deleteAsync() {
        deleteAsync(null);
    }

    public void updateAsync(DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        getBaseDao().updateAsync((T) this, databaseOperationFinishedCallback);
    }

    public void createAsync(DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        getBaseDao().createAsync((T) this, databaseOperationFinishedCallback);
    }

    public void createOrUpdateAsync(DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        getBaseDao().createOrUpdateAsync((T) this, databaseOperationFinishedCallback);
    }

    public void deleteAsync(DatabaseOperationFinishedCallback databaseOperationFinishedCallback) {
        getBaseDao().deleteAsync((T) this, databaseOperationFinishedCallback);
    }

}
