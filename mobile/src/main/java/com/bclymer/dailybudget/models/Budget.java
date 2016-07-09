package com.bclymer.dailybudget.models;

import com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao;
import com.bclymer.dailybudget.database.DatabaseManager;
import com.bclymer.dailybudget.database.DatabaseResource;
import com.bclymer.dailybudget.events.BudgetUpdatedEvent;
import com.bclymer.dailybudget.utilities.Util;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.greenrobot.event.EventBus;

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

    public BudgetStats budgetStats;

    public static Budget createBudget() {
        Budget budget = new Budget();
        budget.name = "New Budget";
        budget.amountPerPeriod = 10.0;
        budget.periodLengthInDays = 1;
        budget.cachedValue = 0.0;
        budget.cachedDate = new Date();
        budget.transactions = getDao().getEmptyForeignCollection(Columns.TRANSACTIONS);
        return budget;
    }

    public static AsyncRuntimeExceptionDao<Budget, Integer> getDao() {
        return DatabaseManager.getBaseDao(Budget.class, Integer.class);
    }

    public static List<Transaction> getSortedTransactions(int budgetId) {
        try {
            return Transaction.getDao().queryBuilder()
                    .orderBy(Transaction.Columns.DATE, false)
                    .where()
                    .eq(Transaction.Columns.FOREIGN_BUDGET, budgetId)
                    .query();
        } catch (SQLException e) {
            return null;
        }
    }

    public void generateStats() {
        if (budgetStats == null) {
            budgetStats = new BudgetStats(this);
        }
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

    public void updateCache() {
        final Date today = new Date();
        if (Util.isSameDay(today, cachedDate)) return;

        long days = Util.getDaysBetweenDates(cachedDate, today);
        cachedValue += days * amountPerPeriod;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(cachedDate);
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Transaction transaction = new Transaction(calendar.getTime(), amountPerPeriod);
            transaction.budget = Budget.this;
            transactions.add(transaction);
        }
        cachedDate = today;
        if (update() > 0) {
            EventBus.getDefault().post(new BudgetUpdatedEvent(this, false));
        }
    }

    public List<Transaction> getSortedTransactions() {
        return getSortedTransactions(id);
    }

    public void cloneInto(Budget budget) {
        budget.cachedValue = cachedValue;
        budget.cachedDate = cachedDate;
        budget.transactions = transactions;
        budget.amountPerPeriod = amountPerPeriod;
        budget.periodLengthInDays = periodLengthInDays;
        budget.name = name;
    }

    public static final class Columns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String AMOUNT_PER_PERIOD = "amount_per_period";
        public static final String PERIOD_LENGTH_IN_DAYS = "period_length";
        public static final String CACHED_VALUE = "cached_value";
        public static final String CACHED_DATE = "cached_date";
        public static final String TRANSACTIONS = "transactions";
    }

}
