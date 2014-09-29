package com.bclymer.dailybudget.utilities;

import android.widget.Toast;

import com.bclymer.dailybudget.BudgetApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        setCalendarToBeginningOfDay(day1Cal);
        Calendar day2Cal = new GregorianCalendar();
        day2Cal.setTime(day2);
        setCalendarToBeginningOfDay(day2Cal);
        return day1Cal.get(Calendar.YEAR) == day2Cal.get(Calendar.YEAR) &&
                day1Cal.get(Calendar.DAY_OF_YEAR) == day2Cal.get(Calendar.DAY_OF_YEAR);
    }

    public static long getDaysBetweenDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return Long.MIN_VALUE;
        }
        Calendar startCal = new GregorianCalendar();
        startCal.setTime(startDate);
        setCalendarToBeginningOfDay(startCal);
        Calendar endCal = new GregorianCalendar();
        endCal.setTime(endDate);
        setCalendarToBeginningOfDay(endCal);
        return getDaysBetweenDates(startCal, endCal);
    }

    public static long getDaysBetweenDates(Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null) {
            return Long.MIN_VALUE;
        }
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static void setCalendarToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
    }

}
