package com.goldfish.sevenseconds.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.bean.MemorySheet;
import com.goldfish.sevenseconds.bean.TitleBarInfo;
import com.goldfish.sevenseconds.tools.Http;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ResponseCache;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MemorySheetActivity extends AppCompatActivity {

    // TitleBar
    private RelativeLayout userInfo;
    private Button followIt;
    private Button unFollowIt;
    private ImageView userFace;
    private TextView userName;
    private TextView userIntroduction;
    private String titleName;
    private String titleIntro;
    private byte[] titleFace;
    private String myAccount;

    // Nav
    private TextView barEdit;
    private ImageView barMsg;
    private ImageView barLike;
    private ImageView barShare;

    // Context
    private RelativeLayout contextCover;
    private RelativeLayout contextLabel;
    private TextView contextTitle;
    private TextView contextTime;
    private LinearLayout contextDetail;
    private Bitmap memCover;
    private String memTitle;
    private String[] memLabel;
    private String memTime;
    private String memContext;
    private int reviewCount;
    private int collectCount;
    private int likeCount;

    private ImageView likeMemory;

    private Context context;
    private DownTask downTask;
    private String memAccount;
    private String memID;
    private boolean titleBarFinished;
    private boolean navBarFinished;
    private boolean contextFinished;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amem);
        Intent getData = getIntent();
        memAccount = getData.getStringExtra("account");
        memID = getData.getStringExtra("memoryID");
        myAccount = "a"; // 以后全局获取账号
        // for text
        memAccount = "b";



        // TitleBar
        unFollowIt = (Button) findViewById(R.id.amem_title_unfollow_btn);
        followIt = (Button) findViewById(R.id.amem_title_follow_btn);
        userInfo = (RelativeLayout) findViewById(R.id.amem_title_info);
        userFace = (ImageView) findViewById(R.id.amem_title_face);
        userName = (TextView) findViewById(R.id.amem_title_name);
        userIntroduction = (TextView) findViewById(R.id.amem_title_intro);
        titleBarFinished = false;

        // 还要添加一个是否有网络的判断
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleBarFinished) {
                    Intent intent = new Intent(MemorySheetActivity.this, UserHomePageActivity.class);
                    startActivity(intent);
                }
            }
        });

        followIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加到我的关注
                if (titleBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("follow it");
                }
            }
        });

        unFollowIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从我的关注删去
                if (titleBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("unfollow it");
                }
            }
        });

        // 加载titleBar
        downTask = new DownTask();//同一个AsyncTask的execute只能调用一次
        downTask.execute("titleBar");//调用execute后将会回调onPreExecute方法

        // Nav
        barEdit = (TextView) findViewById(R.id.nav_bar_edit);
        barMsg = (ImageView) findViewById(R.id.nav_bar_msg);
        barShare = (ImageView) findViewById(R.id.nav_bar_share);
        barLike = (ImageView) findViewById(R.id.nav_bar_like);
        navBarFinished = false;

        barMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 评论
                if (navBarFinished) {
                    Intent intent = new Intent(MemorySheetActivity.this, MemorySheetReviewActivity.class);
                    startActivity(intent);
                }
            }
        });

        barLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("add memory sheet");
                }
            }
        });

        barShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) {
                    Toast.makeText(MemorySheetActivity.this, "该功能敬请期待！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        barEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) {
                    // 写评论
                }
            }
        });

        // 加载nav
        downTask = new DownTask();
        downTask.execute("navBar");

        // 忆单主体内容
        contextCover = (RelativeLayout) findViewById(R.id.amem_cover);
        contextLabel = (RelativeLayout) findViewById(R.id.amem_label_layout);
        contextTitle = (TextView) findViewById(R.id.memorysheet_title);
        contextTime = (TextView) findViewById(R.id.memorysheet_time);
        contextDetail = (LinearLayout) findViewById(R.id.amem_context);
        contextFinished = false;

        downTask = new DownTask();
        downTask.execute("context");

        likeMemory = (ImageView) findViewById(R.id.amem_like_image_icon);
        likeMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
                    downTask = new DownTask();
                    downTask.execute("like");
                }
            }
        });
    }
    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "failed";
            Gson gson = new Gson();

            // 点赞
            if (params[0].equals("like")) {
                try {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("myUsername", myAccount)
                            .add("otherUsername", memAccount)
                            .add("memoryId", memID).build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request
                            .Builder()
                            .url("http://139.199.158.84:3000/api/likeMemSheet")
                            .post(requestBody).build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        if (jsonObject.getBoolean("ok")) {
                            result = "点赞成功！";
                        } else {
                            result = jsonObject.getString("errMsg");
                        }
                    } else {
                        result = "failed";
                    }
                }
                catch (IOException | JSONException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }

            // 添加关注
            else if (params[0].equals("follow it")) {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("myAccount", myAccount);
                    jo.put("otherAccount", memAccount);
                    JSONObject jo_return = Http.addFollow(jo);
                    if (jo_return.getBoolean("ok")) {
                        result = "success in following it";
                    } else {
                        result = jo_return.getString("errMsg");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }

            // 取消关注
            else if (params[0].equals("unfollow it")) {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("myAccount", myAccount);
                    jo.put("otherAccount", memAccount);
                    JSONObject jo_return = Http.deleteFollow(jo);
                    if (jo_return.getBoolean("ok")) {
                        result = "success in following it";
                    } else {
                        result = jo_return.getString("errMsg");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }

            // 添加忆单
            else if (params[0].equals("add memory sheet")) {
                try {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", myAccount)
                            .add("memoryId", memID).build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request
                            .Builder()
                            .url("http://139.199.158.84:3000/api/collectMemory")
                            .post(requestBody).build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        if (jsonObject.getBoolean("ok")) {
                            result = "success in adding memory sheet";
                        } else {
                            result = jsonObject.getString("errMsg");
                        }
                    } else {
                        result = "failed";
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    result = "failed";
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }

            // 标题栏的点击事件
            else if (params[0].equals("titleBar")) {
                try {
                    TitleBarInfo titleBarInfo;
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", memAccount).build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request
                            .Builder()
                            .url("http://139.199.158.84:3000/api/getUserInfo")
                            .post(requestBody).build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        titleBarInfo = gson.fromJson(responseData, TitleBarInfo.class);
                        if (titleBarInfo.getOk()) {
                            titleName = titleBarInfo.getName();
                            titleIntro = titleBarInfo.getIntroduction();
                            titleFace = titleBarInfo.getFace();
                            result = "success in titleBar";
                        } else {
                            result = titleBarInfo.getErrMsg();
                        }

                    } else {
                        result = "failed";
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }

            // 导航栏的点击事件
            else if (params[0].equals("navBar")) {
                result = "success in navBar";
            }

            // 获得忆单主体
            else if (params[0].equals("context")) {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("memoryId", memID);
                    JSONObject jo_return = Http.getMemory(jo);
                    memTitle = jo_return.getString("title");
                    memTime = jo_return.getString("time");
                    reviewCount = jo_return.getInt("reviewCount");
                    collectCount = jo_return.getInt("collectCount");
                    likeCount = jo_return.getInt("likeCount");
                    result = "success in context";

                    jo = new JSONObject();
                    jo.put("memoryId", memID);
                    jo.put("i", 0);
                    Bitmap return_cover = Http.getMemoryImg(jo);
                    memCover = return_cover;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }
            return result;
        }

        //该回调方法执行完毕后，将会调用doInBackground
        @Override
        protected void onPreExecute() { }

        // 在doInBackground()中调用publishProgress()方法更新任务的执行进度,在主线程操作
        @Override
        protected void onProgressUpdate(Integer... Progress) { }

        //doInBackground结束后回调该方法，结束。
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success in titleBar")) {
                userName.setText(titleName);
                userIntroduction.setText(titleIntro);
                // setFace
                titleBarFinished = true;
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("success in navBar")) {
                navBarFinished = true;
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("success in context")) {
                //contextCover.setBackground();
                //contextLabel;
                contextTitle.setText(memTitle);
                contextTime.setText(memTime);
                //contextDetail;
                contextCover.setBackground(new BitmapDrawable(memCover));
                contextFinished = true;
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("success in following it")) {
                followIt.setVisibility(View.GONE);
                unFollowIt.setVisibility(View.VISIBLE);
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("success in unfollowing it")) {
                unFollowIt.setVisibility(View.GONE);
                followIt.setVisibility(View.VISIBLE);
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        // 在调用AsyncTask的cancel()方法时调用。
        @Override
        protected void onCancelled() {

        }
    }
}
