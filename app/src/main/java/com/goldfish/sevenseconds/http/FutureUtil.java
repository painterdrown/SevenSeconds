package com.goldfish.sevenseconds.http;

import org.json.JSONObject;

public class FutureUtil extends HttpBase {
    /**
     * 【参数】
     * from（account）, to（account）, content, duetime（字符串，如"2014-04-04"或"2017-04-04 23:10:58"）
     * 【返回值】
     * ok
     */
    public static JSONObject addFuture(JSONObject jo)
    {
        return postForJSONObject("/add-future", jo);
    }

    /**
     * 【参数】
     * futureId
     * 【返回值】
     * from, to, content, duetime（字符串）
     */
    public static JSONObject getFuture(JSONObject jo)
    {
        return postForJSONObject("/get-future", jo);
    }

    /**
     * 【参数】
     * futureId
     * 【返回值】
     * ok
     */
    public static JSONObject ifTimeIsUp(JSONObject jo)
    {
        return postForJSONObject("/if-time-is-up", jo);
    }
}
