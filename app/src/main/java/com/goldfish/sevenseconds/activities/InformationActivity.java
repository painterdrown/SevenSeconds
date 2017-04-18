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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.goldfish.sevenseconds.bean.Information;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

/**
 * Created by lenovo on 2017/2/22.
 */

public class InformationActivity extends AppCompatActivity {

    private String currentUser;        // 当前操作的User
    private SQLiteDatabase db;            // 数据库
    private DownTask downTask;

    private RelativeLayout setNameLayout;
    private RelativeLayout setSexLayout;
    private RelativeLayout setFaceLayout;
    private RelativeLayout setDateLayout;
    private ImageView back;

    private TextView setName;           // 设置昵称
    private TextView setSex;            // 设置性别
    private TextView setDate;           // 设置生日
    private ImageView setUserFace;      // 头像
    private EditText setIntroduction;  // 设置个人简介
    private Button confirm;             // 确认修改
    private TextView setAccount;

    private Information information;  // 作者信息
    private Bitmap face;
    private Uri uri;
    private String uri_str;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_set_information);

        // 得到MyPage操作的当前用户账号
        Intent getCurrentUser = getIntent();
        currentUser = getCurrentUser.getStringExtra("currentUser");
        information = new Information();

        // 测试
        currentUser = LogActivity.user;
        information.setAccount(currentUser);

        // 获得各种按钮
        confirm = (Button) findViewById(R.id.set_info_save);
        setName = (TextView) findViewById(R.id.set_name);
        setSex = (TextView) findViewById(R.id.set_sex);
        setUserFace = (ImageView) findViewById(R.id.set_face);
        setDate = (TextView) findViewById(R.id.set_birthday);
        setIntroduction = (EditText) findViewById(R.id.set_introduction);
        setNameLayout = (RelativeLayout) findViewById(R.id.set_name_layout);
        setSexLayout = (RelativeLayout) findViewById(R.id.set_sex_layout);
        setDateLayout = (RelativeLayout) findViewById(R.id.set_birthday_layout);
        setFaceLayout = (RelativeLayout) findViewById(R.id.set_face_layout);
        back = (ImageView) findViewById(R.id.set_info_back);
        setAccount = (TextView) findViewById(R.id.user_account);

        downTask = new DownTask();
        downTask.execute("getUserInfo");

        setNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeNickname(); }
        });
        setFaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeFace(); }
        });
        setSexLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeSex(); }
        });
        setDateLayout.setOnClickListener(new View.OnClickListener() {
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setAccount.setText(currentUser);
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

    // 联网获得个人信息
    private String getUserInfo() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            JSONObject jo_return = UserHttpUtil.getUserInfo(jo);
            if (jo_return.getBoolean("ok")) {
                information.setName(jo_return.getString("username"));
                information.setIntroduction(jo_return.getString("introduction"));
                information.setBirthday(jo_return.getString("birthday").substring(0, 10));
                information.setSex(jo_return.getString("sex"));
                face = UserHttpUtil.getUserFace(jo);
                result = "Succeed in getting information";
            } else{
                result = "获取用户信息失败 TAT";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    // 更新个人信息
    private String setUserInfo() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", currentUser);
            jo.put("username", information.getName());
            jo.put("introduction", information.getIntroduction());
            jo.put("birthday", information.getBirthday());
            jo.put("sex", information.getSex());
            JSONObject jo_return = UserHttpUtil.modifyUserInfo(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in posting information";
                if (uri != null) {
                    jo = new JSONObject();
                    jo.put("account", currentUser);
                    JSONObject image_return = UserHttpUtil.setUserFace(jo, uri_str);
                    if (image_return.getBoolean("ok")) {
                        result = "Succeed in posting information";
                    } else {
                        result = "头像设置出错！";
                    }
                }
            } else{
                result = jo_return.getString("errMsg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器故障啦~";
        }
        return result;
    }

    // 找图片
    @NonNull
    private String getImage() {
        face = getBitmapFromUri(uri);
        if (face == null)
            return "Failed";
        else return "Succeed in getting image";
    }

    // 更新至本地数据库
    private String storeInLocal() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        face.compress(Bitmap.CompressFormat.PNG, 100, os);

        Information updateInformation = new Information();
        updateInformation.setName(information.getName());
        updateInformation.setBirthday(information.getBirthday());
        updateInformation.setSex(information.getSex());
        updateInformation.setIntroduction(information.getIntroduction());
        updateInformation.setFace(os.toByteArray());
        updateInformation.setAccount(information.getAccount());
        if (DataSupport.select("account")
                .where("account = ?", currentUser)
                .find(Information.class).size() != 0) {
            updateInformation.updateAll("account = ?", currentUser);
        } else {
            updateInformation.save();
        }
        return "Succeed in storing";
    }

    // 异步
    private class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result;
            if (params[0].equals("getUserInfo")) { result = getUserInfo(); }
            else if (params[0].equals("setUserInfo")) { result = setUserInfo(); }
            else if (params[0].equals("getImage")) { result = getImage();}
            else if (params[0].equals("storeInLocal")) { result = storeInLocal(); }
            else { result = "Failed"; }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Succeed in getting information")) {
                DownTask store = new DownTask();
                store.execute("storeInLocal");
            }
            else if (s.equals("Succeed in posting information")) {
                Toast.makeText(InformationActivity.this, s, Toast.LENGTH_SHORT).show();
            }
            else if (s.equals("Succeed in getting image")){
                setUserFace.setImageBitmap(face);
                if (face.isRecycled()) {
                    face.recycle();
                }
            }
            else if (s.equals("Succeed in storing")) {
                initializeInfo();
            }
        }
    }

    // 设置昵称
    private void changeNickname() {

        AlertDialog.Builder builder = new AlertDialog.Builder (InformationActivity.this);

        //builder.setIcon(R.drawable.app_icon);
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
        ImgSelConfig config = new com.yuyh.library.imgsel.ImgSelConfig.Builder(this, loader)
                .multiSelect(false)
                .btnText("Confirm")
                .btnTextColor(Color.WHITE)
                .statusBarColor(Color.parseColor("#3F51B5"))
                .backResId(R.drawable.ic_back)
                .title("Images")
                .titleColor(Color.WHITE)
                .titleBgColor(Color.parseColor("#3F51B5"))
                .allImagesText("All Images")
                .needCrop(true)
                .cropSize(1, 1, 100, 100)
                .needCamera(true)
                .maxNum(9)
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Nullable
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
        // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            uri_str = pathList.get(0);
            uri = Uri.parse("file://"+ pathList.get(0));
            DownTask getImage = new DownTask();
            getImage.execute("getImage");
        }
    }

    // 设置性别
    int selectWhich;
    private void changeSex() {

        AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);

        //builder.setIcon(R.drawable.app_icon);
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
        face = Bitmap.createBitmap(setUserFace.getDrawingCache());
        setUserFace.setDrawingCacheEnabled(false);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        face.compress(Bitmap.CompressFormat.PNG, 100, os);

        information.setName(setName.getText().toString());
        information.setSex(setSex.getText().toString());
        information.setBirthday(setDate.getText().toString());
        information.setIntroduction(setIntroduction.getText().toString());
        information.setFace(os.toByteArray());

        db.close();

        downTask = new DownTask();
        downTask.execute("setUserInfo");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
