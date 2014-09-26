package com.bclymer.dailybudget;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import static com.bclymer.dailybudget.Budget.Columns.AMOUNT_PER_PERIOD;
import static com.bclymer.dailybudget.Budget.Columns.CACHED_VALUE;
import static com.bclymer.dailybudget.Budget.Columns.ID;
import static com.bclymer.dailybudget.Budget.Columns.PERIOD_LENGTH_IN_DAYS;
import static com.bclymer.dailybudget.Budget.Columns.TRANSACTIONS;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Budget {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    public int id;

    @DatabaseField(columnName = AMOUNT_PER_PERIOD)
    public double amountPerPeriod;

    @DatabaseField(columnName = PERIOD_LENGTH_IN_DAYS)
    public int periodLengthInDays;

    @DatabaseField(columnName = CACHED_VALUE)
    public double cachedValue;

    @ForeignCollectionField(columnName = TRANSACTIONS)
    public ForeignCollection<Transaction> transactions;

    public static AsyncRuntimeExceptionDao<Budget, Integer> getDao() {
        return DatabaseHelper.getBaseDao(Budget.class, Integer.class);
    }

    public static final class Columns {
        public static final String ID = "id";
        public static final String AMOUNT_PER_PERIOD = "amount_per_period";
        public static final String PERIOD_LENGTH_IN_DAYS = "period_length";
        public static final String CACHED_VALUE = "cached_value";
        public static final String TRANSACTIONS = "transactions";
    }

}
