package com.jaragua.avlmobile.entities;

@SuppressWarnings("unused")
public class Interval {

    private String start;
    private String stop;

    public Interval() {

    }

    public Interval(String start, String stop) {
        this.start = start;
        this.stop = stop;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

}
