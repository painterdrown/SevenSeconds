package com.goldfish.sevenseconds.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class MemorySheet extends DataSupport {

    private String name;
    private String contents;

    public String getName(){
        return this.name;
    }
    public String getContents(){
        return this.contents;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setContents(String contents){
        this.contents = contents;
    }
}
