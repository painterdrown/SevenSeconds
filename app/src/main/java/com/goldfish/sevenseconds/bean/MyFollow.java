package com.goldfish.sevenseconds.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/2/23.
 */

public class MyFollow extends DataSupport {

    private int id;
    private String account;
    private String name;
    private String introduction;
    private byte[] face;

    public int getId() {
        return id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getAccount() {
        return account;
    }

    public byte[] getFace() {
        return face;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
