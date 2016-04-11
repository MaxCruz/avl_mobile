package com.jaragua.avlmobile.entities;

import java.util.List;

@SuppressWarnings("unused")
public class Schedule {

    private List<Day> days;

    public Schedule() {
    }

    public Schedule(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

}
