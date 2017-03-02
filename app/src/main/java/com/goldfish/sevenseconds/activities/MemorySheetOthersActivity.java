package com.goldfish.sevenseconds.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.AmemOtherAdapter;
import com.goldfish.sevenseconds.fragment.AmemReviewFragment;
import com.gxz.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/3/2.
 */

public class MemorySheetOthersActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static MemorySheetOthersActivity memorySheetOthersActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amem_other);
        memorySheetOthersActivity = this;

        pager = (ViewPager) findViewById(R.id.amem_pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.amem_tabs);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("转发");
        titles.add("评论");
        titles.add("收藏");
        titles.add("点赞");
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String s : titles) {
            Bundle bundle = new Bundle();
            bundle.putString("title", s);
            fragments.add(AmemReviewFragment.getInstance(bundle));
        }
        pager.setAdapter(new AmemOtherAdapter(getSupportFragmentManager(), titles, fragments));
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);
    }
}
