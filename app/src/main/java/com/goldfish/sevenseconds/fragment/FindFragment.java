package com.goldfish.sevenseconds.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.Addmem;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.SearchActivity;
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
    private SearchView mSearchView;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private ImageView editMemory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        View view = inflater.inflate(R.layout.fragment_find,container,false);
        editMemory = (ImageView) view.findViewById(R.id.find_edit);
        editMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Addmem.class);
                startActivity(intent);
            }
        });
        //final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        //indicator.setColor(Color.WHITE);
        //getSupportActionBar().setHomeAsUpIndicator(indicator);
        // hide the action bar
        /*ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }*/

        /* creat the timeline view */
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //搜索
                Toast.makeText(getContext(), "你搜索了" + query, Toast.LENGTH_LONG).show();
                mSearchView.clearFocus();
                searchfor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //文本改变
                Toast.makeText(getContext(), "文本改变了" + newText, Toast.LENGTH_LONG);
                return false;
            }
        });
        Button btn1 = (Button) view.findViewById(R.id.year00);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getContext(), "xxx", Toast.LENGTH_LONG);
            }
        });
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

    public void searchfor(String query){
        Intent intent = new Intent(BarActivity.barActivity, SearchActivity.class);
        intent.putExtra("querydata", query);
        startActivity(intent);
    }


}



