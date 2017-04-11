package com.goldfish.sevenseconds.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.adapter.MyTimelineAdapter;
import com.goldfish.sevenseconds.adapter.SearchMemAdapter;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.fragment.SquareFragment;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MemorySheetPreview;
import com.goldfish.sevenseconds.item.MyReviewItem;
import com.goldfish.sevenseconds.item.MyTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.tools.PullToRefreshRecyclerView;
import com.goldfish.sevenseconds.tools.ScrollSpeedLinearLayoutManger;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by bytrain on 2017/3/31.
 */

public class SearchActivity extends Activity{
    boolean year;
    String query;
    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private SearchMemAdapter mAdapter;
    private List<String> allmem = new ArrayList<String>();
    private String name;
    private List<MemoryContext> memlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView editMemory;
    private View view;
    private boolean ifStart = false;
    //XRefreshView xRefreshView;

    /*
    ** 时间轴
     */
    private MyTimelineAdapter myTimelineAdapter;
    private RecyclerView recyclerView1;
    private Orientation orientation;
    private List<MyTimelineItem> myTimelineItems = new ArrayList<>();
    private int lastVisibleItem;
    private int firstVisibleItem;
    private int currentVisibleItem;
    private TextView nextYear;
    private TextView lastYear;
    private String[] months = {"Feb", "Jan" ,"Dec", "Nov", "Oct", "Sept", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov"};
    static private String collectTime = "1999-01";
    private String monthStr;
    static private ImageView addOne;
    static public SearchActivity searchActivity;
    private ProgressDialog progressDialog;



    // 异步获取忆单相关信息
    class refresh extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "Failed";
            if (params[0].equals("getAllMemoryList")) { result = getAllMemoryList(); }
            else if (params[0].equals("getSingleMemory")) { result = getSomeMemory(); }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in getAllMemoryList")) { refreshGetAllMemory(); }
            else if (result.equals("Succeed in getSomeMemory")) { refreshPreviewMemory(); }
            else if (result.equals("没有忆单")) { refreshFailed(result); }
        }
    }
    private void refreshFailed(String result) {
        Toast.makeText(SearchActivity.this, result, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    // 获取忆单ID后的操作
    private void refreshGetAllMemory() {
        new SearchActivity.refresh().execute("getSingleMemory");
    }
    // 获取5条忆单的内容
    private String getSomeMemory() {
        String result = "Succeed in getSomeMemory";
        if (allmem.size() >= 5) {
            for (int i = 0; i < 5; i++) {
                if (!getSingleMemory(i)) break;
            }
        } else {
            for (int i = 0; i < allmem.size(); i++) {
                if (!getSingleMemory(i)) break;
            }
        }
        if (allmem.size() == 0)
            result = "没有忆单";
        return result;
    }
    // 获取一条忆单的内容
    private boolean getSingleMemory(int index) {
        try {
            JSONObject jo = new JSONObject();
            jo.put("memoryId", allmem.get(index));
            JSONObject jo_return = MemoryHttpUtil.getMemory(jo);
            MemoryContext memoryContext = new MemoryContext();
            if (jo_return.getBoolean("ok")) {
                memoryContext.setTitle(jo_return.getString("title"));
                memoryContext.setTime(jo_return.getString("time"));
                String[] labels = jo_return.getString("labels").split(",");
                memoryContext.setLabel(labels);
                memoryContext.setContext(jo_return.getString("content"));
                memoryContext.setReviewCount(jo_return.getInt("reviewCount"));
                memoryContext.setCollectCount(jo_return.getInt("collectCount"));
                memoryContext.setLikeCount(jo_return.getInt("likeCount"));
                memoryContext.setAuthor(jo_return.getString("author"));
                memoryContext.setMemoryId(allmem.get(index));
                jo = new JSONObject();
                jo.put("memoryId", allmem.get(index));
                jo.put("i", 0);
                memoryContext.setCover(MemoryHttpUtil.getMemoryImg(jo));
            } else {
                return false;
            }
            JSONObject jo_review = MemoryHttpUtil.getCommentCount(jo);
            JSONObject jo_like = MemoryHttpUtil.getLikeCount(jo);
            JSONObject jo_add = MemoryHttpUtil.getCollectCount(jo);
            jo.put("account", LogActivity.user);
            JSONObject jo_isLike =  UserHttpUtil.ifLikeMemory(jo);
            JSONObject jo_isAdd = UserHttpUtil.ifCollectMemory(jo);
            if (jo_like.getBoolean("ok") && jo_review.getBoolean("ok") && jo_add.getBoolean("ok")) {
                memoryContext.setLikeCount(jo_like.getInt("count"));
                memoryContext.setReviewCount(jo_review.getInt("count"));
                memoryContext.setIsAdd(jo_isAdd.getBoolean("ok"));
                memoryContext.setIsLike(jo_isLike.getBoolean("ok"));
                memoryContext.setCollectCount(jo_add.getInt("count"));
            }
            else {
                return false;
            }
            memlist.add(memoryContext);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // 更新预览界面
    private void refreshPreviewMemory() {
        mAdapter = new SearchMemAdapter(memlist, this);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }
    // 获取时间轴的时间
    static public String getCollectTime() {
        return collectTime;
    }
    public void Exception(){
        //避免出现android.os.NetworkOnMainThreadException异常
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }
    // 获取所有忆单ID
    private String getAllMemoryList() {
        Intent getData = getIntent();
        query = getData.getStringExtra("querydata");
        year = getData.getBooleanExtra("year", false);
        JSONObject jo = new JSONObject();
        try {
            jo.put("query", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }if(!year){
            allmem = MemoryHttpUtil.searchMemorys(jo);
        }else {
            allmem = MemoryHttpUtil.searchMemorysViaDecade(jo);
        }

        String result = "没有忆单";
        if(allmem != null) {
            Log.d("SS",""+allmem.size());
            if (allmem.size() > 0) {
                result = "Succeed in getAllMemoryList";
            }
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle a){
        super.onCreate(a);
        Intent getData = getIntent();
        query = getData.getStringExtra("querydata");
        year = getData.getBooleanExtra("year", false);
        setContentView(R.layout.activity_search);

        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage("正在搜索中...");
        progressDialog.setCanceledOnTouchOutside(false);

        //Toast.makeText(getContext(), "query：" + query + "year:" + year, Toast.LENGTH_LONG).show();
        Exception();
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

         /*
        ** 时间轴
         */
        orientation = Orientation.horizontal;
        recyclerView1 = (RecyclerView) findViewById(R.id.square_timeline);
        final ScrollSpeedLinearLayoutManger mLayoutManager = new ScrollSpeedLinearLayoutManger(recyclerView1.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setSpeedSlow();
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.setHasFixedSize(true);
        initView();
        lastVisibleItem = 0;
        firstVisibleItem = 0;
        currentVisibleItem = 0;
        lastYear = (TextView) findViewById(R.id.square_last_year);
        nextYear = (TextView) findViewById(R.id.square_next_year);
        recyclerView1.addOnScrollListener(new  RecyclerView.OnScrollListener() {

            // 状态改变的时候调用函数
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem + 1 == myTimelineAdapter.getItemCount()) {
                    recyclerView.smoothScrollToPosition(lastVisibleItem);
                    recyclerView.scrollToPosition(1);
                    Log.d(months[currentVisibleItem], String.valueOf(currentVisibleItem));
                    nextYear.setText(String.valueOf(Integer.parseInt(nextYear.getText().toString()) - 1));
                    lastYear.setText(String.valueOf(Integer.parseInt(lastYear.getText().toString()) - 1));
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        firstVisibleItem == 0) {
                    recyclerView.smoothScrollToPosition(lastVisibleItem);
                    View childView=recyclerView.getChildAt(0);
                    int top=childView.getLeft();
                    int topEdge=recyclerView.getPaddingLeft();
                    if(top >= topEdge){
                        recyclerView.scrollToPosition(14);
                        nextYear.setText(String.valueOf(Integer.parseInt(nextYear.getText().toString()) + 1));
                        lastYear.setText(String.valueOf(Integer.parseInt(lastYear.getText().toString()) + 1));
                    }
                    Log.d(months[currentVisibleItem], String.valueOf(currentVisibleItem));
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView.smoothScrollToPosition(lastVisibleItem);
                }
                collectTime = String.valueOf(Integer.parseInt(nextYear.getText().toString()) + 1) + "-" + monthStr;
            }

            // 滚动结束的时候调用函数
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                currentVisibleItem = firstVisibleItem + 1;
                int month = 1;
                if (currentVisibleItem >= 2 && currentVisibleItem <= 13) {
                    month = 14 - currentVisibleItem;
                }
                else if (currentVisibleItem == 1) {
                    month = 1;
                }
                else if (currentVisibleItem == 0) {
                    month = 2;
                }
                else if (currentVisibleItem == 14) {
                    month = 2;
                }else if (currentVisibleItem == 15) {
                    month = 1;
                }
                if (month <=9 ) {
                    monthStr = "0";
                }
                monthStr += String.valueOf(month);
            }
        });
        addOne = (ImageView) findViewById(R.id.square_add_one);

        mPullRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.hor_rec_refresh);
        mRecyclerView = mPullRefreshRecyclerView.getRefreshableView();
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //Toast.makeText(BarActivity.barActivity, "Pull left!", Toast.LENGTH_SHORT).show();
                mPullRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //Toast.makeText(BarActivity.barActivity, "Pull right!", Toast.LENGTH_SHORT).show();
                mPullRefreshRecyclerView.onRefreshComplete();
            }
        });
        progressDialog.show();
        new SearchActivity.refresh().execute("getAllMemoryList");

    }
    public static void showAddOne() {
        addOne.setVisibility(View.VISIBLE);
        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f,0.0f,-100.0f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);
        set.setDuration(1000);
        addOne.startAnimation(set);
        addOne.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ifStart) {
            mAdapter.refreshStartCount();
        } else{
            ifStart = true;
        }

    }

    private void initView() {
        for (int i = 0; i < 16; i++) {
            MyTimelineItem myTimelineItem = new MyTimelineItem();
            myTimelineItem.setMonth(months[i]);
            myTimelineItems.add(myTimelineItem);
        }
        myTimelineAdapter = new MyTimelineAdapter(myTimelineItems, orientation);
        recyclerView1.setAdapter(myTimelineAdapter);
    }
}
