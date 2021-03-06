package com.jaragua.avlmobile.entities;

import java.util.List;

@SuppressWarnings("unused")
public class DriverResponse {

    private String status;
    private List<Message> messages;
    private Configuration configuration;
    private Schedule schedule;

    public DriverResponse() {
    }

    public DriverResponse(String status, List<Message> messages, Configuration configuration, Schedule schedule) {
        this.status = status;
        this.messages = messages;
        this.configuration = configuration;
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    
}
