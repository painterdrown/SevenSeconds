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
}



