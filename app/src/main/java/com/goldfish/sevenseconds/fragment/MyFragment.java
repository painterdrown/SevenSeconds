package com.goldfish.sevenseconds.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.InformationActivity;
import com.goldfish.sevenseconds.activities.MessageActivity;
import com.goldfish.sevenseconds.activities.MyFollowActicity;
import com.goldfish.sevenseconds.activities.SettingActivity;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.view.TurnCardListView;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class MyFragment extends Fragment {

    private String name;
    private SQLiteOpenHelper dbChattingDatabaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        View view = inflater.inflate(R.layout.fragment_my_page,container,false);
        Button myMessage = (Button) view.findViewById(R.id.myMessage);
        Button myInformation = (Button) view.findViewById(R.id.myInformation);
        Button mySetting = (Button) view.findViewById(R.id.mySetting);
        Button myFollow = (Button) view.findViewById(R.id.myFollow);

        mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToSetting = new Intent(BarActivity.barActivity, SettingActivity.class);
                startActivity(myToSetting);
            }
        });

        myInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToInformation = new Intent(BarActivity.barActivity, InformationActivity.class);
                myToInformation.putExtra("currentUser", "y741323965");  // 到时候改成登陆成功的对象账号
                startActivity(myToInformation);
            }
        });

        myFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToFollow = new Intent(BarActivity.barActivity, MyFollowActicity.class);
                startActivity(myToFollow);
            }
        });

        myMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToMessage = new Intent(BarActivity.barActivity, MessageActivity.class);
                startActivity(myToMessage);
            }
        });
        TurnCardListView list = (TurnCardListView) view.findViewById(R.id.card_list);

        list.setOnTurnListener(new TurnCardListView.OnTurnListener() {
            @Override
            public void onTurned(int position) {
                Toast.makeText(BarActivity.barActivity, "position = " + position, Toast.LENGTH_SHORT).show();
            }
        });


        list.setAdapter(new BaseAdapter() {
            int[] colors = {0xffFF9800, 0xff3F51B5, 0xff673AB7, 0xff006064, 0xffC51162, 0xffFFEB3B, 0xff795548, 0xff9E9E9E};

            @Override
            public int getCount() {
                return colors.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View child, ViewGroup parent) {
                if (child == null) {
                    child = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_memory_item, parent, false);
                }

                //child.findViewById(R.id.image).setBackgroundColor(colors[position]);
                return child;
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MyFragment newInstance(String libargument) {
        Bundle bundle = new Bundle();
        MyFragment mm = new MyFragment();
        return mm;
    }
}
