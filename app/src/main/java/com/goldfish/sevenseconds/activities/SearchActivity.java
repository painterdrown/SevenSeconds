package com.goldfish.sevenseconds.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.item.MemorySheetPreview;
import com.goldfish.sevenseconds.item.MyReviewItem;
import com.goldfish.sevenseconds.tools.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by bytrain on 2017/3/31.
 */

public class SearchActivity extends Activity{
    String query;
    private List<String> SearchItems = new ArrayList<>();
    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private MemAdapter mAdapter;
    private String name;
    private List<MemorySheetPreview> memlist = new ArrayList<MemorySheetPreview>();
    private RecyclerView recyclerView;
    private ImageView editMemory;
    private int mLoadCount = 0;

    public void Exception(){
        //避免出现android.os.NetworkOnMainThreadException异常
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    @Override
    protected void onCreate(Bundle a){
        super.onCreate(a);
        Intent getData = getIntent();
        query = getData.getStringExtra("querydata");
        setContentView(R.layout.activity_search);
        Exception();
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPullRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.hor_rec_refresh);
        mRecyclerView = mPullRefreshRecyclerView.getRefreshableView();
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                /*String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // 显示最后更新的时间
                mPullRefreshRecyclerView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);*/
                Toast.makeText(BarActivity.barActivity, "Pull left!", Toast.LENGTH_SHORT).show();
                //new GetDataTask().execute();
                mPullRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                /*
                String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // 显示最后更新的时间
                mPullRefreshRecyclerView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);*/
                Toast.makeText(BarActivity.barActivity, "Pull right!", Toast.LENGTH_SHORT).show();
                //new GetDataTask().execute();
                mPullRefreshRecyclerView.onRefreshComplete();
            }
        });
        for (int i = 0;i < 10; i++){
            MemorySheetPreview memex = new MemorySheetPreview("第一次因为动漫哭泣",R.drawable.memory_test,"第一次看one piece泪流满面,是因为感动.\n没错没错,最坏的时代，才有最好的感情。\n可即使流泪，又会随伙伴们胜利的喜悦又哭又笑...", "zhangziyang", "1");
            memlist.add(memex);
        }
        mAdapter = new MemAdapter(memlist, getContext());
        mRecyclerView.setAdapter(mAdapter);


        /*xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(view.getContext()));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initMem();
                        xRefreshView.stopRefresh();
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mLoadCount++;
                        if (mLoadCount >= 3) {//模拟没有更多数据的情况
                            xRefreshView.setLoadComplete(true);
                        } else {
                            // 刷新完成必须调用此方法停止加载
                            xRefreshView.stopLoadMore(false);
                            //当数据加载失败 不需要隐藏footerview时，可以调用以下方法，传入false，不传默认为true
                            // 同时在Footerview的onStateFinish(boolean hideFooter)，可以在hideFooter为false时，显示数据加载失败的ui
//                            xRefreshView1.stopLoadMore(false);
                        }
                    }
                }, 1000);
            }
        });*/

        /*RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.container);
        ArrayList<String> strings = new ArrayList<>();
        Collections.addAll(strings, TEST_STRINGS);
        mAdapter = new SimpleAdapter(strings);
        //recyclerView
        recyclerView= new SnappingSwipingViewBuilder(view.getContext())
                .setAdapter(mAdapter)
                .setHeadTailExtraMarginDp(17F)
                .setItemMarginDp(8F, 20F, 8F, 20F)
                .setOnSwipeListener(this)
                .setSnapMethod(SnappyLinearLayoutManager.SnappyLinearSmoothScroller.SNAP_CENTER)
                .build();
        if (rl != null) {
            recyclerView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rl.addView(recyclerView);
        }*/


    }
}
