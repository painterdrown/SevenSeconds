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
import com.goldfish.sevenseconds.activities.SquareActivity;
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
                Intent myToSetting = new Intent(SquareActivity.squareActivity, SettingActivity.class);
                startActivity(myToSetting);
            }
        });

        myInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToInformation = new Intent(SquareActivity.squareActivity, InformationActivity.class);
                myToInformation.putExtra("currentUser", "y741323965");  // 到时候改成登陆成功的对象账号
                startActivity(myToInformation);
            }
        });

        myFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToFollow = new Intent(SquareActivity.squareActivity, MyFollowActicity.class);
                startActivity(myToFollow);
            }
        });

        myMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myToMessage = new Intent(SquareActivity.squareActivity, MessageActivity.class);
                startActivity(myToMessage);
            }
        });
        TurnCardListView list = (TurnCardListView) view.findViewById(R.id.card_list);

        list.setOnTurnListener(new TurnCardListView.OnTurnListener() {
            @Override
            public void onTurned(int position) {
                Toast.makeText(SquareActivity.squareActivity, "position = " + position, Toast.LENGTH_SHORT).show();
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 测试
        /*Connector.getWritableDatabase();
        MyFollow test = new MyFollow();
        test.setName("世吹雀");
        test.setAccount("noend22");
        test.setIntroduction("大提琴/甜甜圈四重奏");
        Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        test.setFace(os.toByteArray());
        test.save();*/

        /*dbChattingDatabaseHelper = new ChattingDatabaseHelper(
                this, "MessageStore.db", null, 1);
        SQLiteDatabase dbMessage1 = dbChattingDatabaseHelper.getWritableDatabase();
        dbMessage1.execSQL("create table if not exists noend22 ("
                + "id integer primary key autoincrement, "
                + "account text, "
                + "message text, "
                + "time text, "
                + "sendOrReceive integer, "
                + "readOrNot integer)");
        dbMessage1.execSQL("insert into noend22 (account, message, time, sendOrReceive, readOrNot) " +
                        "values(?, ?, ?, ?, ?)",
                new String[]{"noend22", "Hey", "2017/2/24 10:32", "0", "0"});
        dbMessage1.execSQL("insert into noend22 (account, message, time, sendOrReceive, readOrNot) " +
                        "values(?, ?, ?, ?, ?)",
                new String[]{"y741323965", "Hey! What's up!", "2017/2/24 10:32", "1", "1"});
        dbMessage1.execSQL("insert into noend22 (account, message, time, sendOrReceive, readOrNot) " +
                        "values(?, ?, ?, ?, ?)",
                new String[]{"noend22", "Miss me?", "2017/2/24 10:33", "0", "0"});*/

        /*Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        Information test1 = new Information();
        test1.setFace(os.toByteArray());
        test1.setSex("男");
        test1.setIntroduction("穿睡服的金鱼/梦幻西游");
        test1.setName("Goldfish");
        test1.setAccount("y741323965");
        test1.setBirthday("1997-1-2");
        test1.setPhone("13719326474");
        test1.save();

        Information test2 = new Information();
        test2.setPhone("13502852468");
        test2.setBirthday("1997-1-1");
        test2.setAccount("noend22");
        test2.setName("世吹雀");
        test2.setIntroduction("大提琴/甜甜圈四重奏");
        test2.setSex("女");
        test2.save();*/
    }
    public static MyFragment newInstance(String libargument)
    {
        Bundle bundle = new Bundle();
        MyFragment mm = new MyFragment();
        return mm;
    }
}
