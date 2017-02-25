package com.goldfish.sevenseconds.item;

/**
 * Created by zzz87 on 2017/2/21.
 */

public class MemorySheetPreview {

    private String name;
    private int imageid;
    private String contents;

    public MemorySheetPreview(String name, int id, String contents){
            this.name = name;
            this.imageid = id;
            this.contents = contents;
    }
    public String getName(){return this.name;}
    public int getImageid(){return this.imageid;}
    public String getContents(){return this.contents;}
}
