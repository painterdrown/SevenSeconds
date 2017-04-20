package com.goldfish.sevenseconds.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lenovo on 2017/4/4.
 */

public class TimeCapsuleManagerActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        BaseActivity.getInstance().addActivity(this);

    }
}
