package com.goldfish.sevenseconds.item;

/**
 * Created by lenovo on 2017/3/1.
 */

public class AmemReviewItem {

    private String name;
    private String message;
    private byte[] image;
    private String time;
    private String account;
    private String like;

    public AmemReviewItem(byte[] image, String name, String message, String time, String account, String like) {
        this.name = name;
        this.message = message;
        this.image = image;
        this.time = time;
        this.account = account;
        this.like = like;
    }

    public String getName() { return name; }
    public byte[] getImage() {
        return image;
    }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getAccount() { return account; }
    public String getLike() { return like; }
}
