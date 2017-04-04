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
    private List<MemorySheetPreview> SearchItems = new ArrayList<MemorySheetPreview>();
    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private MemAdapter mAdapter;
    private String name;
    private List<MemorySheetPreview> memlist = new ArrayList<MemorySheetPreview>();
    private RecyclerView recyclerView;
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

    }
}
