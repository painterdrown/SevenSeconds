package com.goldfish.sevenseconds.bean;

/**
 * Created by zzz87 on 2017/2/25.
 */

public class Users {
    private String _id;
    private String username;
    private String password;

    public String get_id(){
        return _id;
    }


    public String getUsername(){
        return username;
    }

    public String getPassword(){return password;}

    public void setUsername(String mid){
        this.username = mid;
    }

    public void setPassword(String mid){
        this.password = mid;
    }

    public void set_id(String mid){_id = mid;}
}
