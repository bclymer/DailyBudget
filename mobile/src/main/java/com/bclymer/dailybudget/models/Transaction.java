package com.bclymer.dailybudget.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bclymer on 9/26/2014.
 */
public class Transaction extends RealmObject {

    @PrimaryKey
    public int id;
    public Date date;
    public double amount;
    public double amountOther;
    public String location;
    public boolean paidForSomeone;
    public Budget budget;

    public double getTotalAmount() {
        return amount + amountOther;
    }

}
