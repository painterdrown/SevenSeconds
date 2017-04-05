package com.goldfish.sevenseconds.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.Addmem;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.item.MemorySheetPreview;

import com.goldfish.sevenseconds.tools.PullToRefreshRecyclerView;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;


import java.util.ArrayList;
import java.util.List;

import static com.goldfish.sevenseconds.http.MemoryHttpUtil.getAllMemoryList;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class SquareFragment extends Fragment{
    //private SimpleAdapter mAdapter;
    static final String[] TEST_STRINGS = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX",
            "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE"};

    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private MemAdapter mAdapter;
    private List<String> allmem = new ArrayList<String>();
    private String name;
    private List<MemorySheetPreview> memlist = new ArrayList<MemorySheetPreview>();
    private RecyclerView recyclerView;
    private ImageView editMemory;
    //XRefreshView xRefreshView;







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
    class refresh extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params){
          try {
              getAllMemoryList();
          }catch (Exception e){
              Log.d("get data error",e.getMessage());
              return false;
          }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                Toast.makeText(BarActivity.barActivity,"拉取成功!"+allmem.get(0),Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(BarActivity.barActivity,"拉取失败!",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        Exception();
        View view = inflater.inflate(R.layout.fragment_square,container,false);
        editMemory = (ImageView) view.findViewById(R.id.square_edit);
        editMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Addmem.class);
                startActivity(intent);
            }
        });

        /*
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mem_list);
        recyclerView.setHasFixedSize(true);

        xRefreshView = (XRefreshView)view.findViewById(R.id.x_fresh_square);
        */
        /*
        MemAdapter adapter;
        adapter = new MemAdapter(memlist,view.getContext());
        recyclerView.setAdapter(adapter);*/
        mPullRefreshRecyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.hor_rec_refresh);
        mRecyclerView = mPullRefreshRecyclerView.getRefreshableView();
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //getAllMemoryList();
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
                //getAllMemoryList();
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
        new refresh().execute();
        //allmem = getAllMemoryList();
        for (int i = 0;i < 10; i++){
            MemorySheetPreview memex = new MemorySheetPreview("第一次因为动漫哭泣",R.drawable.memory_test,"第一次看one piece泪流满面,是因为感动.\n没错没错,最坏的时代，才有最好的感情。\n可即使流泪，又会随伙伴们胜利的喜悦又哭又笑...", "zhangziyang", "1","Jul,2007","#动漫 #海贼王");
            memlist.add(memex);
        }
        mAdapter = new MemAdapter(memlist,view.getContext());
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
            MemorySheetPreview memex = new MemorySheetPreview("第一次因为动漫哭泣",R.drawable.memory_test,"第一次看one piece泪流满面,是因为感动.\n没错没错,最坏的时代，才有最好的感情。\n可即使流泪，又会随伙伴们胜利的喜悦又哭又笑...", "zhangziyang", "1","Jul,2007","#动漫 #海贼王");
            memlist.add(memex);
        }
    }
    /*
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
    }*/
}
