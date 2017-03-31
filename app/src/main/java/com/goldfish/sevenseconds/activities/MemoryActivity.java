package com.goldfish.sevenseconds.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MyTimelineAdapter;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.bean.TitleBarInfo;
import com.goldfish.sevenseconds.item.MyTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.service.NetWorkUtils;
import com.goldfish.sevenseconds.tools.Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MemoryActivity extends AppCompatActivity {

    /**
     *  TitleBar
     */
    // 控件
    private RelativeLayout userInfo;
    private Button followIt;
    private Button unFollowIt;
    private ImageView userFace;
    private TextView userName;

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
    private ImageView contextCover;
    private TextView contextLabel;
    private TextView contextTitle;
    private TextView contextTime;
    private ImageView likeMemory;
    private RelativeLayout contextMain;

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

    /*
    ** 时间轴
     */
    private MyTimelineAdapter myTimelineAdapter;
    private RecyclerView recyclerView;
    private Orientation orientation;
    private List<MyTimelineItem> myTimelineItems = new ArrayList<>();
    private int lastVisibleItem;
    private int firstVisibleItem;
    private int currentVisibleItem;
    private String[] months = { "Nov" ,"Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec", "Jan", "Feb" };

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
        memID = "1490948766965";

        /*
        ** 时间轴
         */
        // 控件
        orientation = Orientation.horizontal;
        recyclerView = (RecyclerView) findViewById(R.id.my_timeline);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        initView();

        /**
         *  顶部标题栏
         */
        // 控件
        unFollowIt = (Button) findViewById(R.id.amem_title_unfollow_btn);
        followIt = (Button) findViewById(R.id.amem_title_follow_btn);
        userInfo = (RelativeLayout) findViewById(R.id.amem_title_info);
        userFace = (ImageView) findViewById(R.id.amem_title_face);
        userName = (TextView) findViewById(R.id.amem_title_name);

        // 数据
        titleBarFinished = false;
        hadFollowed = false;
        titleBarInfo = new TitleBarInfo();

        // 加载titleBar
        downTask = new DownTask();
        downTask.execute("titleBar");

        // 时间轴
        lastVisibleItem = 0;
        firstVisibleItem = 0;
        currentVisibleItem = 0;

        recyclerView.setOnScrollListener(new  RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem + 1 == myTimelineAdapter.getItemCount()) {
                    recyclerView.smoothScrollToPosition(lastVisibleItem);
                    recyclerView.scrollToPosition(1);
                    initData();
                    currentVisibleItem = mLayoutManager.findFirstVisibleItemPosition() + 1;
                    TextView textView = (TextView) mLayoutManager.findViewByPosition(currentVisibleItem).findViewById(R.id.my_timeline_month);
                    textView.setText(months[0] + "1998");
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        firstVisibleItem == 0) {
                    mLayoutManager.scrollToPositionWithOffset(firstVisibleItem, 0);
                    recyclerView.scrollToPosition(14);
                    initData();
                    currentVisibleItem = mLayoutManager.findFirstVisibleItemPosition() + 1;
                    TextView textView = (TextView) mLayoutManager.findViewByPosition(currentVisibleItem).findViewById(R.id.my_timeline_month);
                    textView.setText(months[11] + "1998");
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView.smoothScrollToPosition(lastVisibleItem);
                    TextView textView = (TextView) mLayoutManager.findViewByPosition(currentVisibleItem).findViewById(R.id.my_timeline_month);
                    initData();
                    textView.setText(months[currentVisibleItem - 2] + "1998");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                currentVisibleItem = mLayoutManager.findFirstVisibleItemPosition() + 1;
            }
        });

        // 点击作者信息进入作者主页
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && titleBarFinished) {
                    Intent intent = new Intent(MemoryActivity.this,
                            UserHomePageActivity.class);
                    startActivity(intent);
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemoryActivity.this,
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
                    Toast.makeText(MemoryActivity.this,
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
                    Toast.makeText(MemoryActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         *  底部导航栏
         */
        barEdit = (TextView) findViewById(R.id.nav_bar_edit);
        barMsg = (ImageView) findViewById(R.id.nav_bar_review);
        barShare = (ImageView) findViewById(R.id.nav_bar_add);
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
                    Intent intent = new Intent(MemoryActivity.this, MemoryReviewActivity.class);
                    startActivity(intent);
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemoryActivity.this,
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
                    Toast.makeText(MemoryActivity.this,
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
                    Toast.makeText(MemoryActivity.this, "该功能敬请期待！", Toast.LENGTH_SHORT).show();
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemoryActivity.this,
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
        contextCover = (ImageView) findViewById(R.id.amem_cover);
        contextLabel = (TextView) findViewById(R.id.amem_label);
        contextTitle = (TextView) findViewById(R.id.amem_title);
        contextTime = (TextView) findViewById(R.id.amem_time);
        contextMain = (RelativeLayout) findViewById(R.id.amem_main_context);

        // 数据
        memoryContext = new MemoryContext();
        contextFinished = false;

        // 加载忆单主体内容
        downTask = new DownTask();
        downTask.execute("getContext");
    }

    private void initView() {
        setDataListItems();
        myTimelineAdapter = new MyTimelineAdapter(myTimelineItems, orientation);
        recyclerView.setAdapter(myTimelineAdapter);
    }

    private void initData() {
        myTimelineItems.get(0).setMonth(months[10]);
        myTimelineItems.get(1).setMonth(months[11]);
        for (int i = 2; i < 14; i++) {
            myTimelineItems.get(i).setMonth(months[i - 2]);
        }
        myTimelineItems.get(14).setMonth(months[0]);
        myTimelineItems.get(15).setMonth(months[1]);
        myTimelineAdapter = new MyTimelineAdapter(myTimelineItems, orientation);
        recyclerView.setAdapter(myTimelineAdapter);
    }

    private void setDataListItems() {
        for (int i = 0; i < 16; i++) {
            MyTimelineItem myTimelineItem = new MyTimelineItem();
            myTimelineItem.setMonth(months[i]);
            myTimelineItems.add(myTimelineItem);
        }
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
            JSONObject jo_return = Http.getUserInfo(jo);
            if (jo_return.getBoolean("ok")) {
                titleBarInfo.setName(jo_return.getString("username"));
                result = "Succeed in titleBar";
            }
            else {
                titleBarInfo.setErrMsg(jo_return.getString("errMsg"));
                result = jo_return.getString("errMsg");
            }
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
            if (jo_return.getBoolean("ok")) {
                memoryContext.setTitle(jo_return.getString("title"));
                memoryContext.setTime(jo_return.getString("time"));
                String[] labels = jo_return.getString("labels").split(",");
                memoryContext.setLabel(labels);
                memoryContext.setContext(jo_return.getString("content"));
                memoryContext.setReviewCount(jo_return.getInt("reviewCount"));
                memoryContext.setCollectCount(jo_return.getInt("collectCount"));
                memoryContext.setLikeCount(jo_return.getInt("likeCount"));
                jo = new JSONObject();
                jo.put("memoryId", memID);
                jo.put("i", 0);
                memoryContext.setCover(Http.getMemoryImg(jo));
                result = "Succeed in context";
            } else {
                result = "加载忆单出错";
            }
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
        contextTitle.setText(memoryContext.getTitle());
        contextTime.setText(memoryContext.getTime());
        contextCover.setImageBitmap(memoryContext.getCover());
        String labels = "";
        for (int i = 0; i < memoryContext.getLabel().length; i++) {
            labels += "#" + memoryContext.getLabel()[i] + " ";
        }
        getMainContext();
        contextLabel.setText(labels);
        contextFinished = true;
    }

    // 抓取正文
    private void getMainContext() {
        String mainContext = memoryContext.getContext();
        String[] text = new String[100];
        int textCount = 0;
        ArrayList<Integer> image = new ArrayList<Integer>();
        int imageCount = 0;
        String temp;
        String tempImage;
        int lastpos1 = 0;
        int pos1 = 0;
        int pos2 = 0;
        int isValid;
        while (true) {
            lastpos1 = pos1;
            pos1 = mainContext.indexOf("<", pos1);
            if (pos1 == -1) {
                if (text[textCount] == null) text[textCount] = "";
                text[textCount] += mainContext.substring(lastpos1);
                textCount++;
                break;
            } else {
                temp = mainContext.substring(lastpos1, pos1);
                if (text[textCount] == null) text[textCount] = "";
                text[textCount] += temp;
            }
            pos2 = mainContext.indexOf(">", pos1 + 1);
            if (pos2 == -1) {
                text[textCount] = mainContext.substring(lastpos1);
                textCount++;
                break;
            }
            tempImage = mainContext.substring(pos1, pos2 + 1);
            isValid = tempImage.indexOf("img", 0);
            if (isValid != -1) {
                String regEx = "[^0-9]";//匹配指定范围内的数字
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(tempImage);
                String string = m.replaceAll(" ").trim();
                String[] strArr = string.split(" ");
                image.add(imageCount, Integer.parseInt(strArr[0]));
                textCount++;
                // 标志要放图
                text[textCount++] = "<img>";
                imageCount++;
            } else {
                if (text[textCount] == null) text[textCount] = "";
                text[textCount]+= tempImage;
            }
            pos1 = pos2 + 1;
        }
        /*int imageViewCount = 0;
        int textViewCount = 0;
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals("<img>")) {
                ImageView img = new ImageView(this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, 200);
                img.setId(imageViewCount + 100);
                img.setImageResource(R.drawable.memory_test);

            }
        }*/
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
                Toast.makeText(MemoryActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        // 在调用AsyncTask的cancel()方法时调用。
        @Override
        protected void onCancelled() {}
    }
}
