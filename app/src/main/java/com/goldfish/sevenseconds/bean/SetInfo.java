package com.goldfish.sevenseconds.bean;

/**
 * Created by lenovo on 2017/3/12.
 */

public class SetInfo {

    private String name;
    private String sex;
    private String introduction;
    private byte[] face;
    private String birthday;
    private boolean ok;
    private String errMsg;

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean getOk() {
        return ok;
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

    public byte[] getFace() {
        return face;
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

    public void setSex(String sex) {
        this.sex = sex;
    }

}
