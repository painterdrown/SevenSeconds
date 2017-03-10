package com.goldfish.sevenseconds.bean;

import android.provider.ContactsContract;

import org.litepal.crud.DataSupport;

/**
 * Created by zzz87 on 2017/2/28.
 */

public class Lastmes extends DataSupport{
    private String username;
    private String pass;
    public void setUsername(String mid){
        this.username = mid;
    }
    public void setPass(String mid){
        this.pass = mid;
    }
    public String getPass() {
        return pass;
    }
    public String getUsername() {
        return username;
    }
}
