package com.jaragua.avlmobile.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.jaragua.avlmobile.entities.Day;
import com.jaragua.avlmobile.entities.Interval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ScheduleService {

    private List<Day> days;
    private Context context;

    public ScheduleService(Context context, List<Day> days) {
        this.days = days;
        this.context = context;
    }

    public Day getToday() {
        Day.Week dayWeek = dayWeekFromDate(new Date());
        for (Day day : days) {
            if (day.getDay() == dayWeek) {
                return day;
            }
        }
        return null;
    }

    public Date findNextTime(Date now, Interval.Node node) {
        ArrayList<Date> foundDates = new ArrayList<>();
        for (Day day : days) {
            for (Interval interval : day.getIntervals()) {
                String time = (node == Interval.Node.Start) ? interval.getStart() : interval.getStop();
                Date found = dateFromWeekInterval(day.getDay(), time, now);
                foundDates.add(found);
            }
        }
        Collections.sort(foundDates);
        if (foundDates.size() >= 1) {
            return foundDates.get(0);
        }
        return null;
    }

    public Day.Week dayWeekFromDate(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        return Day.Week.values()[dayOfWeek];
    }

    public Date dateFromWeekInterval(Day.Week day, String interval, Date context) {
        Calendar calendarContext = Calendar.getInstance();
        calendarContext.setTime(context);
        String[] parts = interval.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendarContext.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendarContext.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_WEEK, day.ordinal() + 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(parts[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(parts[1]));
        calendar.set(Calendar.SECOND, Integer.valueOf(parts[2]));
        if (context.after(calendar.getTime())) {
            calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1);
        }
        return calendar.getTime();
    }

    public boolean betweenInterval() {
        boolean result = false;
        Day day = getToday();
        SimpleDateFormat dateFormat = Constants.LocationService.FORMAT_DATE;
        for (Interval interval : day.getIntervals()) {
            try {
                Date now = new Date();
                String nowString = dateFormat.format(now);
                String[] nowParts = nowString.split("T");
                Date start = dateFormat.parse(nowParts[0] + "T" + interval.getStart() + "-0500");
                Date stop = dateFormat.parse(nowParts[0] + "T" + interval.getStop() + "-0500");
                result = (now.after(start) && now.before(stop));
                if (result) break;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void schedule(Class<? extends Service> tClass) {
        if (betweenInterval()) {
            context.startService(new Intent(context.getApplicationContext(), tClass));
            Date stop = findNextTime(new Date(), Interval.Node.Stop);
            alarmManager(tClass, Interval.Node.Stop, stop);
        } else {
            context.stopService(new Intent(context.getApplicationContext(), tClass));
            Date start = findNextTime(new Date(), Interval.Node.Start);
            alarmManager(tClass, Interval.Node.Start, start);
        }
    }

    public void alarmManager(Class<? extends Service> tClass, Interval.Node node, Date when) {
        Intent intent = new Intent(Constants.ScheduleBroadcastReceiver.ACTION);
        String stringClass = tClass.getPackage().getName() + "." + tClass.getSimpleName();
        intent.putExtra(Constants.ScheduleBroadcastReceiver.CLASS, stringClass);
        intent.putExtra(Constants.ScheduleBroadcastReceiver.OPERATION, node.toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC, when.getTime(), pendingIntent);
    }

}
