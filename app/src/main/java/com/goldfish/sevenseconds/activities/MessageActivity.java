package com.goldfish.sevenseconds.activities;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.goldfish.sevenseconds.adapter.MessageAdapter;
import com.goldfish.sevenseconds.adapter.MyReviewAdapter;
import com.goldfish.sevenseconds.fragment.MessageFragment;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.http.CommentHttpUtil;
import com.goldfish.sevenseconds.http.MemoryHttpUtil;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MyReviewItem;
import com.gxz.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class MessageActivity extends BaseActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public static MessageActivity messageActivity;
    private List<MyReviewItem> myReviewItems = new ArrayList<>();
    private ArrayList<JSONObject> jsonComment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        BaseActivity.getInstance().addActivity(this);

        messageActivity = this;
        ImageView back = (ImageView) findViewById(R.id.my_review_back);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在加载您的评论");
        progressDialog.setMessage("请稍候~");
        progressDialog.setCancelable(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.getInstance().finishActivity(messageActivity);
            }
        });
        init();

        // 后期有即时聊天系统再加
        /*pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("聊天");
        titles.add("评论");
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (String s : titles) {
            Bundle bundle = new Bundle();
            bundle.putString("title", s);
            fragments.add(MessageFragment.getInstance(bundle));
        }
        pager.setAdapter(new MessageAdapter(getSupportFragmentManager(), titles, fragments));
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);*/
    }

    private void init() {
        progressDialog.show();
        DownTask downTask = new DownTask();
        downTask.execute("getCommentAboutMe");
    }

    private String getCommentAboutMe() {
        String result = "Failed in geting comment about me";
        try {
            JSONObject jo = new JSONObject();
            jo.put("account", LogActivity.user);
            jsonComment = CommentHttpUtil.getCommentsAboutMe(jo);
            if (jsonComment != null)
                if (jsonComment.size() > 0) {
                    for (int i = 0; i < jsonComment.size(); i++) {
                        MyReviewItem myReviewItem = new MyReviewItem();
                        myReviewItem.setTime(jsonComment.get(i).getString("time"));
                        myReviewItem.setOtherMessage(jsonComment.get(i).getString("content"));
                        jo = new JSONObject();
                        jo.put("account", jsonComment.get(i).getString("account"));
                        JSONObject user = UserHttpUtil.getUserInfo(jo);
                        myReviewItem.setIsMemory(true);
                        Bitmap face = UserHttpUtil.getUserFace(jo);
                        if (user.getBoolean("ok") && face != null) {
                            myReviewItem.setFace(face);
                            myReviewItem.setName(user.getString("username"));
                        }
                        jo = new JSONObject();
                        jo.put("memoryId", jsonComment.get(i).getString("memoryId"));
                        myReviewItem.setMemoryId(jsonComment.get(i).getString("memoryId"));
                        JSONObject memory = MemoryHttpUtil.getMemory(jo);
                        if (memory.getBoolean("ok")) {
                            myReviewItem.setTitle(memory.getString("title"));
                        }
                        jo.put("i", 0);
                        Bitmap cover = MemoryHttpUtil.getMemoryImg(jo);
                        if (cover != null) myReviewItem.setCover(cover);
                        myReviewItems.add(myReviewItem);
                    }
                }
            result = "Succeed in getting comment";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void refreshComment() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_review_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        MyReviewAdapter adapter = new MyReviewAdapter(myReviewItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "Failed in AsyncTask";
            switch (params[0]){
                case "getCommentAboutMe":
                    result = getCommentAboutMe();
                    break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result){
                case "Succeed in getting comment":
                    refreshComment();
                    break;
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}
