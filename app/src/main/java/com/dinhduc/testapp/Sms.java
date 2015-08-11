package com.dinhduc.testapp;

/**
 * Created by Nguyen Dinh Duc on 8/5/2015.
 */
public class Sms {
    public static final int SMS_INBOX = 1;
    public static final int SMS_SENT = 2;
    private String body;
    private long date;
    private int type;

    public Sms(String body, int type) {
        this.body = body;
        this.type = type;
    }
    public Sms(String body, long date, int type) {
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
