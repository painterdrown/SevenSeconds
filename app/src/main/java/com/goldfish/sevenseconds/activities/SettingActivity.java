package com.goldfish.sevenseconds.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

public class SettingActivity extends BaseActivity {
    private List<SettingItem> settingItemsList = new ArrayList<SettingItem>();
    public static SettingActivity setingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_setting);
        setingActivity = this;

        ImageView back = (ImageView) findViewById(R.id.my_setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.getInstance().finishActivity(setingActivity);
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
                if (position == 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.sysu7s.cn/contact-us/"));
                    startActivity(intent);
                }
                else if (position == 1) {
                    DataSupport.deleteAll(Lastmes.class);
                    Intent intent = new Intent(SettingActivity.this,LogActivity.class);
                    LogActivity.user = null;
                    BaseActivity.getInstance().finishActivity(BarActivity.barActivity);
                    BaseActivity.getInstance().finishAllActivity();
                    setingActivity.startActivity(intent);
                }
                else if (position == 2) {
                    BaseActivity.getInstance().finishActivity(BarActivity.barActivity);
                    BaseActivity.getInstance().exit();
                }
            }
        });

    }
    private void init() {
        SettingItem contactUs = new SettingItem("联系我们");
        settingItemsList.add(contactUs);
        SettingItem logout = new SettingItem(getString(R.string.logout));
        settingItemsList.add(logout);
        SettingItem exit = new SettingItem("退出应用");
        settingItemsList.add(exit);
    }
}
