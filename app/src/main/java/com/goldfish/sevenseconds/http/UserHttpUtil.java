package com.goldfish.sevenseconds.http;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserHttpUtil extends HttpBase {
    /**
     * 【参数】
     * account, password
     * 【返回值】
     * ok属性，通过jo.getBoolean("ok")拿到，如果是false就通过jo.getString("errMsg")拿到错误信息
     */
    public static JSONObject login(JSONObject jo)
    {
        return postForJSONObject("/login", jo);
    }

    /**
     * 【参数】
     * account, password
     * 【返回值】
     * ok
     */
    public static JSONObject register(JSONObject jo)
    {
        return postForJSONObject("/register", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * ok, username
     */
    public static JSONObject getUsername(JSONObject jo)
    {
        return postForJSONObject("/get-username", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * ok, account, username, introduction, birthday, sex
     */
    public static JSONObject getUserInfo(JSONObject jo)
    {
        return postForJSONObject("/get-user-info", jo);
    }

    /**
     * 【参数】
     * account:      String
     * username:     String
     * introduction: String
     * birthday:     String
     * sex:          String("男"/"女")
     * 【返回值】
     * ok
     */
    public static JSONObject modifyUserInfo(JSONObject jo)
    {
        return postForJSONObject("/modify-user-info", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * Bitmap
     */
    public static Bitmap getUserFace(final JSONObject jo)
    {
        Bitmap bitmap = postForBitmap("/get-user-face", jo);

        // 存到缓存中
        if (SAVE_TO_CACHE) {
            try {
                String[] dirs = new String[2];
                dirs[0] = "users";
                dirs[1] = jo.getString("faces");
                saveBitmapToCache(bitmap, dirs, jo.getString("account") + ".png");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * 【参数】
     * account, 图片的URI
     * 【返回值】
     * ok
     */
    public static JSONObject setUserFace(JSONObject jo, String url)
    {
        List<String> list = new ArrayList<String>();
        list.add(url);
        return postImages("/set-user-face", jo, list);
    }

    /**
     * 【参数】
     * myAccount, otherAccount
     * 【返回值】
     * ok
     */
    public static JSONObject addFollow(JSONObject jo)
    {
        return postForJSONObject("/add-follow", jo);
    }

    /**
     * 【参数】
     * myAccount, otherAccount
     * 【返回值】
     * ok
     */
    public static JSONObject deleteFollow(JSONObject jo)
    {
        return postForJSONObject("/delete-follow", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject likeMemory(JSONObject jo)
    {
        return postForJSONObject("/like-memory", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject unlikeMemory(JSONObject jo)
    {
        return postForJSONObject("/unlike-memory", jo);
    }

    /**
     * 【参数】
     * account, memoryId, time
     * 【返回值】
     * ok
     */
    public static JSONObject collectMemory(JSONObject jo)
    {
        return postForJSONObject("/collect-memory", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject uncollectMemory(JSONObject jo)
    {
        return postForJSONObject("/uncollect-memory", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject ifLikeMemory(JSONObject jo)
    {
        return postForJSONObject("/if-like-memory", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject ifCollectMemory(JSONObject jo)
    {
        return postForJSONObject("/if-collect-memory", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getFollowingList(JSONObject jo)
    {
        return postForArrayList("/get-following-list", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getMemoryList(JSONObject jo)
    {
        return postForArrayList("/get-memory-list", jo);
    }

    /**
     * 【参数】
     * account, memoryId
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getRestMemoryIds(JSONObject jo)
    {
        return postForArrayList("/get-rest-memory-list", jo);
    }

    /**
     * 【参数】
     * account
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getCollectMemoryList(JSONObject jo)
    {
        return postForArrayList("/get-collect-memory-list", jo);
    }
}