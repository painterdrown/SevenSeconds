package com.goldfish.sevenseconds.item;

/**
 * Created by lenovo on 2017/2/23.
 */

public class MyFollowItem {

    private int imageid;
    private byte[] face;
    private String name;
    private String introduction;

    public MyFollowItem(int imageid, String name, String introduction, byte[] face) {
        this.imageid = imageid;
        this.name = name;
        this.introduction = introduction;
        this.face = face;
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

}
