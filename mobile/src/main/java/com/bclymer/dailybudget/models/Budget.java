package com.bclymer.dailybudget.models;

import com.bclymer.dailybudget.database.DatabaseResource;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import static com.bclymer.dailybudget.models.Budget.Columns.AMOUNT_PER_PERIOD;
import static com.bclymer.dailybudget.models.Budget.Columns.CACHED_DATE;
import static com.bclymer.dailybudget.models.Budget.Columns.CACHED_VALUE;
import static com.bclymer.dailybudget.models.Budget.Columns.ID;
import static com.bclymer.dailybudget.models.Budget.Columns.PERIOD_LENGTH_IN_DAYS;
import static com.bclymer.dailybudget.models.Budget.Columns.TRANSACTIONS;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Budget extends DatabaseResource<Budget, Integer> {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    public int id;

    @DatabaseField(columnName = Columns.NAME)
    public String name;

    @DatabaseField(columnName = AMOUNT_PER_PERIOD)
    public double amountPerPeriod;

    @DatabaseField(columnName = PERIOD_LENGTH_IN_DAYS)
    public int periodLengthInDays;

    @DatabaseField(columnName = CACHED_VALUE)
    public double cachedValue;

    @DatabaseField(columnName = CACHED_DATE)
    public Date cachedDate;

    @ForeignCollectionField(columnName = TRANSACTIONS)
    public ForeignCollection<Transaction> transactions;

    public static final class Columns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String AMOUNT_PER_PERIOD = "amount_per_period";
        public static final String PERIOD_LENGTH_IN_DAYS = "period_length";
        public static final String CACHED_VALUE = "cached_value";
        public static final String CACHED_DATE = "cached_date";
        public static final String TRANSACTIONS = "transactions";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Budget budget = (Budget) o;

        return id == budget.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
