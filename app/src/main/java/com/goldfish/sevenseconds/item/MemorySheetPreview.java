package com.goldfish.sevenseconds.item;

/**
 * Created by zzz87 on 2017/2/21.
 */

public class MemorySheetPreview {

    private String title;
    private int imageid;
    private String contents;
    private String pre_time;
    private String pre_tags;
    // 记录忆单成员的账号
    private String account;
    // 记录忆单的ID
    private String memoryID;

    public MemorySheetPreview(String title, int id, String contents, String account, String memoryID,String t,String ag){
        this.title = title;
        this.imageid = id;
        this.contents = contents;
        this.account = account;
        this.memoryID = memoryID;
        this.pre_tags = ag;
        this.pre_time = t;
    }
    public String getTitle(){return this.title;}
    public int getImageid(){return this.imageid;}
    public String getContents(){return this.contents;}

    public String getPre_tags() {
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
}
