package com.goldfish.sevenseconds.activities;

import android.app.Notification;
import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.Information;
import com.goldfish.sevenseconds.bean.LastUser;
import com.goldfish.sevenseconds.bean.Lastmes;
import com.goldfish.sevenseconds.tools.Http;
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

public class LogActivity extends AppCompatActivity {
    private String user;
    private String psw;
    private boolean check;
    private  String err_msg;
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
        Exception();
        setContentView(R.layout.activity_log);
        /*
        Intent intent1 = new Intent(LogActivity.this, BarActivity.class);
        startActivity(intent1);
        */

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
        test.save();

        dbChattingDatabaseHelper = new ChattingDatabaseHelper(
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
                new String[]{"noend22", "Miss me?", "2017/2/24 10:33", "0", "0"});

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
                        finish();
                                      }
            }
                            }).start();
        }
        Button button_connect = (Button)findViewById(R.id.connect_us);
        Button button_log = (Button) findViewById(R.id.button_log);
        Button button_register = (Button) findViewById(R.id.button_register);
        Button button_return = (Button) findViewById(R.id.return_something);
        EditText edittextuser = (EditText) findViewById(R.id.editText1);
        List<LastUser> lastUsers = DataSupport.findAll(LastUser.class);
        if (lastUsers.size() != 0){
            edittextuser.setText(lastUsers.get(0).getName());
        }
        button_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edittextuser = (EditText) findViewById(R.id.editText1);
                EditText edittextpsw = (EditText) findViewById(R.id.editText2);
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
                            finish();
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
                intent.setData(Uri.parse("http://www.sysu7s.cn/contact_us/"));
                startActivity(intent);
            }
        });
    }

    private void compare_user() throws JSONException {
        Http http = new Http();
        JSONObject jo = new JSONObject();
        jo.put("account",user);
        jo.put("password",psw);
        JSONObject answer = new JSONObject();
        answer = http.login(jo);
        check = answer.getBoolean("ok");
        err_msg = answer.getString("errMsg");
        Log.d("err_msg",err_msg);
    }
}