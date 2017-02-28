package com.goldfish.sevenseconds.bean;

/**
 * Created by zzz87 on 2017/2/28.
 */

public class Lastmes {
    private boolean mess;
    private String username;
    private String pass;
    public void setMess(boolean mid){
        this.mess = mid;
    }
    public void setUsername(String mid){
        this.username = mid;
    }
    public void setPass(String mid){
        this.pass = mid;
    }
    public boolean getMess(){ return this.mess; }
    public String getPass() {
        return pass;
    }
    public String getUsername() {
        return username;
    }
}
