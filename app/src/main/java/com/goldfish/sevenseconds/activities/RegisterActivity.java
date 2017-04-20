package com.goldfish.sevenseconds.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.http.UserHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {

    private String username;
    private String password;
    private  boolean check;
    private RegisterActivity registerActivity;
    private  String err_msg;
    private Handler handler = new Handler() {
      public void handleMessage(Message msg){
          switch (msg.what){
              case 1:
                  Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                  Intent intent = new Intent(RegisterActivity.this,LogActivity.class);
                  startActivity(intent);
                  break;
              case 0:
                  Toast.makeText(RegisterActivity.this, err_msg, Toast.LENGTH_LONG).show();
                  break;
              default:
                  break;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BaseActivity.getInstance().addActivity(this);
        registerActivity = this;

        Exception();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        Button button_register = (Button)findViewById(R.id.register_ensure);
        final EditText editTextn = (EditText) findViewById(R.id.input_name);
        final EditText editTextp = (EditText) findViewById(R.id.input_password);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextn.getText().toString();
                password = editTextp.getText().toString();
                String regUser = "^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{6,16}$";
                if(!username.matches(regUser)) {
                    if(username.length() >= 6 && username.length() <= 18) {
                        Toast.makeText(RegisterActivity.this, "您的用户名不合法", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "您的用户名长度不合法，应该在6~18位", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    String regPass = "^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$";
                    if(!password.matches(regPass)) {
                        if (password.length() >= 6 && password.length() <= 18) {
                            Toast.makeText(RegisterActivity.this, "您的密码不合法", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "您的密码长度不合法，应该在6~18位", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jo = new JSONObject();
                                try {
                                    jo.put("account",username);
                                    jo.put("password",password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject answer =new JSONObject();
                                jo = UserHttpUtil.register(jo);
                                try {
                                    check = jo.getBoolean("ok");
                                    err_msg= jo.getString("errMsg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                if (check == true) msg.what = 1;else msg.what = 0;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                }
            }
        });
        Button button_return = (Button)findViewById(R.id.link_login);
        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity.getInstance().finishActivity(registerActivity);
            }
        });
    }
}
