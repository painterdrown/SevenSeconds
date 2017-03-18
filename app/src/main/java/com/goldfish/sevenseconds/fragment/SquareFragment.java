package com.goldfish.sevenseconds.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.item.MemorySheetPreview;
import com.goldfish.sevenseconds.tools.Http;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class SquareFragment extends Fragment {
    private String name;
    private List<MemorySheetPreview> memlist = new ArrayList<MemorySheetPreview>();
    private RecyclerView recyclerView;
    private Http http = new Http();
    XRefreshView xRefreshView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        Exception();
        View view = inflater.inflate(R.layout.fragment_square,container,false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mem_list);
        recyclerView.setHasFixedSize(true);

        xRefreshView = (XRefreshView)view.findViewById(R.id.x_fresh_square);

        MemAdapter adapter;
        adapter = new MemAdapter(memlist,view.getContext());
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        xRefreshView.setPinnedTime(1000);
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
        });
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.name = bundle.get("topic").toString();
        }
    }
    public static SquareFragment newInstance(String libargument)
    {
        Bundle bundle = new Bundle();
        bundle.putString("topic",libargument);
        SquareFragment mm = new SquareFragment();
        mm.setArguments(bundle);
        return mm;
    }
    private void initMem(){
        for (int i = 0;i < 10; i++){
            MemorySheetPreview apple = new MemorySheetPreview("apple",R.drawable.apple_pic,"fuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuckfuck", "zhangziyang", "1");
            memlist.add(apple);
        }
    }
}
