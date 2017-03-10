package com.goldfish.sevenseconds.item;

/**
 * Created by zzz87 on 2017/2/21.
 */

public class MemorySheetPreview {

    private String name;
    private int imageid;
    private String contents;
    // 记录忆单成员的账号
    private String account;
    // 记录忆单的ID
    private String memoryID;

    public MemorySheetPreview(String name, int id, String contents, String account, String memoryID){
        this.name = name;
        this.imageid = id;
        this.contents = contents;
        this.account = account;
        this.memoryID = memoryID;
    }
    public String getName(){return this.name;}
    public int getImageid(){return this.imageid;}
    public String getContents(){return this.contents;}

    public String getAccount() {
        return account;
    }

    public String getMemoryID() {
        return memoryID;
    }
}
