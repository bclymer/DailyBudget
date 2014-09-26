package com.bclymer.dailybudget;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import static com.bclymer.dailybudget.Transaction.Columns.AMOUNT;
import static com.bclymer.dailybudget.Transaction.Columns.DATE;

/**
 * Created by bclymer on 9/26/2014.
 */
@DatabaseTable
public class Transaction {

    @DatabaseField(columnName = DATE)
    public Date date;

    @DatabaseField(columnName = AMOUNT)
    public double amount;

    public static final class Columns {
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
    }

}
