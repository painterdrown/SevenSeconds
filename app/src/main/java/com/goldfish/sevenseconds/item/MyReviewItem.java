package com.goldfish.sevenseconds.item;

import android.graphics.Bitmap;

/**
 * Created by lenovo on 2017/3/30.
 */

public class MyReviewItem {

    private Bitmap face;
    private String name;
    private String account;
    private String otherMessage;
    private String myMessage;
    private String time;
    private boolean isMemory;
    private Bitmap cover;
    private String title;
    private String memoryId;

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public boolean getIsMemory() {
        return isMemory;
    }

    public void setIsMemory(boolean flag) {
        isMemory = flag;
    }

    public void setMemory(boolean memory) {
        isMemory = memory;
    }

    public String getAccount() {
        return account;
    }

    public String getTime() {
        return time;
    }

    public Bitmap getFace() {
        return face;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public String getName() {
        return name;
    }

    public String getOtherMessage() {
        return otherMessage;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFace(Bitmap face) {
        this.face = face;
    }

    public void setMyMessage(String myMessage) {
        this.myMessage = myMessage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOtherMessage(String otherMessage) {
        this.otherMessage = otherMessage;
    }
}
