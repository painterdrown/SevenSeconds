package com.goldfish.sevenseconds.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.goldfish.sevenseconds.bean.Information;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.SetInfo;
import com.google.gson.Gson;
import com.jph.takephoto.model.TImage;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;


/**
 * Created by lenovo on 2017/2/22.
 */

public class InformationActivity extends AppCompatActivity {

    private String currentUser;        // 当前操作的User
    private SQLiteDatabase db;            // 数据库
    private final String[] items = { "拍照", "从相册中选择" };
    private DownTask downTask;

    private TextView setName;           // 设置昵称
    private TextView setSex;            // 设置性别
    private TextView setDate;           // 设置生日
    private ImageView setUserFace;      // 头像
    private EditText setIntroduction;  // 设置个人简介
    private Button confirm;             // 确认修改

    private String name;
    private String sex;
    private String birthday;
    private String introduction;
    private byte[] face;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
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

        downTask = new DownTask();
        downTask.execute("get");

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

    // 异步

    class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "failed";
            Gson gson = new Gson();

            // 联网获得个人信息
            if (params[0].equals("get")) {
                try {
                    SetInfo setInfo;
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", currentUser).build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request
                            .Builder()
                            .url("http://139.199.158.84:3000/api/getUserInfo")
                            .post(requestBody).build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        setInfo = gson.fromJson(responseData, SetInfo.class);
                        if (setInfo.getOk()) {
                            name = setInfo.getName();
                            introduction = setInfo.getIntroduction();
                            face = setInfo.getFace();
                            sex = setInfo.getSex();
                            birthday = setInfo.getBirthday();
                            result = "success in getting Information";
                        } else {
                            result = setInfo.getErrMsg();
                        }

                    } else {
                        result = "failed";
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    result = "failed";
                }
            }
            // 在服务器数据库更新
            else if (params[0].equals("post")) {
                try {
                    SetInfo setInfo;
                    RequestBody requestBody = new FormBody.Builder()
                            .add("name", name)
                            .add("introduction", introduction)
                            .add("birthday", birthday)
                            .add("sex", sex).build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request
                            .Builder()
                            .url("http://139.199.158.84:3000/api/setUserInfo")
                            .post(requestBody).build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        setInfo = gson.fromJson(responseData, SetInfo.class);
                        if (setInfo.getOk()) {
                            result = "success in posting Information";
                        } else {
                            result = setInfo.getErrMsg();
                        }

                    } else {
                        result = "failed";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success in getting Information")) {
                // 在本地数据库更新
                Information updateInformation = new Information();
                updateInformation.setName(name);
                updateInformation.setBirthday(birthday);
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
                initializeInfo();
            }
            else if (s.equals("success in posting Information")) {
                // 在本地数据库更新
                Information updateInformation = new Information();
                updateInformation.setName(name);
                updateInformation.setBirthday(birthday);
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
            } else {
                Toast.makeText(InformationActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
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

    // 自定义图片加载器
    private ImageLoader loader = new ImageLoader() {
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    // 设置头像
    private void changeFace() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(InformationActivity.this, PhotographActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(InformationActivity.this, SelectPhotoActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });
        builder.show();*/
        ImgSelConfig config = new com.yuyh.library.imgsel.ImgSelConfig.Builder(this, loader)
                // 是否多选
                .multiSelect(false)
                .btnText("Confirm")
                // 确定按钮背景色
                //.btnBgColor(Color.parseColor(""))
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                .title("Images")
                .titleColor(Color.WHITE)
                .titleBgColor(Color.parseColor("#3F51B5"))
                .allImagesText("All Images")
                .needCrop(true)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();

        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            setUserFace.setImageURI(Uri.parse("file://"+pathList.get(0)));
            // 测试Fresco。可不理会
            // draweeView.setImageURI(Uri.parse("file://"+pathList.get(0)));
            /*for (String path : pathList) {

                tvResult.append(path + "\n");
            }*/
        }
    }

    /*private ArrayList<TImage> images;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (requestCode == 1) {
                    images = (ArrayList<TImage>) data.getSerializableExtra("images");
                    Glide.with(this).load(new File(images.get(0).getCompressPath())).into(setUserFace);
                    setName.setText("ok");
                }
        }
    }*/

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

        name = setName.getText().toString();
        sex = setSex.getText().toString();
        birthday = setDate.getText().toString();
        face = os.toByteArray();
        introduction = setIntroduction.getText().toString();

        downTask = new DownTask();
        downTask.execute("post");

        db.close();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
