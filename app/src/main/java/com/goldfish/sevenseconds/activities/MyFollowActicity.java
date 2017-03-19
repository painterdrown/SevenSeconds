package com.goldfish.sevenseconds.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goldfish.sevenseconds.bean.MyFollow;
import com.goldfish.sevenseconds.adapter.MyFollowAdapter;
import com.goldfish.sevenseconds.item.MyFollowItem;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.tools.Http;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class MyFollowActicity extends AppCompatActivity {

    private List<MyFollowItem> followItemList = new ArrayList<>();
    private SQLiteDatabase db;            // 数据库
    private String myAccount;
    private String[] myFollow;
    private DownTask downTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        Intent intent = getIntent();
        myAccount = intent.getStringExtra("currentUser");

        // test
        myAccount = "a";

        initMyFollowItem();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_follow);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MyFollowAdapter adapter = new MyFollowAdapter(followItemList);
        recyclerView.setAdapter(adapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initMyFollowItem() {
        // 本地更新异步联网加载
        downTask = new DownTask();
        downTask.execute("getFollowList");

        // 本地更新
        db = Connector.getDatabase();
        List<MyFollow> myFollowList = DataSupport.findAll(MyFollow.class);
        if (myFollowList.size() > 0) {
            for (MyFollow myFollow : myFollowList) {
                MyFollowItem myFollowItem = new MyFollowItem(
                        R.drawable.red_love, myFollow.getName(),
                        myFollow.getIntroduction(), myFollow.getFace(), myFollow.getAccount());
                followItemList.add(myFollowItem);
            }
        }
    }

    private String getFollowList() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", myAccount);
            myFollow = Http.getFollowingList(jo);
            result = "Succeed in getting follow list";
        } catch (JSONException e) {
            result = "获取关注列表失败TAT";
            e.printStackTrace();
        }
        return result;
    }

    private String getFollows() {
        String result = "获取关注列表失败TAT";
        for (int i = 0; i < myFollow.length; i++) {
            try {
                JSONObject jo = new JSONObject();
                jo.put("account", myFollow[i]);
                JSONObject jo_return = Http.getUserInfo(jo);
                if (!jo_return.getBoolean("ok")) {
                    result = "获取关注列表失败TAT";
                    break;
                } else {
                    MyFollowItem myFollowItem = new MyFollowItem();
                    myFollowItem.setName(jo_return.getString("username"));
                    myFollowItem.setIntroduction(jo_return.getString("introduction"));
                    myFollowItem.setAccount(jo_return.getString("account"));
                    Bitmap userface = Http.getUserFace(jo);

                    //myFollowItem.setFace();
                }
                result = "Succeed in getting follows";
            } catch (JSONException e) {
                result = "获取关注列表失败TAT";
                e.printStackTrace();
            }
        }
        return result;
    }

    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "Failed";
            if (params[0].equals("getFollowList")) {
                result = getFollowList();
            }
            else if (params[0].equals("getFollows")) {
                result = getFollows();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting follow list")) {
                if (myFollow.length > 0) {
                    downTask = new DownTask();
                    downTask.execute("getFollows");
                }
            }
            else if (s.equals("Succeed in getting follows")) {

            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
