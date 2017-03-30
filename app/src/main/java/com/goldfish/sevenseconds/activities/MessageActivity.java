package com.goldfish.sevenseconds.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.goldfish.sevenseconds.adapter.MessageAdapter;
import com.goldfish.sevenseconds.adapter.MyReviewAdapter;
import com.goldfish.sevenseconds.fragment.MessageFragment;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.MyReviewItem;
import com.gxz.PagerSlidingTabStrip;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class MessageActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static MessageActivity messageActivity;
    private List<MyReviewItem> myReviewItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        messageActivity = this;
        ImageView back = (ImageView) findViewById(R.id.my_review_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_review_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        MyReviewAdapter adapter = new MyReviewAdapter(myReviewItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());




        // 后期有即时聊天系统再加
        /*pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("聊天");
        titles.add("评论");
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String s : titles) {
            Bundle bundle = new Bundle();
            bundle.putString("title", s);
            fragments.add(MessageFragment.getInstance(bundle));
        }
        pager.setAdapter(new MessageAdapter(getSupportFragmentManager(), titles, fragments));
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);*/
    }

    private void init() {
        for (int i = 0; i < 3; i++) {
            MyReviewItem myReviewItem = new MyReviewItem();
            myReviewItem.setAccount("a");
            myReviewItem.setName("穿睡服的金鱼");
            myReviewItem.setMyMessage("测试我的评论");
            myReviewItem.setOtherMessage("测试别人回复我的评论");
            myReviewItem.setTime("2017-3-30");
            Resources res = getResources();
            Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
            myReviewItem.setFace(bmp);
            myReviewItems.add(myReviewItem);
        }
    }
}
