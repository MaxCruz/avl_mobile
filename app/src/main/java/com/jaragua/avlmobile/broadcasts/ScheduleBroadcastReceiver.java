package com.jaragua.avlmobile.broadcasts;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaragua.avlmobile.entities.Interval;
import com.jaragua.avlmobile.entities.Schedule;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.ScheduleHandler;

import java.util.Date;

public class ScheduleBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.ScheduleBroadcastReceiver.ACTION.equals(intent.getAction())) {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCES, 0);
            Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
            Bundle extras = intent.getExtras();
            String className = extras.getString(Constants.ScheduleBroadcastReceiver.CLASS);
            try {
                Class<?> rawClass = Class.forName(className);
                Class<? extends Service> tClass = rawClass.asSubclass(Service.class);
                String scheduleString = settings.getString(Constants.SharedPreferences.SCHEDULE,
                        Constants.LocationService.DEFAULT_SCHEDULE);
                Schedule schedule = gson.fromJson(scheduleString, Schedule.class);
                ScheduleHandler scheduleHandler;
                if (schedule != null) {
                    scheduleHandler = new ScheduleHandler(context.getApplicationContext(), schedule.getDays());
                } else {
                    context.startService(new Intent(context.getApplicationContext(), tClass));
                    return;
                }
                String nodeString = extras.getString(Constants.ScheduleBroadcastReceiver.OPERATION);
                Interval.Node node = Interval.Node.valueOf(nodeString);
                if (node == Interval.Node.Start) {
                    context.startService(new Intent(context.getApplicationContext(), tClass));
                    Date stop = scheduleHandler.findNextTime(new Date(), Interval.Node.Stop);
                    scheduleHandler.alarmManager(tClass, Interval.Node.Stop, stop);
                } else if (node == Interval.Node.Stop) {
                    context.stopService(new Intent(context.getApplicationContext(), tClass));
                    Date start = scheduleHandler.findNextTime(new Date(), Interval.Node.Start);
                    scheduleHandler.alarmManager(tClass, Interval.Node.Start, start);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
