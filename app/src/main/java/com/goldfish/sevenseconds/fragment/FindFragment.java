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
import android.widget.RelativeLayout;
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
    private RelativeLayout searchLayout;

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
        Button btn1 = (Button) view.findViewById(R.id.year00);
        Button btn2 = (Button) view.findViewById(R.id.year90);
        Button btn3 = (Button) view.findViewById(R.id.year80);
        Button btn4 = (Button) view.findViewById(R.id.year70);
        Button btn5 = (Button) view.findViewById(R.id.label_cartoon);
        Button btn6 = (Button) view.findViewById(R.id.label_game);
        Button btn7 = (Button) view.findViewById(R.id.label_kid);
        Button btn8 = (Button) view.findViewById(R.id.label_music);
        Button btn9 = (Button) view.findViewById(R.id.label_sport);
        Button btn10 = (Button) view.findViewById(R.id.label_tv);
        searchLayout = (RelativeLayout) view.findViewById(R.id.search_layout);
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.setQueryHint("今日忆");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //搜索
                //Toast.makeText(getContext(), "你搜索了" + query, Toast.LENGTH_LONG).show();
                mSearchView.clearFocus();
                searchfor(query, false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //文本改变
                //Toast.makeText(getContext(), "文本改变了" + newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("2000", true);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("1990", true);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("1980", true);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("1970", true);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("动漫", false);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("游戏", false);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("童趣", false);
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("音乐", false);
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("体育", false);
            }
        });
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                searchfor("影视", false);
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

    public void searchfor(String query, boolean year){
        Intent intent = new Intent(BarActivity.barActivity, SearchActivity.class);
        intent.putExtra("querydata", query);
        intent.putExtra("year", year);

        startActivity(intent);
    }


}



