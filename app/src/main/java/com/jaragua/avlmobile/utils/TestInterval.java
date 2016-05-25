package com.jaragua.avlmobile.utils;

import com.jaragua.avlmobile.entities.Day;
import com.jaragua.avlmobile.entities.Interval;
import com.jaragua.avlmobile.entities.Schedule;

import java.util.ArrayList;
import java.util.Locale;

public class TestInterval {

    public static void main(String args[]) {
        Schedule testSchedule = new Schedule();
        ArrayList<Day> days = new ArrayList<>();
        Day sunday = new Day();
        sunday.setDay(Day.Week.Sunday);
        ArrayList<Interval> intervals = new ArrayList<>();
        for (int h = 0; h <= 23; h++) {
            for (int m = 1; m <= 59; m += 2) {
                Interval interval = new Interval();
                interval.setStart(String.format(Locale.US, "%02d", h) + ":" + String.format(Locale.US, "%02d", m) + ":00");
                interval.setStop(String.format(Locale.US, "%02d", h) + ":" + String.format(Locale.US, "%02d", m) + ":59");
                intervals.add(interval);
            }
        }
        sunday.setIntervals(intervals);
        days.add(sunday);
        testSchedule.setDays(days);
        System.out.println(testSchedule);
    }

}
