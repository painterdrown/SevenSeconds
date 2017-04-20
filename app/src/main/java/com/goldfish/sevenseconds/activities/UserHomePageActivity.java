package com.goldfish.sevenseconds.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MyPageTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/3/4.
 */

public class UserHomePageActivity extends BaseActivity {

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
    private UserHomePageActivity userHomePageActivity;

    private Orientation orientation;
    private RecyclerView recyclerView;
    private MyPageTimelineAdapter myPageTimelineAdapter;
    private List<MyPageTimelineItem> myPageTimelineItemList = new ArrayList<>();
    private ArrayList<String> memoryList;
    private ProgressDialog progressDialog;
    private TextView nowText;
    private ImageView nowPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        currentUser = getIntent().getStringExtra("account");
        BaseActivity.getInstance().addActivity(this);
        userHomePageActivity = this;

        progressDialog = new ProgressDialog(UserHomePageActivity.this);
        progressDialog.setMessage("正在加载该用户的忆单，请稍候~");
        progressDialog.setCanceledOnTouchOutside(false);

        nowPoint = (ImageView) findViewById(R.id.home_now_point);
        nowText = (TextView) findViewById(R.id.home_now_text);
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
                BaseActivity.getInstance().finishActivity(userHomePageActivity);
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
        progressDialog.show();
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

    // 联网获得个人信息
    private String getUserInfo() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            JSONObject jo_return = UserHttpUtil.getUserInfo(jo);
            if (jo_return.getBoolean("ok")) {
                titleBarInfo.setName(jo_return.getString("username"));
                titleBarInfo.setIntroduction(jo_return.getString("introduction"));
                titleBarInfo.setBirthday(jo_return.getString("birthday").substring(0, 10));
                titleBarInfo.setSex(jo_return.getString("sex"));
                titleBarInfo.setFace(UserHttpUtil.getUserFace(jo));
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
            ArrayList<String> myFollowers = UserHttpUtil.getFollowingList(jo);
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
        hadFollowed = true;
        toolBarFollow.setImageResource(R.drawable.ic_star_black_36dp);
    }

    // 更新关注按钮
    private void refreshUnfollowButton() {
        hadFollowed = false;
        toolBarFollow.setImageResource(R.drawable.ic_star_border_black_36dp);
    }

    private String follow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", LogActivity.user);
            jo.put("otherAccount", currentUser);
            JSONObject jo_return = UserHttpUtil.addFollow(jo);
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
            JSONObject jo_return = UserHttpUtil.deleteFollow(jo);
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
            else if (params[0].equals("follow")) { result = follow(); }
            else if (params[0].equals("unfollow")) { result = unfollow(); }
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