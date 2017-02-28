package com.goldfish.sevenseconds.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by zzz87 on 2017/2/24.
 */

public class LastUser extends DataSupport {

    private String name;
    public void setName(String mid){
        this.name = mid;
    }
    public String getName(){
        return this.name;
    }
}
