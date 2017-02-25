package com.goldfish.sevenseconds.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/2/22.
 */

public class Information extends DataSupport {

    private int id;
    private String account;
    private String name;
    private String sex;
    private String introduction;
    private byte[] face;
    private String birthday;
    private String phone;

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhone() {
        return phone;
    }

    public byte[] getFace() {
        return face;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
