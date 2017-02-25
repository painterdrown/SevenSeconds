package com.goldfish.sevenseconds.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.service.GetLogMSG;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        Button button_register = (Button)findViewById(R.id.btn_signup);
        final EditText editTextn = (EditText) findViewById(R.id.input_name);
        EditText editTextp = (EditText) findViewById(R.id.input_password);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextn.getText().toString();
                String password = editTextn.getText().toString();
                if (password == "") Toast.makeText(RegisterActivity.this,"your do not input password",Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(RegisterActivity.this,"register succeed",Toast.LENGTH_LONG).show();
                    GetLogMSG.upload(username,password);
                    finish();}
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
