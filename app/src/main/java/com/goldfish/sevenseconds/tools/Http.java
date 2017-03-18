package com.goldfish.sevenseconds.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    private static OkHttpClient okHttpClient = new OkHttpClient();

    // ====================== 私有 ======================

    private static Response getByJSON(String url, JSONObject jo)
    {
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

    private static Response postJSON(String url, JSONObject jo)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
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

    private static JSONObject postForJSONObject(String action, JSONObject jo)
    {
        JSONObject joToReturn = null;
        try {
            Response response = postJSON(API_PATH + action, jo);
            if (response.isSuccessful()) {
                joToReturn = new JSONObject(response.body().string());
            } else {
                joToReturn.put("ok", false);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return joToReturn;
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
     * JSONObject jo = new JSONObject();
     * jo.put("account", "...");
     * jo.put("password", "...");
     *
     * 【返回值】
     * ok属性，通过jo.getBoolean("ok")拿到，如果是false就通过jo.getString("errMsg")拿到错误信息
     */
    public static JSONObject login(JSONObject jo)
    {
        return postForJSONObject("/login", jo);
    }

    /**
     * 参考login
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
     * 参考addFollow
     */
    public static JSONObject deleteFollow(JSONObject jo)
    {
        return postForJSONObject("/delete-follow", jo);
    }

    /**
     * 【参数】
     * jo.put("title", "第一个忆单！");
     * jo.put("author", "a");
     * jo.put("time", "2017-03-11");
     * jo.put("labels", "aa,bb");
     * jo.put("description", "这是我们的第一个忆单哟！");
     * jo.put("content", "这是第一段<img1>这是第二段<img2></>");
     * jo.put("authority", "0");
     */
    public static JSONObject addMemory(JSONObject jo, String[] imageUrls)
    {
        String url = API_PATH + "/add-memory";

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addPart(RequestBody.create(JSON, jo.toString()));

        for (int i = 0; i < imageUrls.length; ++i) {
            File file = new File(imageUrls[i]);
            builder.addFormDataPart(i + "", file.getName(), RequestBody.create(PNG, file));
        }

        MultipartBody requestBody = builder.build();  // 构建请求体

        // 构建请求
        Request request = new Request.Builder()
            .url(url)  // 地址
            .post(requestBody)  // 添加请求体
            .build();

        Response response = null;
        JSONObject joToReturn = null;
        try {
            response = okHttpClient.newCall(request).execute();
            joToReturn = new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return joToReturn;
    }

    /*
     JSONObject jo = new JSONObject();
     jo.put("memoryId", "...");
     */
    public static JSONObject getMemory(JSONObject jo)
    {
        return postForJSONObject("/get-memory", jo);
    }

    /*
     JSONObject jo = new JSONObject();
     jo.put("memoryId", "...");
     jo.put("i", "0");  // 0表示第0张图片，也就是忆单的封面
     */
    public static Bitmap getMemoryImg(final JSONObject jo)
    {
        String url = API_PATH + "/get-memory-img";

        Response response = postJSON(url, jo);
        InputStream is = response.body().byteStream();

        Bitmap bitmap = BitmapFactory.decodeStream(is);

        // 存到缓存中
        try {
            String[] dirs = new String[2];
            dirs[0] = "memorys";
            dirs[1] = jo.getString("memoryId");
            saveBitmapToCache(bitmap, dirs, jo.getString("i") +".png");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /*
     JSONObject jo = new JSONObject();
     jo.put("account", "a");
     */
    public static Bitmap getUserFace(final JSONObject jo)
    {
        String url = API_PATH + "/get-user-face";

        Response response = postJSON(url, jo);
        InputStream is = response.body().byteStream();

        Bitmap bitmap = BitmapFactory.decodeStream(is);

        // 存到缓存中
        try {
            String[] dirs = new String[2];
            dirs[0] = "users";
            dirs[1] = jo.getString("faces");
            saveBitmapToCache(bitmap, dirs, jo.getString("account") + ".png");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /*
     JSONObject jo = new JSONObject();
     jo.put("account", "a");
     */
    public static String[] getFollowingList(JSONObject jo)
    {
        String url = API_PATH + "/get-following-list";

        String str = null;
        JSONObject joToReturn = null;
        try {
            joToReturn = new JSONObject(postJSON(url, jo).body().string());
            str = joToReturn.getString("followingList");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return str.split("\\,");
    }

    /*
     JSONObject jo = new JSONObject();
     jo.put("account", "...");
     */
    public static String[] getMemoryList(JSONObject jo)
    {
        String url = API_PATH + "/get-memory-list";

        String str = null;
        JSONObject joToReturn = null;
        try {
            joToReturn = new JSONObject(postJSON(url, jo).body().string());
            str = joToReturn.getString("memoryList");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return str.split("\\,");
    }

    /*
     【需要的参数】
     account

     【返回的属性】
     ok:           Boolean
     account:      String
     username:     String
     introduction: String
     birthday:     String
     sex:          String
     */
    public static JSONObject getUserInfo(JSONObject jo)
    {
        return postForJSONObject("/get-user-info", jo);
    }

    /*
     【需要的参数】
     memoryId

     【返回的属性】
     ok
     */
    public static JSONObject likeMemory(JSONObject jo)
    {
        return postForJSONObject("/like-memory", jo);
    }

    /*
     【需要的参数】
     account:      String
     username:     String
     introduction: String
     birthday:     String
     sex:          String("男"/"女")

     【返回的属性】
     ok
     */
    public static JSONObject modifyUserInfo(JSONObject jo)
    {
        return postForJSONObject("/modify-user-info", jo);
    }

    /*
     【需要的参数】
     account
     memoryId

     【返回的属性】
     ok
     */
    public static JSONObject collectMemory(JSONObject jo)
    {
        return postForJSONObject("/collect-memory", jo);
    }

    /*
     【需要的参数】
     account

     【返回的属性】
     ok
     username

     【用法】 记得要在新开的线程里调用这个函数
     JSONObject jo = new JSONObject();
     jo.put("account", "...");
     JSONObject result = Http.getUsername(jo);
     if (result.getBoolean("ok")) {
        // 成功取到用户名
        String username = result.getString("username");
     }
     */
    public static JSONObject getUsername(JSONObject jo)
    {
        return postForJSONObject("/get-username", jo);
    }
}
