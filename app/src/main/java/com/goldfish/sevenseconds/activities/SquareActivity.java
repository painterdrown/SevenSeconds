package com.goldfish.sevenseconds.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.fragment.MyFragment;
import com.goldfish.sevenseconds.fragment.FindFragment;
import com.goldfish.sevenseconds.fragment.SquareFragment;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.util.ArrayList;
import java.util.List;

public class SquareActivity extends AppCompatActivity {

    public static SquareActivity squareActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        squareActivity = this;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        TabView tabView = (TabView)findViewById(R.id.tabView_square);
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.squaresl,R.drawable.squarensel,"广场",  SquareFragment.newInstance("广场"));
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.searchsl,R.drawable.searchunsl,"发现",  FindFragment.newInstance("发现"));
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.mysl,R.drawable.myunsel,"我的",  MyFragment.newInstance("我的"));
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
    }

}
