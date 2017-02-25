package com.goldfish.sevenseconds.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.SettingAdapter;
import com.goldfish.sevenseconds.item.SettingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class SettingActivity extends AppCompatActivity {
    private List<SettingItem> settingItemsList = new ArrayList<SettingItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();

        SettingAdapter adapter = new SettingAdapter(
                SettingActivity.this, R.layout.setting_item, settingItemsList);

        ListView listView = (ListView) findViewById(R.id.mySetting);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击跳转(For test)
                /*Intent dialogueBox = new Intent(MyToSetting.this, DialogueBox.class);
                startActivity(dialogueBox);*/
            }
        });

    }

    private void init() {
        SettingItem accountmanage = new SettingItem(getString(R.string.account_manage));
        settingItemsList.add(accountmanage);

        SettingItem logout = new SettingItem(getString(R.string.logout));
        settingItemsList.add(logout);
    }
}
