package com.goldfish.sevenseconds.http;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemoryHttpUtil extends HttpBase {
    /**
     * 【参数】
     * title, author, time, labels（字符串的形式，如"movies,music"）, description, content, authority
     * 【返回值】
     * ok
     */
    public static JSONObject addMemory(JSONObject jo, List<String> imageUrls)
    {
        return postImages("/add-memory", jo, imageUrls);
    }

    /**
     * 【参数】
     * memoryId
     * 【返回值】
     * ok, title, author, labels（字符串，如"movies,music"）, time（忆单的时间属性，不是创建忆单的时间）, content， reviewCount（评论数目）, collectCount（被收藏数目）, likeCount（被点赞/喜欢数目）
     */
    public static JSONObject getMemory(JSONObject jo)
    {
        return postForJSONObject("/get-memory", jo);
    }

    /**
     * 【参数】
     * memoryId, i（该忆单的第几张图片，0表示封面，1表示正文的第一张图片）
     * 【返回值】
     * Bitmap（可能为null！！！）
     */
    public static Bitmap getMemoryImg(final JSONObject jo)
    {
        Bitmap bitmap = postForBitmap("/get-memory-img", jo);
        // 存到缓存中
        if (SAVE_TO_CACHE) {
            try {
                String[] dirs = new String[2];
                dirs[0] = "memorys";
                dirs[1] = jo.getString("memoryId");
                saveBitmapToCache(bitmap, dirs, jo.getString("i") +".png");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取我收藏忆单时，对于我来说的纪念时间
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok, count
     */
    public static JSONObject getMemoryTimeForMe(JSONObject jo)
    {
        return postForJSONObject("/get-memory-time-for-me", jo);
    }

    /**
     * 【参数】
     * memoryId
     * 【返回值】
     * ok, count
     */
    public static JSONObject getLikeCount(JSONObject jo)
    {
        return postForJSONObject("/get-like-count", jo);
    }

    /**
     * 【参数】
     * memoryId
     * 【返回值】
     * ok, count
     */
    public static JSONObject getCollectCount(JSONObject jo)
    {
        return postForJSONObject("/get-collect-count", jo);
    }

    /**
     * 【参数】
     * memoryId
     * 【返回值】
     * ok, count
     */
    public static JSONObject getCommentCount(JSONObject jo)
    {
        return postForJSONObject("/get-comment-count", jo);
    }

    /**
     * 【参数】
     * memoryId
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getCommentList(JSONObject jo)
    {
        return postForArrayList("/get-comment-list", jo);
    }

    /**
     * 【参数】
     * query（要查询的字符串）
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> searchMemorys(JSONObject jo)
    {
        return postForArrayList("/search-memorys", jo);
    }

    /**
     * 【参数】
     * （不需要参数）
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getAllMemoryList()
    {
        JSONObject jo = null;
        return postForArrayList("/get-all-memory-list", jo);
    }
}
