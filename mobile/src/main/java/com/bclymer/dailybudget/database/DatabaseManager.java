package com.bclymer.dailybudget.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bclymer.dailybudget.models.Budget;
import com.bclymer.dailybudget.models.Transaction;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bclymer on 9/26/2014.
 */
public class DatabaseManager extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "dailybudget.db";
    private static final int DATABASE_VERSION = 4;

    private static final Class[] tableClasses = new Class[]{
            Budget.class,
            Transaction.class,
    };

    private static DatabaseManager mInstance;
    private final Map<Class, AsyncRuntimeExceptionDao> mDaos = new HashMap<>();

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void init(Context ctx) {
        mInstance = new DatabaseManager(ctx);
    }

    /**
     * @see //getRuntimeExceptionDao()
     */
    public static <D extends AsyncRuntimeExceptionDao<T, ?>, T> D getBaseDao(Class<T> clazz) {
        return getBaseDao(clazz, null);
    }

    /**
     * @see //getRuntimeExceptionDao()
     * Second param is purely so java can infer a type.
     */
    public static <D extends AsyncRuntimeExceptionDao<T, ?>, T, K> D getBaseDao(Class<T> clazz, Class<K> idTypeClazz) {
        try {
            if (mInstance.mDaos.containsKey(clazz)) {
                return (D) mInstance.mDaos.get(clazz);
            }
            Dao<T, K> dao = mInstance.getDao(clazz);
            @SuppressWarnings({"unchecked", "rawtypes"})
            D castDao = (D) new AsyncRuntimeExceptionDao(dao);
            mInstance.mDaos.put(clazz, castDao);
            return castDao;
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Could not create BaseDao for class " + clazz, e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        createDatabase(connectionSource);
    }

    private void createDatabase(ConnectionSource connectionSource) {
        try {
            for (Class tableClass : tableClasses) {
                TableUtils.createTable(connectionSource, tableClass);
            }
        } catch (SQLException | java.sql.SQLException e) {
            Log.e(DatabaseManager.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE `transaction` ADD COLUMN `" + Transaction.Columns.PAID_FOR_SOMEONE + "` BOOLEAN NOT NULL DEFAULT 0");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE `transaction` ADD COLUMN `" + Transaction.Columns.AMOUNT_OTHER + "` INTEGER NOT NULL DEFAULT 0");
        }
    }
}
