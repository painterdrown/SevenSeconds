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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldfish.sevenseconds.bean.MyFollow;
import com.goldfish.sevenseconds.adapter.MyFollowAdapter;
import com.goldfish.sevenseconds.item.MyFollowItem;
import com.goldfish.sevenseconds.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class MyFollowActicity extends AppCompatActivity {

    private List<MyFollowItem> followItemList = new ArrayList<>();
    private SQLiteDatabase db;            // 数据库
    private String myAccount;
    private ArrayList<String> myFollow;
    private DownTask downTask;
    public static MyFollowActicity myFollowActicity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        Intent intent = getIntent();
        myFollowActicity = this;
        myAccount = intent.getStringExtra("currentUser");

        // test
        myAccount = "a";

        ImageView back = (ImageView) findViewById(R.id.my_follow_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMyFollowItem();
    }

    private void initMyFollowItem() {
        // 本地更新异步联网加载
        downTask = new DownTask();
        downTask.execute("getFollowList");
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
        for (int i = 0; i < myFollow.size(); i++) {
            try {
                if (!myFollow.get(i).equals("")) {
                    JSONObject jo = new JSONObject();
                    jo.put("account", myFollow.get(i));
                    JSONObject jo_return = Http.getUserInfo(jo);
                    if (!jo_return.getBoolean("ok")) {
                        result = "获取关注列表失败TAT";
                        break;
                    } else {
                        MyFollowItem myFollowItem = new MyFollowItem();
                        myFollowItem.setName(jo_return.getString("username"));
                        myFollowItem.setAccount(jo_return.getString("account"));

                        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                        myFollowItem.setIntroduction(jo_return.getString("introduction"));
                        Bitmap userface = Http.getUserFace(jo);
                        userface.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                        userface.recycle(); //自由选择是否进行回收
                        myFollowItem.setFace(output.toByteArray()); //转换成功了
                        myFollowItem.setImageid(R.drawable.ic_star_black_24dp);
                        followItemList.add(myFollowItem);
                        try {
                            output.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                result = "Succeed in getting follows";
            } catch (JSONException e) {
                result = "获取关注列表失败TAT";
                e.printStackTrace();
            }
        }
        return result;
    }

    // 更新至本地数据库
    private String storeInLocal() {
        for (int i = 0; i < followItemList.size(); i++) {
            MyFollow myFollow = new MyFollow();
            myFollow.setAccount(followItemList.get(i).getAccount());
            myFollow.setName(followItemList.get(i).getName());
            myFollow.setIntroduction(followItemList.get(i).getIntroduction());
            myFollow.setFace(followItemList.get(i).getFace());
            myFollow.setMyAccount(myAccount);
            if (DataSupport.select("account")
                    .where("account = ?", followItemList.get(i).getAccount())
                    .find(MyFollow.class).size() != 0) {
                myFollow.updateAll("account = ?", followItemList.get(i).getAccount());
            } else {
                myFollow.save();
            }
        }
        return "Succeed in storing";
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
            else if (params[0].equals("storeInLocal")) {
                result = storeInLocal();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting follow list")) {
                if (myFollow.size() > 0) {
                    downTask = new DownTask();
                    downTask.execute("getFollows");
                }
            }
            else if (s.equals("Succeed in getting follows")) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_follow);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MyFollowActicity.this);
                recyclerView.setLayoutManager(layoutManager);
                MyFollowAdapter adapter = new MyFollowAdapter(followItemList);
                recyclerView.setAdapter(adapter);
                //设置Item增加、移除动画
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                downTask = new DownTask();
                downTask.execute("storeInLocal");
            }
            else {
                Toast.makeText(MyFollowActicity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
