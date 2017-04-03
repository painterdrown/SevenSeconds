package com.goldfish.sevenseconds.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http
{
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType PNG = MediaType.parse("image/png");
    private static final String CACHE_PATH = "/data/data/com.painterdrown.sevens/cache";
    private static final String API_PATH = "http://www.sysu7s.cn:3000/api";

    private static final int CONNECT_TIMEOUT = 3 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;
    private static final int WRITE_TIMEOUT = 10 * 1000;

    private static final boolean SAVE_TO_CACHE = false;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    // ====================== 私有 ======================

    // GET方法
    private static Response getByJSON(String url, JSONObject jo)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    // 返回的response不止有JSON，还可以有文件流
    private static Response postJSON(String url, JSONObject jo)
    {
        RequestBody body = RequestBody.create(JSON, jo.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static JSONObject postForJSONObject(String action, JSONObject jo)
    {
        JSONObject joToReturn = new JSONObject();
        try {
            Response response = postJSON(API_PATH + action, jo);
            if (response != null) {
                if (response.isSuccessful()) {
                    joToReturn = new JSONObject(response.body().string());
                } else {
                    joToReturn.put("ok", false);
                }
            } else {
                joToReturn.put("ok", false);
                joToReturn.put("errMsg", "啊，服务器出错了！");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return joToReturn;
    }

    private static Bitmap postForBitmap(String action, JSONObject jo)
    {
        Bitmap bitmap = null;

        Response response = postJSON(API_PATH + action, jo);
        if (response != null && response.isSuccessful()) {
            InputStream is = response.body().byteStream();
            bitmap = BitmapFactory.decodeStream(is);
        }

        return bitmap;
    }

    private static JSONObject postImages(String action, JSONObject jo, List<String> imageUrls)
    {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addPart(RequestBody.create(JSON, jo.toString()));

        int count = 0;
        for (String imageUrl : imageUrls) {
            File file = new File(imageUrl);
            builder.addFormDataPart(count++ + "", file.getName(), RequestBody.create(PNG, file));
        }

        MultipartBody requestBody = builder.build();  // 构建请求体

        // 构建请求
        Request request = new Request.Builder()
                .url(API_PATH + action)  // 地址
                .post(requestBody)  // 添加请求体
                .build();

        Response response = null;
        JSONObject joToReturn = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                joToReturn = new JSONObject(response.body().string());
            } else {
                joToReturn = new JSONObject();
                joToReturn.put("ok", false);
                joToReturn.put("errMsg", "服务器出错");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return joToReturn;
    }

    private static void saveInputStreamToCache(InputStream is, String[] dirs, String fileName)
    {
        // 一层一层创建文件夹
        String dirPath = CACHE_PATH;
        for (String dir : dirs) {
            dirPath += "/" + dir;
            new File(dirPath).mkdir();
        }

        // 创建空文件
        String filePath = dirPath + "/" + fileName;
        File file = new File(filePath);

        // 开始从输入流读取到空文件
        OutputStream os = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while(is.read(buffer) != -1){
                os.write(buffer);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void saveBitmapToCache(Bitmap bitmap, String[] dirs, String fileName)
    {
        // 一层一层创建文件夹
        String dirPath = CACHE_PATH;
        for (String dir : dirs) {
            dirPath += "/" + dir;
            new File(dirPath).mkdir();
        }

        // 创建空文件
        String filePath = dirPath + "/" + fileName;
        File file = new File(filePath);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> postForArrayList(String action, JSONObject jo)
    {
        ArrayList<String> arrayList = null;
        String str = null;
        try {
            Response response = postJSON(API_PATH + action, jo);
            if (response != null && response.isSuccessful()) {
                JSONObject joToReturn = new JSONObject(response.body().string());
                str = joToReturn.getString("list");
                arrayList = new ArrayList<String>(Arrays.asList(str.split("\\,")));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private static InputStream BitmapToInputStream(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // ====================== 接口（全部不能在主线程直接调用） ======================

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
     * ok, title, labels（字符串，如"movies,music"）, time（忆单的时间属性，不是创建忆单的时间）, content， reviewCount（评论数目）, collectCount（被收藏数目）, likeCount（被点赞/喜欢数目）
     */
    public static JSONObject getMemory(JSONObject jo)
    {
        return postForJSONObject("/get-memory", jo);
    }

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
     * memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject likeMemory(JSONObject jo)
    {
        return postForJSONObject("/like-memory", jo);
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
     * account, memoryId
     * 【返回值】
     * ok
     */
    public static JSONObject collectMemory(JSONObject jo)
    {
        return postForJSONObject("/collect-memory", jo);
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
     * （不需要参数）
     * 【返回值】
     * ArrayList<String>（可能为null！！！）
     */
    public static ArrayList<String> getAllMemoryList()
    {
        return postForArrayList("/get-all-memory-list", jo);
    }
}
