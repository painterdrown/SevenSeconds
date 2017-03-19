package com.goldfish.sevenseconds.item;

import com.goldfish.sevenseconds.bean.MyFollow;

/**
 * Created by lenovo on 2017/2/23.
 */

public class MyFollowItem {

    private int imageid;
    private byte[] face;
    private String name;
    private String introduction;
    private String account;

    public MyFollowItem() {}

    public MyFollowItem(int imageid, String name, String introduction, byte[] face, String account) {
        this.imageid = imageid;
        this.name = name;
        this.introduction = introduction;
        this.face = face;
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getImageid() {
        return imageid;
    }

    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public byte[] getFace() { return face; }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public void setImageid(int imageid) {
        this.imageid = imageid;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setName(String name) {
        this.name = name;
    }

}
