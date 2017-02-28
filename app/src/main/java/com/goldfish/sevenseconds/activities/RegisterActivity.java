package com.goldfish.sevenseconds.activities;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.service.GetLogMSG;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private String username;
    private String password;
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
                if (username.equals(""))
                    Toast.makeText(RegisterActivity.this, "your do not input username", Toast.LENGTH_LONG).show();
                else {
                    if (password.equals(""))
                        Toast.makeText(RegisterActivity.this, "your do not input password", Toast.LENGTH_LONG).show();
                    else {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("username",username)
                                    .add("password",password)
                                    .build();

                            Request request = new Request.Builder().url("http://139.199.158.84:3000/api/register").post(requestBody).build();
                            Response response = null;
                            response = client.newCall(request).execute();
                            String reponseData = response.body().string();
                            if (reponseData.equals("exit!")) Toast.makeText(RegisterActivity.this, "the username is exiting!", Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        Button button_return = (Button)findViewById(R.id.link_login);
        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
