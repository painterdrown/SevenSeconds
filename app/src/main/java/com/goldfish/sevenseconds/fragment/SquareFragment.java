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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.Addmem;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.LogActivity;
import com.goldfish.sevenseconds.adapter.MemAdapter;
import com.goldfish.sevenseconds.adapter.MyTimelineAdapter;
import com.goldfish.sevenseconds.bean.MemoryContext;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MemorySheetPreview;

import com.goldfish.sevenseconds.item.MyTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.tools.PullToRefreshRecyclerView;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.tools.ScrollSpeedLinearLayoutManger;
import com.handmark.pulltorefresh.library.PullToRefreshBase;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.goldfish.sevenseconds.http.MemoryHttpUtil.getAllMemoryList;
import static com.goldfish.sevenseconds.http.MemoryHttpUtil.getMemory;

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
    private List<JSONObject> allmemb = new ArrayList<JSONObject>();
    private List<MemoryContext> memlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView editMemory;
    private View view;
    private boolean ifStart = false;
    //XRefreshView xRefreshView;

    /*
    ** ʱ����
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



    private int mLoadCount = 0;

    public void Exception(){
        //�������android.os.NetworkOnMainThreadException�쳣
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    // �첽��ȡ�䵥�����Ϣ
    class refresh extends AsyncTask<String, Integer, String>{
        /*@Override
        protected Boolean doInBackground(Void... params){
            try {
                allmem = getAllMemoryList();
            }catch (Exception e){
                Log.d("get data error",e.getMessage());
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                Toast.makeText(BarActivity.barActivity,"��ȡ�ɹ�!",Toast.LENGTH_LONG).show();
                new refreshbegin().execute();
            }
            else
            {
                Toast.makeText(BarActivity.barActivity,"��ȡʧ��!",Toast.LENGTH_LONG).show();
            }
        }
    }
    class refreshbegin extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params){
            try {
                for (int i =0;i<allmem.size();i++){
                    JSONObject js = new JSONObject();
                    js.put("memoryId",allmem.get(i));
                    allmemb.add(getMemory(js));
                }
            }catch (Exception e){
                Log.d("get data error",e.getMessage());
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                for (int i =0;i<allmemb.size();i++){
                    memlist.add(new MemorySheetPreview());
                }
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(BarActivity.barActivity,"��ȡʧ��!",Toast.LENGTH_LONG).show();
            }
        }*/
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
        }
    }

    // ��ȡ�����䵥ID
    private String getAllMemoryList() {
        String result;
        allmem = MemoryHttpUtil.getAllMemoryList();
        if (allmem.size() > 0) {
            result = "Succeed in getAllMemoryList";
        }
        else {
            result = "û���䵥";
        }
        return result;
    }
    // ��ȡ�䵥ID��Ĳ���
    private void refreshGetAllMemory() {
        new refresh().execute("getSingleMemory");
    }
    // ��ȡ5���䵥������
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
        return result;
    }
    // ��ȡһ���䵥������
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
    // ����Ԥ������
    private void refreshPreviewMemory() {
        mAdapter = new MemAdapter(memlist,view.getContext());
        mRecyclerView.setAdapter(mAdapter);
    }
    // ��ȡʱ�����ʱ��
    static public String getCollectTime() {
        return collectTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        Exception();
        view = inflater.inflate(R.layout.fragment_square,container,false);
        editMemory = (ImageView) view.findViewById(R.id.square_edit);
        editMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Addmem.class);
                startActivity(intent);
            }
        });

        /*
        ** ʱ����
         */
        orientation = Orientation.horizontal;
        recyclerView1 = (RecyclerView) view.findViewById(R.id.square_timeline);
        final ScrollSpeedLinearLayoutManger mLayoutManager = new ScrollSpeedLinearLayoutManger(recyclerView1.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setSpeedSlow();
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.setHasFixedSize(true);
        initView();
        lastVisibleItem = 0;
        firstVisibleItem = 0;
        currentVisibleItem = 0;
        lastYear = (TextView) view.findViewById(R.id.square_last_year);
        nextYear = (TextView) view.findViewById(R.id.square_next_year);
        recyclerView1.addOnScrollListener(new  RecyclerView.OnScrollListener() {

            // ״̬�ı��ʱ����ú���
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

            // ����������ʱ����ú���
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
        addOne = (ImageView) view.findViewById(R.id.square_add_one);

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
                // ��ʾ�����µ�ʱ��
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
                // ��ʾ�����µ�ʱ��
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
        /*
        for (int i = 0;i < 10; i++){
            MemorySheetPreview memex = new MemorySheetPreview("��һ����Ϊ��������",R.drawable.memory_test,"��һ�ο�one piece��������,����Ϊ�ж�.\nû��û��,���ʱ����������õĸ��顣\n�ɼ�ʹ���ᣬ�ֻ�������ʤ����ϲ���ֿ���Ц...", "zhangziyang", "1","Jul,2007","#���� #������");
            memlist.add(memex);
        }*/
        mAdapter = new MemAdapter(memlist,view.getContext());

        new refresh().execute("getAllMemoryList");
        //allmem = getAllMemoryList();
        /*for (int i = 0;i < 10; i++){
            MemorySheetPreview memex = new MemorySheetPreview("��һ����Ϊ��������",R.drawable.memory_test,"��һ�ο�one piece��������,����Ϊ�ж�.\nû��û��,���ʱ����������õĸ��顣\n�ɼ�ʹ���ᣬ�ֻ�������ʤ����ϲ���ֿ���Ц...", "zhangziyang", "1","Jul,2007","#���� #������");
            memlist.add(memex);
        }
        mRecyclerView.setAdapter(mAdapter);
        mAdapter = new MemAdapter(memlist,view.getContext());*/

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
                        if (mLoadCount >= 3) {//ģ��û�и������ݵ����
                            xRefreshView.setLoadComplete(true);
                        } else {
                            // ˢ����ɱ�����ô˷���ֹͣ����
                            xRefreshView.stopLoadMore(false);
                            //�����ݼ���ʧ�� ����Ҫ����footerviewʱ�����Ե������·���������false������Ĭ��Ϊtrue
                            // ͬʱ��Footerview��onStateFinish(boolean hideFooter)��������hideFooterΪfalseʱ����ʾ���ݼ���ʧ�ܵ�ui
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
            mAdapter.refreshAllCount();
        } else{
            ifStart = true;
        }

    }

    // ʱ�������
    private void initView() {
        for (int i = 0; i < 16; i++) {
            MyTimelineItem myTimelineItem = new MyTimelineItem();
            myTimelineItem.setMonth(months[i]);
            myTimelineItems.add(myTimelineItem);
        }
        myTimelineAdapter = new MyTimelineAdapter(myTimelineItems, orientation);
        recyclerView1.setAdapter(myTimelineAdapter);
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
    /*private void initMem(){
        for (int i = 0;i < 10; i++){
            MemorySheetPreview memex = new MemorySheetPreview("��һ����Ϊ��������",R.drawable.memory_test,"��һ�ο�one piece��������,����Ϊ�ж�.\nû��û��,���ʱ����������õĸ��顣\n�ɼ�ʹ���ᣬ�ֻ�������ʤ����ϲ���ֿ���Ц...", "zhangziyang", "1","Jul,2007","#���� #������");
            memlist.add(memex);
        }
    }*/
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
