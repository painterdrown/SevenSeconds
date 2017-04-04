package com.goldfish.sevenseconds.item;

import android.graphics.Bitmap;

/**
 * Created by lenovo on 2017/4/3.
 */

public class MyPageTimelineItem {

    private String title;
    private Bitmap cover;
    private String time;
    private String memoryId;
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getTime() {
        return time;
    }

    public Bitmap getCover() {
        return cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
