package com.goldfish.sevenseconds.http;

import org.json.JSONObject;

import java.util.ArrayList;

public class CommentHttpUtil extends HttpBase {
    /**
     * 【参数】
     * memoryId, account, content
     * 【返回值】
     * ok
     */
    public static JSONObject addComment(JSONObject jo)
    {
        return postForJSONObject("/add-comment", jo);
    }

    /**
     * 【参数】
     * commentId
     * 【返回值】
     * ok, account, time（字符串）, content
     */
    public static JSONObject getComment(JSONObject jo)
    {
        return postForJSONObject("/get-comment", jo);
    }

    /**
     * 获取跟我相关的评论（最新的在最前面）
     * 【参数】
     * account
     * 【返回值】
     * ArrayList<JSONObject>（可能为null！！！）
     * JSONObject有account（谁评论的）, memoryId, time, content属性
     */
    public static ArrayList<JSONObject> getCommentsAboutMe(JSONObject jo)
    {
        return postForArrayListOfJSONObject("/get-comments-about-me", jo);
    }
}
