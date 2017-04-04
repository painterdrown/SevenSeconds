package com.goldfish.sevenseconds.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.bean.LastUser;
import com.goldfish.sevenseconds.fragment.MyFragment;
import com.goldfish.sevenseconds.fragment.FindFragment;
import com.goldfish.sevenseconds.fragment.SquareFragment;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import static com.goldfish.sevenseconds.http.UserHttpUtil.getUsername;

public class BarActivity extends AppCompatActivity {
    /*private  Toolbar toolbar;
    private  TextView textView;*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.square_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.bar_plus:
                Intent intent = new Intent(BarActivity.this,Addmem.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }
    public void Exception(){
        //避免出现android.os.NetworkOnMainThreadException异常
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }
    public static BarActivity barActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        barActivity = this;
        Exception();
        Connector.getDatabase();


        /*toolbar = (Toolbar) findViewById(R.id.square_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        textView = (TextView)findViewById(R.id.toolbar_title);
        GetName getName = new GetName();
        getName.execute();*/

        TabView tabView = (TabView)findViewById(R.id.tabView_square);
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.ic_card_membership_orange_36dp, R.drawable.ic_card_membership_black_36dp,"旧胶卷",  SquareFragment.newInstance("旧胶卷"));
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.ic_youtube_searched_for_orange_36dp, R.drawable.ic_youtube_searched_for_black_36dp,"发现",  FindFragment.newInstance("发现"));
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.ic_face_orange_36dp, R.drawable.ic_face_black_36dp,"我的",  MyFragment.newInstance("我的"));
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
    }
    /*class GetName extends AsyncTask<Void,Void,Boolean> {
        private String username;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject jo = new JSONObject();
                List<LastUser> lastusers = DataSupport.findAll(LastUser.class);
                if (lastusers.size() == 0) return false;
                LastUser lastuser = lastusers.get(0);
                jo.put("account", lastuser.getName());
                JSONObject result = getUsername(jo);
                if (result.getBoolean("ok")) {
                    // 成功取到用户名
                    username = result.getString("username");
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                textView.setText(username);
            }
        }
    }*/

}
