package com.bclymer.dailybudget.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import static com.bclymer.dailybudget.models.Transaction.Columns.AMOUNT;
import static com.bclymer.dailybudget.models.Transaction.Columns.DATE;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Transaction {

    public Transaction(Date date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    @DatabaseField(columnName = DATE)
    public Date date;

    @DatabaseField(columnName = AMOUNT)
    public double amount;

    public static final class Columns {
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
    }

}
