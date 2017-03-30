package com.goldfish.sevenseconds.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.TimeLineAdapter;
import com.goldfish.sevenseconds.adapter.TimeLineSideBarAdapter;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.item.TimeLineModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class FindFragment extends Fragment {
    private String name;
    private TimeLineAdapter mTimeLineAdapter;
    private TimeLineSideBarAdapter mTimeLineSideBarAdapter;
    private DrawerLayout drawerLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView RecyclerViewInSideBar;
    private Orientation mOrientation;

    private List<TimeLineModel> mDataList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        View view = inflater.inflate(R.layout.fragment_find,container,false);

        //final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        //indicator.setColor(Color.WHITE);
        //getSupportActionBar().setHomeAsUpIndicator(indicator);
        // hide the action bar
        /*ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }*/

        /* creat the timeline view */
        mOrientation = Orientation.horizontal;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);
        initTimeLineView();

        RecyclerViewInSideBar = (RecyclerView) view.findViewById(R.id.recyclerViewInSideBar);
        RecyclerViewInSideBar.setLayoutManager(getLinearLayoutManager());
        RecyclerViewInSideBar.setHasFixedSize(true);
        initTimeLineViewInSideBar();
        /*drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (((ViewGroup) drawerView).getChildAt(1).getId() == R.id.leftSideBar) {
                    //indicator.setProgress(slideOffset);
                }
            }
        });*/
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    public static FindFragment newInstance(String libargument)
    {
        Bundle bundle = new Bundle();
        FindFragment mm = new FindFragment();
        return mm;
    }
    private LinearLayoutManager getLinearLayoutManager() {

        if (mOrientation == Orientation.horizontal) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            return linearLayoutManager;
        } else {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
            return linearLayoutManager;
        }

    }
    private void initTimeLineView() {

        for(int i = 0;i <20;i++) {
            TimeLineModel model = new TimeLineModel();
            model.setName("Random"+i);
            model.setAge(i);
            mDataList.add(model);
        }
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }
    private void initTimeLineViewInSideBar(){
        for(int i = 0;i <20;i++) {
            TimeLineModel model = new TimeLineModel();
            model.setName("Random"+i);
            model.setAge(i);
            mDataList.add(model);
        }
        mTimeLineSideBarAdapter = new TimeLineSideBarAdapter(mDataList, mOrientation);
        RecyclerViewInSideBar.setAdapter(mTimeLineSideBarAdapter);
    }
}



