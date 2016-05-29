package com.bclymer.dailybudget.models;

import com.bclymer.dailybudget.database.AsyncRuntimeExceptionDao;
import com.bclymer.dailybudget.database.DatabaseManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import io.realm.RealmObject;

import static com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT;
import static com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT_OTHER;
import static com.bclymer.dailybudget.models.Transaction.Columns.DATE;
import static com.bclymer.dailybudget.models.Transaction.Columns.FOREIGN_BUDGET;
import static com.bclymer.dailybudget.models.Transaction.Columns.ID;
import static com.bclymer.dailybudget.models.Transaction.Columns.LOCATION;
import static com.bclymer.dailybudget.models.Transaction.Columns.PAID_FOR_SOMEONE;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Transaction extends RealmObject {

    @DatabaseField(columnName = ID, generatedId = true, index = true)
    public int id;
    @DatabaseField(columnName = DATE)
    public Date date;
    @DatabaseField(columnName = AMOUNT)
    public double amount;
    @DatabaseField(columnName = AMOUNT_OTHER)
    public double amountOther;
    @DatabaseField(columnName = LOCATION)
    public String location;
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

    public static AsyncRuntimeExceptionDao<Transaction, Integer> getDao2() {
        return DatabaseManager.getBaseDao(Transaction.class, Integer.class);
    }

    public double getTotalAmount() {
        return amount + amountOther;
    }

    public static final class Columns {
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
        public static final String AMOUNT_OTHER = "amount_other";
        public static final String LOCATION = "notes";
        public static final String PAID_FOR_SOMEONE = "paid_for_someone";
        public static final String FOREIGN_BUDGET = "budget_id";
    }

}
