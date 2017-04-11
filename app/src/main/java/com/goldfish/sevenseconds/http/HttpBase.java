package com.goldfish.sevenseconds.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
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

public class HttpBase {
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final MediaType PNG = MediaType.parse("image/png");
    protected static final MediaType JPEG = MediaType.parse("image/jpeg");
    protected static final String CACHE_PATH = "/data/data/com.painterdrown.sevens/cache";
    protected static final String API_PATH = "http://www.sysu7s.cn:3000/api";

    protected static final int CONNECT_TIMEOUT = 3 * 1000;
    protected static final int READ_TIMEOUT = 10 * 1000;
    protected static final int WRITE_TIMEOUT = 10 * 1000;

    protected static final boolean SAVE_TO_CACHE = false;

    protected static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    // ======================

    // GET方法
    protected static Response getByJSON(String url, JSONObject jo)
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
    protected static Response postJSON(String url, JSONObject jo)
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

    protected static JSONObject postForJSONObject(String action, JSONObject jo)
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

    protected static Bitmap postForBitmap(String action, JSONObject jo)
    {
        Bitmap bitmap = null;

        Response response = postJSON(API_PATH + action, jo);
        if (response != null && response.isSuccessful()) {
            InputStream is = response.body().byteStream();
            bitmap = BitmapFactory.decodeStream(is);
        }

        return bitmap;
    }

    protected static JSONObject postImages(String action, JSONObject jo, List<String> imageUrls)
    {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addPart(RequestBody.create(JSON, jo.toString()));

        int count = 0;
        for (String imageUrl : imageUrls) {
            File file = new File(imageUrl);
            builder.addFormDataPart(count++ + "", file.getName(), RequestBody.create(JPEG, file));
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

    protected static void saveInputStreamToCache(InputStream is, String[] dirs, String fileName)
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

    protected static void saveBitmapToCache(Bitmap bitmap, String[] dirs, String fileName)
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static ArrayList<String> postForArrayList(String action, JSONObject jo)
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

    protected static ArrayList<JSONObject> postForArrayListOfJSONObject(String action, JSONObject jo)
    {
        ArrayList<JSONObject> arrayList = null;
        String jsonString = null;
        try {
            Response response = postJSON(API_PATH + action, jo);
            if (response != null && response.isSuccessful()) {
                JSONObject joToReturn = new JSONObject(response.body().string());
                jsonString = joToReturn.getString("jsonString");
                JSONArray ja = new JSONArray(jsonString);
                arrayList = new ArrayList<JSONObject>();
                for (int i = 0; i < ja.length(); ++i) {
                    arrayList.add(ja.getJSONObject(i));
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    protected static InputStream BitmapToInputStream(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
}