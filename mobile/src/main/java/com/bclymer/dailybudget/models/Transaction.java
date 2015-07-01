package com.bclymer.dailybudget.models;

import com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao;
import com.bclymer.dailybudget.database.DatabaseHelper;
import com.bclymer.dailybudget.database.DatabaseResource;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import static com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT;
import static com.bclymer.dailybudget.models.Transaction.Columns.DATE;
import static com.bclymer.dailybudget.models.Transaction.Columns.FOREIGN_BUDGET;
import static com.bclymer.dailybudget.models.Transaction.Columns.ID;
import static com.bclymer.dailybudget.models.Transaction.Columns.NOTES;
import static com.bclymer.dailybudget.models.Transaction.Columns.PAID_FOR_SOMEONE;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Transaction extends DatabaseResource<Transaction, Integer> {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    public int id;
    @DatabaseField(columnName = DATE)
    public Date date;
    @DatabaseField(columnName = AMOUNT)
    public double amount;
    @DatabaseField(columnName = NOTES)
    public String notes;
    @DatabaseField(columnName = PAID_FOR_SOMEONE)
    public boolean paidForSomeone;
    @DatabaseField(columnName = FOREIGN_BUDGET, foreign = true)
    public Budget budget;

    public Transaction() {
    }

    public Transaction(Date date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public static AsyncRuntimeExceptionDao<Transaction, Integer> getDao() {
        return DatabaseHelper.getBaseDao(Transaction.class, Integer.class);
    }

    public static final class Columns {
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
        public static final String NOTES = "notes";
        public static final String PAID_FOR_SOMEONE = "paid_for_someone";
        public static final String FOREIGN_BUDGET = "budget_id";
    }

}
