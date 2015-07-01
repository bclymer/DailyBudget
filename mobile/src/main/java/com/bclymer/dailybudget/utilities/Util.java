package com.bclymer.dailybudget.utilities;

import android.widget.Toast;

import com.bclymer.dailybudget.BudgetApplication;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by bclymer on 9/28/2014.
 */
public class Util {

    public static void toast(final String text, final int duration) {
        ThreadManager.runOnUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BudgetApplication.getApplication(), text, duration).show();
            }
        });
    }

    public static void toast(int recId, int duration) {
        String text = BudgetApplication.getApplication().getResources().getString(recId);
        toast(text, duration);
    }

    public static void toast(final String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    public static void toast(int recId) {
        String text = BudgetApplication.getApplication().getResources().getString(recId);
        toast(text, Toast.LENGTH_SHORT);
    }

    public static boolean isSameDay(Date day1, Date day2) {
        Calendar day1Cal = new GregorianCalendar();
        day1Cal.setTime(day1);
        Calendar day2Cal = new GregorianCalendar();
        day2Cal.setTime(day2);
        return day1Cal.get(Calendar.YEAR) == day2Cal.get(Calendar.YEAR) &&
                day1Cal.get(Calendar.DAY_OF_YEAR) == day2Cal.get(Calendar.DAY_OF_YEAR);
    }

    public static long getDaysBetweenDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return Long.MIN_VALUE;
        }
        Calendar startCal = new GregorianCalendar();
        startCal.setTime(startDate);
        Calendar endCal = new GregorianCalendar();
        endCal.setTime(endDate);
        return getDaysBetweenDates(startCal, endCal);
    }

    public static long getDaysBetweenDates(Calendar startCal, Calendar endCal) {
        if (startCal == null || endCal == null) {
            return Long.MIN_VALUE;
        }
        setCalendarToBeginningOfDay(startCal);
        setCalendarToBeginningOfDay(endCal);
        endCal.set(Calendar.MINUTE, 1); // make sure it's 1 minute past, in case seconds make a difference.
        long diff = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static void setCalendarToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static String makeLikeMoney(double value) {
        return NumberFormat.getCurrencyInstance().format(value);
    }

}
