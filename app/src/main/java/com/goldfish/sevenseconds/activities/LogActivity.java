package com.goldfish.sevenseconds.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.Users;
import com.google.gson.Gson;

import org.litepal.tablemanager.Connector;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogActivity extends AppCompatActivity {

    private boolean check;
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
        //Exception();
        setContentView(R.layout.activity_log);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        Button button_log = (Button) findViewById(R.id.button_log);
        Button button_register = (Button) findViewById(R.id.button_register);
        Button button_return = (Button) findViewById(R.id.return_something);
        button_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edittextuser = (EditText) findViewById(R.id.editText1);
                EditText edittextpsw = (EditText) findViewById(R.id.editText2);
                String user;
                String psw;
                user = edittextuser.getText().toString();
                psw = edittextpsw.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(LogActivity.this);
                progressDialog.setTitle("is loging");
                progressDialog.setMessage("waiting");
                progressDialog.setCancelable(false);
                progressDialog.show();


                /*
                List<LastUser> llasts = DataSupport.findAll(LastUser.class);
                if (llasts.size() != 0) {
                    edittextuser.setText(llasts.get(0).getName());
                }
                */


                check = true;
                //compare_user(user, psw);
                if (check == true) {
                        /*LastUser llast = new LastUser();
                        llast.setName(user);
                        llast.updateAll();*/
                    progressDialog.dismiss();
                    Intent intent = new Intent(LogActivity.this, SquareActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LogActivity.this, "Your ID not exits or PassWord is Wrong", Toast.LENGTH_LONG).show();
                }
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
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);
            }
        });
    }

    private void compare_user(String username, String password) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://139.199.158.84:3000/api/signin").build();
            Response response = null;
            response = client.newCall(request).execute();
            String reponseData = response.body().toString();
            Log.d("test",reponseData);
            Gson gson = new Gson();
            Users aa = gson.fromJson(reponseData, Users.class);
            if (username.equals(aa.getUsername()) && password.equals(aa.getPassword())) check = true;
        }
        catch (IOException e) {
                    e.printStackTrace();
                }
    }
}