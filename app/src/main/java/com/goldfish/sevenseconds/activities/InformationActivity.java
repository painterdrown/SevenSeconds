package com.goldfish.sevenseconds.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.goldfish.sevenseconds.bean.Information;
import com.goldfish.sevenseconds.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created by lenovo on 2017/2/22.
 */

public class InformationActivity extends AppCompatActivity {

    private String currentUser;        // 当前操作的User
    private SQLiteDatabase db;            // 数据库

    private TextView setName;           // 设置昵称
    private TextView setSex;            // 设置性别
    private TextView setDate;           // 设置生日
    private ImageView setUserFace;      // 头像
    private EditText setIntroduction;  // 设置个人简介
    private Button confirm;             // 确认修改


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // 得到MyPage操作的当前用户账号
        Intent getCurrentUser = getIntent();
        currentUser = getCurrentUser.getStringExtra("currentUser");

        // 获得各种按钮
        confirm = (Button) findViewById(R.id.confirm);
        setName = (TextView) findViewById(R.id.username);
        setSex = (TextView) findViewById(R.id.select_sex);
        setUserFace = (ImageView) findViewById(R.id.user_face);
        setDate = (TextView) findViewById(R.id.select_birthday);
        setIntroduction = (EditText) findViewById(R.id.introduction);

        Log.d("test","ok");

        initializeInfo();

        setName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeNickname(); }
        });

        setUserFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeFace(); }
        });

        setSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeSex(); }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYearMonthDayPicker(v);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInformationChanged();
            }
        });
    }

    // 对界面进行初始化
    private void initializeInfo() {
        // 本地加载异步联网更新数据库

        db = Connector.getWritableDatabase();
        List<Information> informationList = DataSupport
                .select("name", "sex", "birthday", "introduction", "face")
                .where("account=?", currentUser)
                .find(Information.class);

        if (informationList.size() == 1) {
            for (Information information : informationList) {
                String name = information.getName();
                String sex = information.getSex();
                String birthday = information.getBirthday();
                String introduction = information.getIntroduction();
                byte[] face = information.getFace();
                ByteArrayInputStream is = new ByteArrayInputStream(face);

                setName.setText(name);
                setSex.setText(sex);
                setDate.setText(birthday);
                setIntroduction.setText(introduction);
                setUserFace.setImageDrawable(Drawable.createFromStream(is, "myFace"));
            }
        }
        db.close();
    }

    // 设置昵称
    private void changeNickname() {

        AlertDialog.Builder builder = new AlertDialog.Builder (InformationActivity.this);

        builder.setIcon(R.drawable.app_icon);
        builder.setTitle("请设置昵称");
        View view = LayoutInflater  // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                .from(InformationActivity.this)
                .inflate(R.layout.dialog_set_name, null);
        builder.setView(view);      // 设置我们自己定义的布局文件作为弹出框的Content
        final EditText nickname = (EditText)view.findViewById(R.id.edtTxt_nickname);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nick_a = nickname.getText().toString().trim();
                setName.setText(nick_a);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }

    // 设置头像
    private void changeFace() {
        Intent intent = new Intent(InformationActivity.this, PhotoActivity.class);
        startActivity(intent);
    }

    // 设置性别
    int selectWhich;
    private void changeSex() {

        AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);

        builder.setIcon(R.drawable.app_icon);
        builder.setTitle("请选择性别");
        final String[] sex = {"男", "女"};

        builder.setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectWhich = which;
                Toast.makeText(InformationActivity.this, "性别为：" + sex[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSex.setText(sex[selectWhich]);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.show();
    }

    // 设置生日
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void setToText(String msg) { setDate.setText(msg); }
    public void onYearMonthDayPicker(View view) {
        final DatePicker picker = new DatePicker(this);
        picker.setTopPadding(2);
        picker.setRangeStart(1900, 1, 1);
        picker.setRangeEnd(2018, 1, 1);
        picker.setSelectedItem(2017, 2, 23);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                showToast(year + "-" + month + "-" + day);
                setToText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    // 确认修改
    public void confirmInformationChanged() {

        db = Connector.getWritableDatabase();

        // 从ImageView获得二进制图像
        setUserFace.setDrawingCacheEnabled(true);
        Bitmap obmp = Bitmap.createBitmap(setUserFace.getDrawingCache());
        setUserFace.setDrawingCacheEnabled(false);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obmp.compress(Bitmap.CompressFormat.PNG, 100, os);

        String name = setName.getText().toString();
        String sex = setSex.getText().toString();
        String date = setDate.getText().toString();
        byte[] face = os.toByteArray();
        String introduction = setIntroduction.getText().toString();

        // 在服务器数据库更新

        // 在本地数据库更新
        Information updateInformation = new Information();
        updateInformation.setName(name);
        updateInformation.setBirthday(date);
        updateInformation.setSex(sex);
        updateInformation.setIntroduction(introduction);
        updateInformation.setFace(face);
        if (DataSupport.select("account")
                .where("account = ?", currentUser)
                .find(Information.class) != null) {
            updateInformation.updateAll("account = ?", currentUser);
        } else {
            updateInformation.save();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
