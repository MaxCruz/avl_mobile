package com.jaragua.avlmobile.entities;

@SuppressWarnings("unused")
public class Configuration {

    private int timeLimit;
    private int txDistance;

    public Configuration() {
    }

    public Configuration(int timeLimit, int txDistance) {
        this.timeLimit = timeLimit;
        this.txDistance = txDistance;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTxDistance() {
        return txDistance;
    }

    public void setTxDistance(int txDistance) {
        this.txDistance = txDistance;
    }

}
