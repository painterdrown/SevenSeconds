package com.goldfish.sevenseconds.item;

/**
 * Created by lenovo on 2017/2/23.
 */

public class ChattingItem {

    private String name;
    private String message;
    private byte[] image;
    private String time;
    private String account;
    private int readOrNot;

    public ChattingItem(byte[] image, String name, String message, String time, String account, int readOrNot) {
        this.name = name;
        this.message = message;
        this.image = image;
        this.time = time;
        this.account = account;
        this.readOrNot = readOrNot;
    }

    public String getName() { return name; }
    public byte[] getImage() {
        return image;
    }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getAccount() { return account; }
    public int getReadOrNot() { return readOrNot; }

}
