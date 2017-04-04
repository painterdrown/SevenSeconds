package com.goldfish.sevenseconds.activities;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.MyPageTimelineAdapter;
import com.goldfish.sevenseconds.bean.TitleBarInfo;
import com.goldfish.sevenseconds.fragment.MyFragment;
import com.goldfish.sevenseconds.item.MyPageTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.tools.Http;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/3/4.
 */

public class UserHomePageActivity extends AppCompatActivity {

    private String currentUser;
    private Bitmap face;
    private ImageView headPortrait;
    private TextView toolBarName;
    private ImageView toolBarBack;
    private ImageView toolBarFollow;
    private TextView name;
    private ImageView sex;
    private TextView times;
    private TextView introduction;
    private TitleBarInfo titleBarInfo;
    private boolean hadFollowed = false;

    private Orientation orientation;
    private RecyclerView recyclerView;
    private MyPageTimelineAdapter myPageTimelineAdapter;
    private List<MyPageTimelineItem> myPageTimelineItemList = new ArrayList<>();
    private ArrayList<String> memoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        currentUser = getIntent().getStringExtra("account");

        headPortrait = (ImageView) findViewById(R.id.home_headPortrait);
        toolBarBack = (ImageView) findViewById(R.id.home_back);
        toolBarFollow = (ImageView) findViewById(R.id.home_follow);
        toolBarName = (TextView) findViewById(R.id.home_toolbar_name);
        name = (TextView) findViewById(R.id.home_name);
        sex = (ImageView) findViewById(R.id.home_sex);
        times = (TextView) findViewById(R.id.home_times);
        introduction = (TextView) findViewById(R.id.home_introduction);
        titleBarInfo = new TitleBarInfo();

        toolBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolBarFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hadFollowed) {
                    DownTask downTask = new DownTask();
                    downTask.execute("unfollow");
                } else {
                    DownTask downTask = new DownTask();
                    downTask.execute("follow");
                }
            }
        });
        initView();

        orientation = Orientation.vertical;
        recyclerView = (RecyclerView) findViewById(R.id.home_timeline);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void initView() {
        DownTask downTask = new DownTask();
        downTask.execute("getMyMemoryList");
        DownTask ifFollow = new DownTask();
        ifFollow.execute("ifFollowAuthor");
        DownTask getUser = new DownTask();
        getUser.execute("getUserInfo");
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
            myPageTimelineItem.setTitle("该用户还没有忆单~");
            myPageTimelineItemList.add(myPageTimelineItem);
        }
        myPageTimelineAdapter = new MyPageTimelineAdapter(myPageTimelineItemList, orientation);
        recyclerView.setAdapter(myPageTimelineAdapter);
    }

    private String getImage() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            face = Http.getUserFace(jo);
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
            memoryList = Http.getMemoryList(jo);
            result = "Succeed in getting my memory list";
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
                            JSONObject jo_return = Http.getMemory(jo);
                            if (jo_return.getBoolean("ok")) {
                                MyPageTimelineItem myPageTimelineItem = new MyPageTimelineItem();
                                myPageTimelineItem.setTitle(jo_return.getString("title"));
                                String time = jo_return.getString("time");
                                time = time.substring(5, 7) + "/" + time.substring(0, 4);
                                myPageTimelineItem.setTime(time);
                                jo.put("i", 0);
                                Bitmap bitmap = Http.getMemoryImg(jo);
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

    // 联网获得个人信息
    private String getUserInfo() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            JSONObject jo_return = Http.getUserInfo(jo);
            if (jo_return.getBoolean("ok")) {
                titleBarInfo.setName(jo_return.getString("username"));
                titleBarInfo.setIntroduction(jo_return.getString("introduction"));
                titleBarInfo.setBirthday(jo_return.getString("birthday").substring(0, 10));
                titleBarInfo.setSex(jo_return.getString("sex"));
                titleBarInfo.setFace(Http.getUserFace(jo));
                result = "Succeed in getting information";
            } else{
                result = "获取用户信息失败 TAT";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    private void setUserInfo() {
        name.setText(titleBarInfo.getName());
        if (titleBarInfo.getSex().equals("男")) { sex.setImageResource(R.drawable.ic_person_black_24dp_male);}
        else { sex.setImageResource(R.drawable.ic_person_black_24dp_female); }
        String timesStr = titleBarInfo.getBirthday().substring(2, 3) + "0后";
        times.setText(timesStr);
        headPortrait.setImageBitmap(titleBarInfo.getFace());
        introduction.setText(titleBarInfo.getIntroduction());
        toolBarName.setText(titleBarInfo.getName());
    }

    private void setStar() {
        if (hadFollowed) {
            toolBarFollow.setImageResource(R.drawable.ic_star_black_36dp);
        }
        else {
            toolBarFollow.setImageResource(R.drawable.ic_star_border_black_36dp);
        }
    }

    private String ifFollowAuthor() {
        String result = "Failed in judging";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            ArrayList<String> myFollowers = Http.getFollowingList(jo);
            for (int i = 0; i < myFollowers.size(); i++) {
                if (myFollowers.get(i).equals(currentUser)) {
                    hadFollowed = true;
                }
            }
            result = "Succeed in judging";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 更新关注按钮
    private void refreshFollowButton() {
        toolBarFollow.setImageResource(R.drawable.ic_star_black_36dp);
    }

    // 更新关注按钮
    private void refreshUnfollowButton() {
        toolBarFollow.setImageResource(R.drawable.ic_star_border_black_36dp);
    }

    private String follow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", LogActivity.user);
            jo.put("otherAccount", currentUser);
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
            jo.put("myAccount", LogActivity.user);
            jo.put("otherAccount", currentUser);
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

    private class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            if (params[0].equals("getImage")) { result = getImage(); }
            else if (params[0].equals("getUserInfo")) { result = getUserInfo(); }
            else if (params[0].equals("getMyMemoryList")) { result = getMyMemoryList(); }
            else if (params[0].equals("getMyMemory")) { result = getMyMemory(); }
            else if (params[0].equals("ifFollowAuthor")) { result = ifFollowAuthor(); }
            else if (params[0].equals("Follow")) { result = follow(); }
            else if (params[0].equals("Unfollow")) { result = unfollow(); }
            else { result = params[0]; }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting face")) { headPortrait.setImageBitmap(face); }
            else if (s.equals("Succeed in getting my memory list")) {
                DownTask downTask = new DownTask();
                downTask.execute("getMyMemory");
            }
            else if (s.equals("Succeed in getting memory")) { setDataListItems(); }
            else if (s.equals("Have no memory")) { setDataListItems(); }
            else if (s.equals("Succeed in getting information")) { setUserInfo(); }
            else if (s.equals("Succeed in judging")) { setStar(); }
            else if (s.equals("Succeed in following")) { refreshFollowButton(); }
            else if (s.equals("Succeed in unfollowing")) { refreshUnfollowButton(); }
        }
    }
}