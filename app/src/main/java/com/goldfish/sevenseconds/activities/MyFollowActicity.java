package com.goldfish.sevenseconds.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goldfish.sevenseconds.bean.MyFollow;
import com.goldfish.sevenseconds.adapter.MyFollowAdapter;
import com.goldfish.sevenseconds.item.MyFollowItem;
import com.goldfish.sevenseconds.R;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
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

        // 本地更新
        db = Connector.getDatabase();
        List<MyFollow> myFollowList = DataSupport.findAll(MyFollow.class);
        if (myFollowList.size() > 0) {
            for (MyFollow myFollow : myFollowList) {
                MyFollowItem myFollowItem = new MyFollowItem(
                        R.drawable.red_love, myFollow.getName(),
                        myFollow.getIntroduction(), myFollow.getFace());
                followItemList.add(myFollowItem);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
