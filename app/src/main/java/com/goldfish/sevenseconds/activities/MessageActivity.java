package com.goldfish.sevenseconds.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.goldfish.sevenseconds.adapter.MessageAdapter;
import com.goldfish.sevenseconds.fragment.MessageFragment;
import com.goldfish.sevenseconds.R;
import com.gxz.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/2/22.
 */

public class MessageActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static MessageActivity messageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageActivity = this;

        pager = (ViewPager) findViewById(R.id.pager);
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
        pager.setCurrentItem(1);
    }
}
