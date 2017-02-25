package com.goldfish.sevenseconds.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/2/23.
 */

public class Chatting extends DataSupport {

    private int id;
    private String account;
    private String message;
    private String time;
    private int sendOrReceive;
    private int readOrNot;

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public int getReadOrNot() {
        return readOrNot;
    }

    public int getSendOrReceive() {
        return sendOrReceive;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReadOrNot(int readOrNot) {
        this.readOrNot = readOrNot;
    }

    public void setSendOrReceive(int sendOrReceive) {
        this.sendOrReceive = sendOrReceive;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
