package com.bclymer.dailybudget.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bclymer on 9/26/2014.
 */
public class Budget extends RealmObject {

    @PrimaryKey
    public int id;
    public String name;
    public double amountPerPeriod;
    public int periodLengthInDays;
    public double cachedValue;
    public Date cachedDate;
    public RealmList<Transaction> transactions;

}
