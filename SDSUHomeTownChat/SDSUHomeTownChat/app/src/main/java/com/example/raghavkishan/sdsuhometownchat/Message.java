package com.example.raghavkishan.sdsuhometownchat;

import java.util.Date;

/**
 * Created by raghavkishan on 4/16/2017.
 */

public class Message {

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    private String text;
    private String sender;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private Date date;

}
