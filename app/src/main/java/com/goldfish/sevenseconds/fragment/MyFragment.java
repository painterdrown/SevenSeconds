package com.goldfish.sevenseconds.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.InformationActivity;
import com.goldfish.sevenseconds.activities.MessageActivity;
import com.goldfish.sevenseconds.activities.MyFollowActicity;
import com.goldfish.sevenseconds.activities.SettingActivity;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.tools.Http;
import com.goldfish.sevenseconds.view.TurnCardListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class MyFragment extends Fragment {

    private String name;
    private SQLiteOpenHelper dbChattingDatabaseHelper;
    private String currentUser;
    private Bitmap face;
    private ImageView headPortrait;
    private Toolbar toolbar;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        // 成功登陆的账号
        currentUser = "a";

        View view = inflater.inflate(R.layout.fragment_my_page,container,false);
        RelativeLayout myMessage = (RelativeLayout) view.findViewById(R.id.myMessage);
        RelativeLayout myInformation = (RelativeLayout) view.findViewById(R.id.myInformation);
        RelativeLayout mySetting = (RelativeLayout) view.findViewById(R.id.mySetting);
        RelativeLayout myFollow = (RelativeLayout) view.findViewById(R.id.myFollow);
        headPortrait = (ImageView) view.findViewById(R.id.headPortrait);

        DownTask downTask = new DownTask();
        downTask.execute("getImage");


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
                myToFollow.putExtra("currentUser", "y741323965");  // 到时候改成登陆成功的对象账号
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
        /*TurnCardListView list = (TurnCardListView) view.findViewById(R.id.card_list);

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
                return child;
            }
        });*/
        return view;
    }

    private String getImage() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            face = Http.getUserFace(jo);
            if (face != null) {
                result = "Succeed in getting face";
            } else {
                result = "服务器故障啦~";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }


    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            if (params[0].equals("getImage")) {
                result = getImage();
            } else {
                result = params[0];
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting face")) {
                headPortrait.setImageBitmap(face);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        DownTask downTask = new DownTask();
        downTask.execute("getImage");
    }

    public static MyFragment newInstance(String libargument) {
        Bundle bundle = new Bundle();
        MyFragment mm = new MyFragment();
        return mm;
    }
}
