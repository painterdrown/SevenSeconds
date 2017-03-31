package com.goldfish.sevenseconds.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.SettingAdapter;
import com.goldfish.sevenseconds.bean.Lastmes;
import com.goldfish.sevenseconds.item.SettingItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class SettingActivity extends AppCompatActivity {
    private List<SettingItem> settingItemsList = new ArrayList<SettingItem>();
    public static Activity setingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setingActivity = this;

        ImageView back = (ImageView) findViewById(R.id.my_setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();

        SettingAdapter adapter = new SettingAdapter(
                SettingActivity.this, R.layout.setting_item, settingItemsList);

        ListView listView = (ListView) findViewById(R.id.mySetting);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 //点击跳转(For test)
                if (position == 1) {
                    DataSupport.deleteAll(Lastmes.class);
                    Intent intent = new Intent(SettingActivity.this,LogActivity.class);
                    setingActivity.startActivity(intent);
                }
                if (position == 0) {
                    Intent intent = new Intent(SettingActivity.this, MemoryActivity.class);
                    intent.putExtra("account", "a");
                    intent.putExtra("memoryID", "b");
                    startActivity(intent);
                }
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
