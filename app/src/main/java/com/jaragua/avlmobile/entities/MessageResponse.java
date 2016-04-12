package com.jaragua.avlmobile.entities;

@SuppressWarnings("unused")
public class MessageResponse {

    private int motive;
    private String date;
    private long messageId;
    private String messageResponse;

    public MessageResponse() {
    }

    public MessageResponse(int motive, String date, long messageId, String messageResponse) {
        this.motive = motive;
        this.date = date;
        this.messageId = messageId;
        this.messageResponse = messageResponse;
    }

    public int getMotive() {
        return motive;
    }

    public void setMotive(int motive) {
        this.motive = motive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    }
}
