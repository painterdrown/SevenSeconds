package com.goldfish.sevenseconds.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.item.MemorySheetPreview;
import com.goldfish.sevenseconds.tools.Http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import SnappingSwipingRecyclerView.SnappingSwipingViewBuilder;
import SnappingSwipingRecyclerView.SnappyLinearLayoutManager;
import SnappingSwipingRecyclerView.SwipeGestureHelper;

import static com.andview.refreshview.R.styleable.XRefreshView;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class SquareFragment extends Fragment implements SwipeGestureHelper.OnSwipeListener{
    private SimpleAdapter mAdapter;
    static final String[] TEST_STRINGS = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX",
            "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE"};

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
    @Override
    public void onSwipe(RecyclerView rv, int adapterPosition, float dy) {
        Toast.makeText(this.getContext(), "xxx", Toast.LENGTH_LONG);
        mAdapter.removeItem(adapterPosition);
        rv.invalidateItemDecorations();
    }

    //SimpleViewHolder
    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;

        public SimpleViewHolder(View ll, TextView itemView) {
            super(ll);
            mTextView = itemView;
        }
    }
    //SimpleAdapter
    class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder>{
        private final List<String> mDataSet;


        public SimpleAdapter(List<String> dataSet) {
            this.mDataSet = dataSet;
        }

        public void removeItem(int adapterPos) {
            mDataSet.remove(adapterPos);
            notifyItemRemoved(adapterPos);
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Log.d(TAG, "onCreateViewHolder");
//            LinearLayout ll = new LinearLayout(parent.getContext());
            TextView tv = new TextView(parent.getContext());
            tv.setTextColor(Color.WHITE);
//            tv.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            ll.addView(tv);
            int w = parent.getWidth();
            int itemMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9F,
                    getResources().getDisplayMetrics()) + 0.5F);
            int itemPadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F,
                    getResources().getDisplayMetrics()) + 0.5F);
            int itemWidth = w - (itemMargin + itemPadding * 2) * 2;
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
//            int h = parent.getHeight();
//            int childW = (int) (w * 0.7);
//            ll.setPadding(itemPadding, itemPadding, itemPadding, itemPadding);
//            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
//                    itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//            ll.setLayoutParams(lp);
//            ll.setBackground(getDrawable(R.drawable.round_rect_border));
            return new SimpleViewHolder(tv, tv);
        }

        private int getColor(int position) {
            float factor = (float) position / (float) getItemCount();
            return 0xFF000000 | (int) (0x0000FF * Math.sin(factor))
                    | ((int) (0x0000FF * Math.sin(2 * Math.PI * (factor + 1F / 3F))) << 8)
                    | ((int) (0x0000FF * Math.sin(2 * Math.PI * (factor + 2F / 3F))) << 16);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            //Log.d(TAG, "onBindViewHolder: " + holder.itemView);
            TextView tv = holder.mTextView;
            String content = mDataSet.get(position);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            tv.setGravity(Gravity.CENTER);
            tv.setText(content);
            int color = getColor(position);
            //Log.d(TAG, String.format("color: %8h", color));
            tv.setBackgroundColor(color);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }
}
