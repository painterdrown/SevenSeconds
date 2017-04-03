package com.goldfish.sevenseconds.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.transition.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.AmemReviewAdapter;
import com.goldfish.sevenseconds.adapter.MyTimelineAdapter;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.bean.TitleBarInfo;
import com.goldfish.sevenseconds.item.AmemReviewItem;
import com.goldfish.sevenseconds.item.MyTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.service.NetWorkUtils;
import com.goldfish.sevenseconds.tools.Http;
import com.goldfish.sevenseconds.view.ReviewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private ImageView editMemory;
    private ImageView back;

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
    private ImageView barAdd;

    /**
     *  Context
     */
    // 控件
    private ImageView contextCover;
    private TextView contextLabel;
    private TextView contextTitle;
    private TextView contextTime;
    private LinearLayout contextMain;
    private ArrayList<Integer> image;
    private ArrayList<Integer> imagePosition;
    private Bitmap[] bitImages;
    private TextView countLikeTv;
    private TextView countAddTv;
    private TextView countReviewTv;

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

    /*
    ** 评论区
     */
    private List<AmemReviewItem> reviewItemList = new ArrayList<>();
    private RecyclerView recyclerViewReview;
    private String editContext;
    private ReviewDialog reviewDialog;

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
        memID = "1491188366992";

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
        back = (ImageView) findViewById(R.id.amem_back);
        editMemory = (ImageView) findViewById(R.id.amem_edit);

        // 数据
        titleBarFinished = false;
        hadFollowed = false;
        titleBarInfo = new TitleBarInfo();

        // 加载titleBar
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemoryActivity.this, Addmem.class);
                startActivity(intent);
            }
        });

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
        barAdd = (ImageView) findViewById(R.id.nav_bar_add);
        barLike = (ImageView) findViewById(R.id.nav_bar_like);
        navBarFinished = false;

        // 查看评论
        barMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) {
                    TextView textView = (TextView) findViewById(R.id.amem_review_title1);
                    textView.setFocusableInTouchMode(false);
                    textView.setFocusableInTouchMode(true);
                    textView.requestFocus();
                }
            }
        });

        // 点赞
        barLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && navBarFinished) {
                    if (memoryContext.getIsLike()) {
                        downTask = new DownTask();
                        downTask.execute("dislike");
                    }
                    else {
                        downTask = new DownTask();
                        downTask.execute("Like the memory");
                    }
                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemoryActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 添加到我的收藏
        barAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((NetWorkUtils.getAPNType(context) != 0) && navBarFinished) {
                    if (memoryContext.getIsAdd()) {
                        downTask = new DownTask();
                        downTask.execute("Sub");
                    }
                    else {
                        downTask = new DownTask();
                        downTask.execute("Show in my favorites");
                    }

                }
                else if (NetWorkUtils.getAPNType(context) == 0) {
                    Toast.makeText(MemoryActivity.this,
                            "哎呀~网络连接有问题！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        reviewDialog = new ReviewDialog(context);
        // 编辑评论
        barEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navBarFinished) {
                    reviewDialog.show();
                    reviewDialog.setClickListenerInterface(new ReviewDialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm() {
                            editContext = reviewDialog.getEdit();
                            downTask = new DownTask();
                            downTask.execute("deliverReview");
                        }
                    });
                }
            }
        });

        /**
         * 忆单主体内容
         */
        contextCover = (ImageView) findViewById(R.id.amem_cover);
        contextLabel = (TextView) findViewById(R.id.amem_label);
        contextTitle = (TextView) findViewById(R.id.amem_title);
        contextTime = (TextView) findViewById(R.id.amem_time);
        contextMain = (LinearLayout) findViewById(R.id.amem_main_context);
        countAddTv = (TextView) findViewById(R.id.add_count);
        countLikeTv = (TextView) findViewById(R.id.like_count);
        countReviewTv = (TextView) findViewById(R.id.review_count);

        // 数据
        memoryContext = new MemoryContext();
        contextFinished = false;

        // 加载忆单主体内容
        downTask = new DownTask();
        downTask.execute("getContext");

        // 加载评论区
        downTask = new DownTask();
        downTask.execute("getReview");
    }

    private void initReview() {
        /*Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        for (int i = 0; i < 2; i++) {
            AmemReviewItem amemReviewItem = new AmemReviewItem(os.toByteArray(),
                    "穿睡服的金鱼", "这周APP上线啦，今晚整合",
                    "2017-2-24", "noend22", "100");
            reviewItemList.add(amemReviewItem);
        }*/
        recyclerViewReview = (RecyclerView) findViewById(R.id.amem_review_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MemoryActivity.this);
        recyclerViewReview.setLayoutManager(layoutManager);
        AmemReviewAdapter adapter = new AmemReviewAdapter(reviewItemList);
        recyclerViewReview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            jo.put("account", myAccount);
            JSONObject jo_return = Http.likeMemory(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in like memory";
                memoryContext.setIsLike(true);
            }
            else {
                result = "Failed in like Memory";
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "Failed in like Memory";
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
        String result = "Failed in navBar";
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            JSONObject jo_review = Http.getCommentCount(jo);
            JSONObject jo_like = Http.getLikeCount(jo);
            jo.put("account", myAccount);
            JSONObject jo_isLike = Http.ifLikeMemory(jo);
            JSONObject jo_isAdd = Http.ifCollectMemory(jo);
            if (jo_like.getBoolean("ok") && jo_review.getBoolean("ok")) {
                memoryContext.setLikeCount(jo_like.getInt("count"));
                memoryContext.setReviewCount(jo_review.getInt("count"));
                memoryContext.setIsAdd(jo_isAdd.getBoolean("ok"));
                memoryContext.setIsLike(jo_isLike.getBoolean("ok"));
                result = "Succeed in navBar";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                result = jo_return.getString("errMsg");
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void refreshNavBar() {
        if (memoryContext.getCollectCount() > 0) {
            if (memoryContext.getCollectCount() <= 99) {
                countAddTv.setText(String.valueOf(memoryContext.getCollectCount()));
                countAddTv.setVisibility(View.VISIBLE);
            }
            else {
                countAddTv.setText("99+");
                countAddTv.setVisibility(View.VISIBLE);
            }
        }
        else {
            countAddTv.setVisibility(View.INVISIBLE);
        }
        if (memoryContext.getLikeCount() > 0) {
            if (memoryContext.getLikeCount() <= 99) {
                countLikeTv.setText(String.valueOf(memoryContext.getLikeCount()));
                countLikeTv.setVisibility(View.VISIBLE);
            }
            else {
                countLikeTv.setText("99+");
                countLikeTv.setVisibility(View.VISIBLE);
            }
        }
        else {
            countLikeTv.setVisibility(View.INVISIBLE);
        }
        if (memoryContext.getReviewCount() > 0) {
            if (memoryContext.getLikeCount() <= 99) {
                countReviewTv.setText(String.valueOf(memoryContext.getReviewCount()));
                countReviewTv.setVisibility(View.VISIBLE);
            }
            else {
                countReviewTv.setText("99+");
                countReviewTv.setVisibility(View.VISIBLE);
            }
        }
        else {
            countReviewTv.setVisibility(View.INVISIBLE);
        }
        if (memoryContext.getIsLike()) {
            barLike.setAlpha((float) 0.9);
            barLike.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
        }
        else {
            barLike.setAlpha((float) 0.4);
            barLike.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }
        if (memoryContext.getIsAdd()) {
            barAdd.setAlpha((float) 0.9);
            barAdd.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
        }
        else {
            barAdd.setAlpha((float) 0.4);
            barAdd.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }
        navBarFinished = true;
    }

    // 更新忆单内容UI
    private void refreshContext() {
        contextTitle.setText(memoryContext.getTitle());
        contextTime.setText(memoryContext.getTime());
        contextCover.setImageBitmap(memoryContext.getCover());
        String labels = "";
        for (int i = 0; i < memoryContext.getLabel().length; i++) {
            if (memoryContext.getLabel()[i].equals("")) {
                labels += "#" + memoryContext.getLabel()[i] + " ";
            }
        }
        if (labels.equals("# ")) {
            labels = "";
        }
        downTask = new DownTask();
        downTask.execute("navBar");
        getMainContext();
        contextLabel.setText(labels);
        contextFinished = true;
    }

    // 抓取正文
    private void getMainContext() {
        String mainContext = memoryContext.getContext();
        String[] text = new String[100];
        int textCount = 0;
        image = new ArrayList<Integer>();
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
        imagePosition = new ArrayList<>();
        for (int i = 0; i < textCount; i++) {
            if (text[i].equals("<img>")) {
                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 10;
                imagePosition.add(i);
                img.setId(i);
                img.setImageResource(R.drawable.app_icon);
                img.setLayoutParams(lp);
                img.setAdjustViewBounds(true);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                contextMain.addView(img);
            } else {
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 10;
                textView.setId(i);
                textView.setText(text[i]);
                textView.setLayoutParams(lp);
                contextMain.addView(textView);
            }
        }
        downTask = new DownTask();
        downTask.execute("getMemoryImg");
    }

    private String getMemoryImg() {
        String result = "获取图片失败";
        bitImages = new Bitmap[image.size()];
        for (int i = 0; i < image.size(); i++) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("memoryId", memID);
                jo.put("i", image.get(i));
                Bitmap temp = Http.getMemoryImg(jo);
                if (temp != null) {
                    bitImages[i] = temp;
                    result = "Succeed in getting memory images";
                } else {
                    result = "获取图片失败";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                result = "获取图片失败";
            }
        }
        return result;
    }

    private void refreshMemoryImages() {
        for (int i = 0; i < imagePosition.size(); i++) {
            ImageView imageView = (ImageView) contextMain.findViewById(imagePosition.get(i));
            imageView.setImageBitmap(bitImages[i]);
        }
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

    // 发表评论
    private String deliverReview() {
        String result = "Failed in deliver review";
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            jo.put("account", myAccount);
            jo.put("content", editContext);
            JSONObject jo_return = Http.addComment(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in deliver review";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 更新评论
    private void refreshMemoryReview() {
        reviewDialog.setEditText("");
        reviewDialog.dismiss();
        reviewItemList.clear();
        DownTask navBar = new DownTask();
        navBar.execute("navBar");
        downTask = new DownTask();
        downTask.execute("getReview");
    }

    // 更新所有评论
    private void refreshAllReview() {
        initReview();
    }

    // 获取评论
    private String getReview() {
        String result = "Failed in getting review";
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", memID);
            ArrayList<String> commentList = Http.getCommentList(jo);
            if (!commentList.isEmpty()) {
                for (int i = 0; i < commentList.size(); i++) {
                    if (!commentList.get(i).isEmpty()) {
                        jo = new JSONObject();
                        jo.put("commentId", commentList.get(i));
                        JSONObject jo_return = Http.getComment(jo);
                        if (jo_return.getBoolean("ok")) {
                            jo = new JSONObject();
                            jo.put("account", jo_return.getString("account"));
                            JSONObject user_return = Http.getUserInfo(jo);
                            if (user_return.getBoolean("ok")) {
                                Bitmap face = Http.getUserFace(jo);
                                AmemReviewItem amemReviewItem = new AmemReviewItem(face,
                                        user_return.getString("username"),
                                        jo_return.getString("content"),
                                        jo_return.getString("time"),
                                        jo_return.getString("account"));
                                reviewItemList.add(amemReviewItem);
                                result = "Succeed in getting review";
                            }
                            else {
                                result = "Failed in getting review";
                                break;
                            }
                        }
                        else {
                            result = "Failed in getting review";
                            break;
                        }
                    }
                    else {
                        result = "Failed in getting review";
                        break;
                    }
                }
            }
            else {
                result = "Failed in getting review";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "Failed in getting review";
        }
        return result;
    }

    // 取消点赞
    private String dislike() {
        String result = "Failed in unlike";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", myAccount);
            jo.put("memoryId", memID);
            JSONObject jo_return = Http.unlikeMemory(jo);
            if (jo_return.getBoolean("ok")) {
                memoryContext.setIsLike(false);
                result = "Succeed in unlike";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 取消收藏
    private String sub() {
        String result = "Failed in sub";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", myAccount);
            jo.put("memoryId", memID);
            JSONObject jo_return = Http.uncollectMemory(jo);
            if (jo_return.getBoolean("ok")) {
                memoryContext.setIsAdd(false);
                result = "Succeed in sub";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 更新底部导航栏
    private void refreshNavBarTotally() {
        downTask = new DownTask();
        downTask.execute("navBar");
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
            else if (params[0].equals("getMemoryImg")) { result = getMemoryImg(); }
            else if (params[0].equals("deliverReview")) { result = deliverReview(); }
            else if (params[0].equals("getReview")) { result = getReview(); }
            else if (params[0].equals("dislike")) { result = dislike(); }
            else if (params[0].equals("Sub")) { result = sub(); }
            return result;
        }

        // 在doInBackground()中调用publishProgress()方法更新任务的执行进度,在主线程操作
        @Override
        protected void onProgressUpdate(Integer... Progress) { }

        //doInBackground结束后回调该方法，结束。
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in titleBar")) { refreshTitleBarUI(); }
            else if(result.equals("Succeed in navBar")) { refreshNavBar(); }
            else if (result.equals("Succeed in context")) { refreshContext(); }
            else if (result.equals("Succeed in following")) { refreshFollowButton(); }
            else if (result.equals("Succeed in unfollowing")) { refreshUnfollowButton(); }
            else if (result.equals("Succeed in getting memory images")) { refreshMemoryImages();}
            else if (result.equals("Succeed in deliver review")) { refreshMemoryReview(); }
            else if (result.equals("Succeed in getting review")) { refreshAllReview(); }
            else if (result.equals("Succeed in unlike")) { refreshNavBarTotally(); }
            else if (result.equals("Succeed in sub")) { refreshNavBarTotally(); }
            else if (result.equals("Succeed in like memory")) { refreshNavBarTotally(); }
            else {
                Toast.makeText(MemoryActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        // 在调用AsyncTask的cancel()方法时调用。
        @Override
        protected void onCancelled() {}
    }
}
