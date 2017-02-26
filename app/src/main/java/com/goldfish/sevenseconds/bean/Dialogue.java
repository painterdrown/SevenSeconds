package com.goldfish.sevenseconds.bean;

/**
 * Created by lenovo on 2017/2/26.
 */

public class Dialogue {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private byte[] face;
    private  String time;

    public Dialogue(String content, int type, byte[] face, String time) {
        this.content = content;
        this.type = type;
        this.face = face;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public byte[] getFace() { return face; }

    public String getTime() { return time; }

}
