package com.goldfish.sevenseconds.item;

import android.graphics.Bitmap;

/**
 * Created by zzz87 on 2017/2/21.
 */

public class MemorySheetPreview {

    private String title;
    private Bitmap image;
    private String contents;
    private String pre_time;
    private String[] pre_tags;
    // 记录忆单成员的账号
    private String account;
    // 记录忆单的ID
    private String memoryID;

    public MemorySheetPreview(String title, Bitmap image, String contents, String account, String memoryID,String t,String[] ag){
        this.title = title;
        this.image = image;
        this.contents = contents;
        this.account = account;
        this.memoryID = memoryID;
        this.pre_tags = ag;
        this.pre_time = t;
    }
    public String getTitle(){return this.title;}
    public Bitmap getImageid(){return this.image;}
    public String getContents(){return this.contents;}
    public String[] getPre_tags() {
        return pre_tags;
    }
    public String getPre_time() {
        return pre_time;
    }
    public String getAccount() {
        return account;
    }
    public String getMemoryID() {
        return memoryID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setMemoryID(String memoryID) {
        this.memoryID = memoryID;
    }

    public void setPre_tags(String[] pre_tags) {
        this.pre_tags = pre_tags;
    }

    public void setPre_time(String pre_time) {
        this.pre_time = pre_time;
    }
}
