package com.goldfish.sevenseconds.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldfish.sevenseconds.activities.LogActivity;
import com.goldfish.sevenseconds.activities.UserHomePageActivity;
import com.goldfish.sevenseconds.http.UserHttpUtil;
import com.goldfish.sevenseconds.item.MyFollowItem;
import com.goldfish.sevenseconds.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/23.
 */

public class MyFollowAdapter extends RecyclerView.Adapter<MyFollowAdapter.ViewHolder>  {

    private List<MyFollowItem> mMyFollowList = new ArrayList<>();
    private String memAccount;
    private String myAccount;
    private ViewHolder holder;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userFace;
        ImageView loveImage;
        TextView userName;
        TextView userIntroduction;
        View myFollowView;

        public ViewHolder (View view) {
            super(view);
            myFollowView = view;
            userFace = (ImageView) view.findViewById(R.id.follow_face);
            userName = (TextView) view.findViewById(R.id.follow_name);
            userIntroduction = (TextView) view.findViewById(R.id.follow_introduction);
            loveImage = (ImageView) view.findViewById(R.id.follow_image);
        }
    }

    public MyFollowAdapter(List<MyFollowItem> MyFollowList) {
        mMyFollowList = MyFollowList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_follow_item, null, false);
        holder = new ViewHolder(view);
        holder.myFollowView.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition ();
                MyFollowItem myFollow = mMyFollowList.get (position);
                Intent intent = new Intent(parent.getContext(), UserHomePageActivity.class);
                intent.putExtra("account", myFollow.getAccount());
                parent.getContext().startActivity(intent);
            }
        });
        holder.loveImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition ();
                MyFollowItem myFollowItem = mMyFollowList.get (position);
                if (!holder.loveImage.getTag ().equals ("red_love")) {
                    DownTask downTask = new DownTask();
                    downTask.execute("follow");
                } else {
                    DialogInterface.OnClickListener dialogOnclickListerner = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Dialog.BUTTON_POSITIVE:
                                    DownTask downTask = new DownTask();
                                    downTask.execute("unfollow");
                                    break;
                                case Dialog.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    builder.setTitle("提示");
                    builder.setMessage("是否取消关注该用户？");
                    builder.setIcon(R.drawable.app_icon);
                    builder.setPositiveButton("确认", dialogOnclickListerner);
                    builder.setNegativeButton("取消", dialogOnclickListerner);
                    builder.create().show();
                }
            }
        });
        return holder;
    }

    // 添加关注
    private String follow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", myAccount);
            jo.put("otherAccount", memAccount);
            JSONObject jo_return = UserHttpUtil.addFollow(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in following";
            } else {
                result = jo_return.getString("errMsg");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "添加关注失败";
        }
        return result;
    }

    // 取消关注
    private String unfollow() {
        String result;
        try {
            JSONObject jo = new JSONObject();
            jo.put("myAccount", myAccount);
            jo.put("otherAccount", memAccount);
            JSONObject jo_return = UserHttpUtil.deleteFollow(jo);
            if (jo_return.getBoolean("ok")) {
                result = "Succeed in unfollowing";
            } else {
                result = jo_return.getString("errMsg");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result = "取消关注失败";
        }
        return result;
    }

    private class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "Failed";
            if (params[0].equals("follow")) { result = follow();}
            else if (params[0].equals("unfollow")) { result = unfollow(); }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Succeed in following")) {
                holder.loveImage.setImageResource (R.drawable.ic_star_black_24dp);
                holder.loveImage.setTag ("red_love");}
            else if (result.equals("Succeed in unfollowing")) {
                holder.loveImage.setImageResource (R.drawable.ic_star_border_black_24dp);
                holder.loveImage.setTag ("blank_love");
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyFollowItem myFollowItem = mMyFollowList.get(position);
        holder.userName.setText(myFollowItem.getName());
        holder.loveImage.setImageResource(myFollowItem.getImageid());
        holder.userIntroduction.setText(myFollowItem.getIntroduction());
        holder.userFace.setImageDrawable(
                Drawable.createFromStream(
                        new ByteArrayInputStream(myFollowItem.getFace()), "myFace"));
        memAccount = myFollowItem.getAccount();
        myAccount = LogActivity.user;
    }

    @Override
    public int getItemCount() {
        return mMyFollowList.size();
    }

}
