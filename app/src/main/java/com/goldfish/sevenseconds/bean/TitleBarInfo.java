package com.goldfish.sevenseconds.bean;

/**
 * Created by lenovo on 2017/3/5.
 */

public class TitleBarInfo {

    private String name;
    private String introduction;
    private boolean ok;
    private byte[] face;
    private String birthday;
    private String sex;
    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getSex() {
        return sex;
    }

    public byte[] getFace() {
        return face;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean getOk() {
        return ok;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getName() {
        return name;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setName(String name) {
        this.name = name;
    }
}
