package com.goldfish.sevenseconds.activities;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.Information;
import com.goldfish.sevenseconds.bean.LastUser;
import com.goldfish.sevenseconds.bean.Lastmes;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.bean.MyFollow;
import com.goldfish.sevenseconds.bean.Users;
import com.goldfish.sevenseconds.db.ChattingDatabaseHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogActivity extends BaseActivity {
    public static String user;
    private String psw;
    private boolean check;
    private  String err_msg;
    private LogActivity logActivity;
    private ChattingDatabaseHelper dbChattingDatabaseHelper;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0: check = false;
                case 1: check = true;
            }
        }
    };
    public void Exception(){
        //避免出现android.os.NetworkOnMainThreadException异常
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Connector.getDatabase();
        super.onCreate(savedInstanceState);
        logActivity = this;
        BaseActivity.getInstance().addActivity(this);
        Exception();
        setContentView(R.layout.activity_log);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) actionBar.hide();
        List<Lastmes> lastmess = DataSupport.findAll(Lastmes.class);
        if (lastmess.size() != 0)  {
            Lastmes lastmes = DataSupport.findFirst(Lastmes.class);
            user = lastmes.getUsername();
            psw = lastmes.getPass();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        compare_user();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (check == true) {
                        List<LastUser> lastusers = DataSupport.findAll(LastUser.class);
                        if (lastusers.size() == 0){
                            LastUser lastUser = new LastUser();
                            lastUser.setName(user);
                            lastUser.save();
                        }
                        LastUser lastUser = new LastUser();
                        lastUser.setName(user);
                        lastUser.updateAll();
                        Intent intent = new Intent(LogActivity.this, BarActivity.class);
                        startActivity(intent);
                        BaseActivity.getInstance().finishActivity(logActivity);
                                      }
            }
                            }).start();
        }
        Button button_log = (Button) findViewById(R.id.button_log);
        Button button_register = (Button) findViewById(R.id.button_register);
        Button button_return = (Button) findViewById(R.id.return_something);
        EditText edittextuser = (EditText) findViewById(R.id.input_name);
        List<LastUser> lastUsers = DataSupport.findAll(LastUser.class);
        if (lastUsers.size() != 0){
            edittextuser.setText(lastUsers.get(0).getName());
        }
        button_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edittextuser = (EditText) findViewById(R.id.input_name);
                EditText edittextpsw = (EditText) findViewById(R.id.input_password);
                user = edittextuser.getText().toString();
                psw = edittextpsw.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(LogActivity.this);
                progressDialog.setTitle("is loging");
                progressDialog.setMessage("waiting");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            compare_user();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (check == true) {
                            LastUser llast = new LastUser();
                            llast.setName(user);
                            llast.updateAll();
                            List<Lastmes> lastmess = DataSupport.findAll(Lastmes.class);
                            if (lastmess.size() != 0)  DataSupport.deleteAll(Lastmes.class);
                            Lastmes lastMes = new Lastmes();
                            lastMes.setUsername(user);
                            lastMes.setPass(psw);
                            lastMes.save();
                            List<LastUser> lastusers = DataSupport.findAll(LastUser.class);
                            if (lastusers.size() == 0){
                                LastUser lastUser = new LastUser();
                                lastUser.setName(user);
                                lastUser.save();
                            }
                            LastUser lastUser = new LastUser();
                            lastUser.setName(user);
                            lastUser.updateAll();
                            progressDialog.dismiss();
                            Intent intent = new Intent(LogActivity.this, BarActivity.class);
                            startActivity(intent);
                            BaseActivity.getInstance().finishActivity(logActivity);
                        } else {
                            progressDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(LogActivity.this, err_msg, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.sysu7s.cn/contact-us/"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(logActivity).setTitle("系统提示")//设置对话框标题
                    .setMessage("是否退出7秒")//设置显示的内容
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            BaseActivity.getInstance().exit();
                        }
                    }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            }).show();//在按键响应事件中显示此对话框
        }
        return true;
    }

    private void compare_user() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("account",user);
        jo.put("password",psw);
        JSONObject answer = new JSONObject();
        answer = UserHttpUtil.login(jo);
        check = answer.getBoolean("ok");
        err_msg = answer.getString("errMsg");
        Log.d("err_msg",err_msg);
    }
}