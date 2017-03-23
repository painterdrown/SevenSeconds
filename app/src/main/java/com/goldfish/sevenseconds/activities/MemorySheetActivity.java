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
import com.goldfish.sevenseconds.service.NetWorkUtils;
import com.goldfish.sevenseconds.tools.Http;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ResponseCache;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MemorySheetActivity extends AppCompatActivity {

    /**
     *  TitleBar
     */
    // 控件
    private RelativeLayout userInfo;
    private Button followIt;
    private Button unFollowIt;
    private ImageView userFace;
    private TextView userName;
    private TextView userIntroduction;

    // 数据
    private TitleBarInfo titleBarInfo;
    private String myAccount;
    private Boolean hadFollowed;

    /**
     * Navigation
     */
    // 控件
    private TextView barEdit;
    private ImageView barMsg;
    private ImageView barLike;
    private ImageView barShare;

    /**
     *  Context
     */
    // 控件
    private RelativeLayout contextCover;
    private RelativeLayout contextLabel;
    private TextView contextTitle;
    private TextView contextTime;
    private ImageView likeMemory;

    // 数据
    private MemoryContext memoryContext;

    /**
     *  通用数据
     */
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
        context = this;
        memAccount = getData.getStringExtra("account");
        memID = getData.getStringExtra("memoryID");

        // 测试临时个人账户和忆单的作者账户
        myAccount = "a";
        memAccount = "b";

        /**
         *  顶部标题栏
         */
        // 控件
        unFollowIt = (Button) findViewById(R.id.amem_title_unfollow_btn);
        followIt = (Button) findViewById(R.id.amem_title_follow_btn);
        userInfo = (RelativeLayout) findViewById(R.id.amem_title_info);
        userFace = (ImageView) findViewById(R.id.amem_title_face);
        userName = (TextView) findViewById(R.id.amem_title_name);
        userIntroduction = (TextView) findViewById(R.id.amem_title_intro);

        // 数据
        titleBarFinished = false;
        hadFollowed = false;
        titleBarInfo = new TitleBarInfo();

        // 加载titleBar
        downTask = new DownTask();
        downTask.execute("titleBar");

        // 点击作者信息进入作者主页
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && titleBarFinished) {
                    Intent intent = new Intent(MemorySheetActivity.this,
                            UserHomePageActivity.class);
                    startActivity(intent);
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 添加到我的关注
        followIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && titleBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("Follow");
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 从我的关注删去
        unFollowIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && titleBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("Unfollow");
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         *  底部导航栏
         */
        barEdit = (TextView) findViewById(R.id.nav_bar_edit);
        barMsg = (ImageView) findViewById(R.id.nav_bar_msg);
        barShare = (ImageView) findViewById(R.id.nav_bar_share);
        barLike = (ImageView) findViewById(R.id.nav_bar_like);
        navBarFinished = false;

        // 加载底部导航栏
        downTask = new DownTask();
        downTask.execute("navBar");

        // 查看评论
        barMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && navBarFinished) {
                    Intent intent = new Intent(MemorySheetActivity.this, MemorySheetReviewActivity.class);
                    startActivity(intent);
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 添加到我的收藏
        barLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && navBarFinished) {
                    downTask = new DownTask();
                    downTask.execute("Show in my favorites");
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 分享
        barShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && navBarFinished) {
                    Toast.makeText(MemorySheetActivity.this, "该功能敬请期待！", Toast.LENGTH_SHORT).show();
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 编辑评论
        barEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) { }
            }
        });

        /**
         * 忆单主体内容
         */
        contextCover = (RelativeLayout) findViewById(R.id.amem_cover);
        contextLabel = (RelativeLayout) findViewById(R.id.amem_label_layout);
        contextTitle = (TextView) findViewById(R.id.memorysheet_title);
        contextTime = (TextView) findViewById(R.id.memorysheet_time);
        likeMemory = (ImageView) findViewById(R.id.amem_like_image_icon);

        // 数据
        memoryContext = new MemoryContext();
        contextFinished = false;

        // 加载忆单主体内容
        downTask = new DownTask();
        downTask.execute("getContext");

        // 点赞该忆单
        likeMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && contextFinished) {
                    downTask = new DownTask();
                    downTask.execute("Like the memory");
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemorySheetActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 点赞该忆单
    private String likeTheMemory() {
        String result;
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
        return result;
    }

    // 添加关注
    private String follow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", myAccount);
            jo.put("otherAccount", memAccount);
            JSONObject jo_return = Http.addFollow(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in following";
            } else {
                result = jo_return.getString("errMsg");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "添加关注失败";
        }
        return result;
    }

    // 取消关注
    private String unfollow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", myAccount);
            jo.put("otherAccount", memAccount);
            JSONObject jo_return = Http.deleteFollow(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in unfollowing";
            } else {
                result = jo_return.getString("errMsg");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "取消关注失败";
        }
        return result;
    }

    // 添加忆单
    private String showInMyFavorites() {
        String result;
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
        catch (IOException | JSONException e) {
            e.printStackTrace();
            result = "failed";
        }
        return result;
    }

    // 标题栏的点击事件
    private String titleBar() {
        String result;
        try {
            // 查看是否已经关注过作者
            JSONObject jo = new JSONObject();
            jo.put("account", myAccount);
            ArrayList<String> myFollowers = Http.getFollowingList(jo);
            for (int i = 0; i < myFollowers.size(); i++) {
                if (myFollowers.get(i).equals(memAccount)) {
                    hadFollowed = true;
                }
            }

            // 获取标题栏数据
            jo = new JSONObject();
            jo.put("account", memAccount);
            titleBarInfo.setFace(Http.getUserFace(jo));
            result = "Succeed in titleBar";
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "Failed";
        }
        return result;
    }

    // 导航栏的点击事件
    private String navBar() {
        String result = "Failed";
        result = "Succeed in navBar";
        return result;
    }

    // 获得忆单主体
    private String getMemoryContext() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            JSONObject jo_return = Http.getMemory(jo);
            memoryContext.setTitle(jo_return.getString("title"));
            memoryContext.setTime(jo_return.getString("time"));
            memoryContext.setReviewCount(jo_return.getInt("reviewCount"));
            memoryContext.setCollectCount(jo_return.getInt("collectCount"));
            memoryContext.setLikeCount(jo_return.getInt("likeCount"));

            jo = new JSONObject();
            jo.put("memoryId", memID);
            jo.put("i", 0);
            memoryContext.setCover(Http.getMemoryImg(jo));

            result = "Succeed in context";
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "加载忆单出错";
        }
        return result;
    }

    // 更新标题栏的UI
    private void refreshTitleBarUI() {
        userName.setText(titleBarInfo.getName());
        userIntroduction.setText(titleBarInfo.getIntroduction());
        userFace.setImageBitmap(titleBarInfo.getFace());
        titleBarFinished = true;
        if (hadFollowed) {
            followIt.setVisibility(View.GONE);
            unFollowIt.setVisibility(View.VISIBLE);
        }
    }

    // 更新导航栏UI
    private void refreshNavBar() {
        navBarFinished = true;
    }

    // 更新忆单内容UI (待完成导航栏UI)
    private void refreshContext() {
        contextTime.setText(memoryContext.getTime());
        contextTitle.setText(memoryContext.getTitle());
        contextCover.setBackground(new BitmapDrawable(memoryContext.getCover()));
        contextFinished = true;
    }

    // 更新关注按钮
    private void refreshFollowButton() {
        followIt.setVisibility(View.GONE);
        unFollowIt.setVisibility(View.VISIBLE);
    }

    // 更新关注按钮
    private void refreshUnfollowButton() {
        unFollowIt.setVisibility(View.GONE);
        followIt.setVisibility(View.VISIBLE);
    }


    /**
     * 异步操作
     */
    class DownTask extends AsyncTask<String, Integer, String> {

        //该回调方法执行完毕后，将会调用doInBackground
        @Override
        protected void onPreExecute() { }

        // 在另一个线程操作
        @Override
        protected String doInBackground(String... params) {
            String result = "Failed";
            if (params[0].equals("Like the memory")) { result = likeTheMemory(); }
            else if (params[0].equals("Follow")) { result = follow(); }
            else if (params[0].equals("Unfollow")) { result = unfollow(); }
            else if (params[0].equals("Show in my favorites")) { result = showInMyFavorites(); }
            else if (params[0].equals("titleBar")) { result = titleBar(); }
            else if (params[0].equals("navBar")) { result = navBar(); }
            else if (params[0].equals("getContext")) { result = getMemoryContext(); }
            return result;
        }

        // 在doInBackground()中调用publishProgress()方法更新任务的执行进度,在主线程操作
        @Override
        protected void onProgressUpdate(Integer... Progress) { }

        //doInBackground结束后回调该方法，结束。
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in titleBar")) { refreshTitleBarUI(); }
            else if(result.equals("Succeed in navBar")) { refreshNavBar(); }
            else if (result.equals("Succeed in context")) { refreshContext(); }
            else if (result.equals("Succeed in following")) { refreshFollowButton(); }
            else if (result.equals("Succeed in unfollowing")) { refreshUnfollowButton(); }
            else {
                Toast.makeText(MemorySheetActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        // 在调用AsyncTask的cancel()方法时调用。
        @Override
        protected void onCancelled() {}
    }
}
