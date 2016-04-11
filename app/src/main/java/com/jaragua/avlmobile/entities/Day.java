package com.jaragua.avlmobile.entities;

import java.util.List;

@SuppressWarnings("unused")
public class Day {

    private int day;
    private List<Interval> intervals;

    public Day() {
    }

    public Day(int day, List<Interval> intervals) {
        this.day = day;
        this.intervals = intervals;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

}
