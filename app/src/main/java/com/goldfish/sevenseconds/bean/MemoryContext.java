package com.goldfish.sevenseconds.bean;

import android.graphics.Bitmap;

/**
 * Created by lenovo on 2017/3/10.
 */

public class MemoryContext {

    private String title;
    private String[] label;
    private Bitmap cover;
    private String time;
    private int reviewCount;
    private int collectCount;
    private int likeCount;
    private boolean ok;
    private String errMsg;
    private String context;
    private boolean isLike;
    private boolean isAdd;

    public void setIsAdd(boolean add) {
        isAdd = add;
    }

    public void setIsLike(boolean like) {
        isLike = like;
    }

    public boolean getIsLike() {
        return isLike;
    }

    public boolean getIsAdd() {
        return isAdd;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean getOk() {
        return ok;
    }

    public Bitmap getCover() {
        return cover;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String[] getLabel() {
        return label;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setLabel(String[] label) {
        this.label = label;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
