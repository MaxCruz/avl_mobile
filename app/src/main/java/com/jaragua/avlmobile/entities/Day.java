package com.jaragua.avlmobile.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Day {

    private Week day;
    private List<Interval> intervals;
    public Day() {
    }

    public Day(Week day, List<Interval> intervals) {
        this.day = day;
        this.intervals = intervals;
    }

    public Week getDay() {
        return day;
    }

    public void setDay(Week day) {
        this.day = day;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public enum Week {
        @SerializedName("0")
        Sunday,
        @SerializedName("1")
        Monday,
        @SerializedName("2")
        Tuesday,
        @SerializedName("3")
        Wednesday,
        @SerializedName("4")
        Thursday,
        @SerializedName("5")
        Friday,
        @SerializedName("6")
        Saturday
    }

}
