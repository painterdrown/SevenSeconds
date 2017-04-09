package com.goldfish.sevenseconds.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.Addmem;
import com.goldfish.sevenseconds.activities.InformationActivity;
import com.goldfish.sevenseconds.activities.LogActivity;
import com.goldfish.sevenseconds.activities.MessageActivity;
import com.goldfish.sevenseconds.activities.MyFollowActicity;
import com.goldfish.sevenseconds.activities.SettingActivity;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.TimeCapsuleManagerActivity;
import com.goldfish.sevenseconds.adapter.MyPageTimelineAdapter;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MyPageTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class MyFragment extends Fragment {

    private String name;
    private SQLiteOpenHelper dbChattingDatabaseHelper;
    private String currentUser;
    private Bitmap face;
    private ImageView headPortrait;
    private Toolbar toolbar;
    private TextView textView;
    private Orientation orientation;
    private RecyclerView recyclerView;
    private MyPageTimelineAdapter myPageTimelineAdapter;
    private List<MyPageTimelineItem> myPageTimelineItemList = new ArrayList<>();
    private ArrayList<String> memoryList;
    private ImageView letter;
    private ProgressDialog progressDialog;

    private ImageView nowPoint;
    private TextView nowText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        // 成功登陆的账号
        currentUser = LogActivity.user;

        progressDialog = new ProgressDialog(BarActivity.barActivity);
        progressDialog.setMessage("正在加载您的忆单，请稍候~");
        progressDialog.setCanceledOnTouchOutside(false);

        View view = inflater.inflate(R.layout.fragment_my_page,container,false);
        RelativeLayout myMessage = (RelativeLayout) view.findViewById(R.id.myMessage);
        RelativeLayout myInformation = (RelativeLayout) view.findViewById(R.id.myInformation);
        RelativeLayout mySetting = (RelativeLayout) view.findViewById(R.id.mySetting);
        RelativeLayout myFollow = (RelativeLayout) view.findViewById(R.id.myFollow);
        headPortrait = (ImageView) view.findViewById(R.id.headPortrait);
        nowPoint = (ImageView) view.findViewById(R.id.my_page_now_point);
        nowText = (TextView) view.findViewById(R.id.my_page_now_text);

        letter = (ImageView) view.findViewById(R.id.my_page_letter);
        letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Addmem.class);
                startActivityForResult(intent, 1);
            }
        });

        // 从编辑忆单回来才需要重新加载
        myPageTimelineItemList.clear();
        nowPoint.setVisibility(View.INVISIBLE);
        nowText.setVisibility(View.INVISIBLE);
        initView();

        mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToSetting = new Intent(BarActivity.barActivity, SettingActivity.class);
                startActivity(myToSetting);
            }
        });

        myInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToInformation = new Intent(BarActivity.barActivity, InformationActivity.class);
                myToInformation.putExtra("currentUser", currentUser);
                startActivity(myToInformation);
            }
        });

        myFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToFollow = new Intent(BarActivity.barActivity, MyFollowActicity.class);
                myToFollow.putExtra("currentUser", currentUser);
                startActivity(myToFollow);
            }
        });

        myMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToMessage = new Intent(BarActivity.barActivity, MessageActivity.class);
                startActivity(myToMessage);
            }
        });

        orientation = Orientation.vertical;
        recyclerView = (RecyclerView) view.findViewById(R.id.my_page_timeline);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        /*TurnCardListView list = (TurnCardListView) view.findViewById(R.id.card_list);

        list.setOnTurnListener(new TurnCardListView.OnTurnListener() {
            @Override
            public void onTurned(int position) {
                Toast.makeText(BarActivity.barActivity, "position = " + position, Toast.LENGTH_SHORT).show();
            }
        });

        list.setAdapter(new BaseAdapter() {
            int[] colors = {0xffFF9800, 0xff3F51B5, 0xff673AB7, 0xff006064, 0xffC51162, 0xffFFEB3B, 0xff795548, 0xff9E9E9E};

            @Override
            public int getCount() {
                return colors.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View child, ViewGroup parent) {
                if (child == null) {
                    child = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_memory_item, parent, false);
                }
                return child;
            }
        });*/
        return view;
    }
    private void initView() {
        progressDialog.show();
        DownTask downTask = new DownTask();
        downTask.execute("getMyMemoryList");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnData = data.getStringExtra("add memory return");
                    if (returnData.equals("refresh memory")) {
                        // 从编辑忆单回来才需要重新加载
                        myPageTimelineItemList.clear();
                        nowPoint.setVisibility(View.INVISIBLE);
                        nowText.setVisibility(View.INVISIBLE);
                        initView();
                    }
                }
                break;
        }
    }


    private void setDataListItems() {
        if (myPageTimelineItemList.size() == 0) {
            MyPageTimelineItem myPageTimelineItem = new MyPageTimelineItem();
            Time t=new Time();
            t.setToNow();
            int year = t.year;
            int month = t.month;
            String date;
            if (month <= 9) date = "0" + String.valueOf(month);
            else date = String.valueOf(month);
            date +=  "/" + String.valueOf(year);
            myPageTimelineItem.setTime(date);
            myPageTimelineItem.setTitle("您还没有忆单，创建一条吧~");
            myPageTimelineItem.setMemoryId("add memory");
            myPageTimelineItemList.add(myPageTimelineItem);
        }
        nowPoint.setVisibility(View.VISIBLE);
        Time t=new Time("GMT+8");
        t.setToNow();
        int year = t.year;
        nowText.setText(String.valueOf(year));
        nowText.setVisibility(View.VISIBLE);
        myPageTimelineItemList = makeInorder(myPageTimelineItemList);
        myPageTimelineAdapter = new MyPageTimelineAdapter(myPageTimelineItemList, orientation);
        recyclerView.setAdapter(myPageTimelineAdapter);
        progressDialog.dismiss();
    }

    // 排序
    private List<MyPageTimelineItem> makeInorder(List<MyPageTimelineItem> apageTimelineItemList) {
        for (int i = 0; i < apageTimelineItemList.size() - 1; i++) {
            for (int j = i + 1; j < apageTimelineItemList.size(); j++) {
                if (StringToInt(apageTimelineItemList.get(i).getTime()) < StringToInt(apageTimelineItemList.get(j).getTime())){
                    MyPageTimelineItem temp = apageTimelineItemList.get(i);
                    apageTimelineItemList.set(i, apageTimelineItemList.get(j));
                    apageTimelineItemList.set(j, temp);
                }
            }
        }
        return apageTimelineItemList;
    }

    private int StringToInt(String s) {
        if (s != null) {
            if (s.length() >= 7) {
                String temp = s.substring(3, 7);
                temp += s.substring(0, 2);
                return Integer.parseInt(temp);
            }
        }
        return 0;
    }

    private String getImage() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            face = UserHttpUtil.getUserFace(jo);
            if (face != null) {
                result = "Succeed in getting face";
            } else {
                result = "服务器故障啦~";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    private String getMyMemoryList() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            memoryList = new ArrayList<>();
            memoryList = UserHttpUtil.getMemoryList(jo);
            result = "Succeed in getting my memory list";
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    private String getMyCollectList() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            memoryList.addAll(UserHttpUtil.getCollectMemoryList(jo));
            result = "Succeed in getting my collect memory list";
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    private String getMyMemory() {
        String result = "获取忆单失败";
        if (memoryList != null) {
            if (memoryList.size() > 0) {
                for (int i = 0; i < memoryList.size(); i++) {
                    if (!memoryList.get(i).equals("")) {
                        try {
                            JSONObject jo = new JSONObject();
                            jo.put("memoryId", memoryList.get(i));
                            JSONObject jo_return = MemoryHttpUtil.getMemory(jo);
                            if (jo_return.getBoolean("ok")) {
                                MyPageTimelineItem myPageTimelineItem = new MyPageTimelineItem();
                                myPageTimelineItem.setTitle(jo_return.getString("title"));
                                String time = jo_return.getString("time");
                                time = time.substring(5, 7) + "/" + time.substring(0, 4);
                                myPageTimelineItem.setTime(time);
                                jo.put("i", 0);
                                Bitmap bitmap = MemoryHttpUtil.getMemoryImg(jo);
                                if (bitmap != null) {
                                    myPageTimelineItem.setCover(bitmap);
                                }
                                myPageTimelineItem.setAccount(jo_return.getString("author"));
                                myPageTimelineItem.setMemoryId(memoryList.get(i));
                                myPageTimelineItemList.add(myPageTimelineItem);
                                result = "Succeed in getting memory";
                            }
                        } catch (JSONException e) {
                            result = "获取忆单失败";
                            e.printStackTrace();
                        }
                    } else result = "Have no memory";
                }
            } else result = "Have no memory";
        } else result = "Have no memory";
        return result;
    }

    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            if (params[0].equals("getImage")) { result = getImage(); }
            else if (params[0].equals("getMyMemoryList")) { result = getMyMemoryList(); }
            else if (params[0].equals("getMyMemory")) { result = getMyMemory(); }
            else if (params[0].equals("getMyCollectList")) { result = getMyCollectList(); }
            else { result = params[0]; }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting face")) { headPortrait.setImageBitmap(face); }
            else if (s.equals("Succeed in getting my memory list")) {
                new DownTask().execute("getMyCollectList");
            }
            else if (s.equals("Succeed in getting memory")) {
                setDataListItems();
            }
            else if (s.equals("Have no memory")) {
                setDataListItems();
            }
            else if (s.equals("Succeed in getting my collect memory list"))
            {
                DownTask downTask = new DownTask();
                downTask.execute("getMyMemory");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        DownTask downTask = new DownTask();
        downTask.execute("getImage");
    }

    public static MyFragment newInstance(String libargument) {
        Bundle bundle = new Bundle();
        MyFragment mm = new MyFragment();
        return mm;
    }
}
