package com.jaragua.avlmobile.entities;

@SuppressWarnings("unused")
public class Message {

    private int id;
    private String message;
    private String response;

    public Message(int id, String message, String response) {
        this.id = id;
        this.message = message;
        this.response = response;
    }

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
