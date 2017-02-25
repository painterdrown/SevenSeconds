package com.goldfish.sevenseconds.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldfish.sevenseconds.item.MyFollowItem;
import com.goldfish.sevenseconds.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/23.
 */

public class MyFollowAdapter extends RecyclerView.Adapter<MyFollowAdapter.ViewHolder>  {

    private List<MyFollowItem> mMyFollowList = new ArrayList<>();

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_follow_item, null, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.myFollowView.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition ();
                MyFollowItem myFollow = mMyFollowList.get (position);
                // 跳转到个人界面
            }
        });
        holder.loveImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition ();
                MyFollowItem myFollowItem = mMyFollowList.get (position);
                if (holder.loveImage.getTag ().equals ("red_love")) {
                    holder.loveImage.setImageResource (R.drawable.blank_love);
                    holder.loveImage.setTag ("blank_love");
                } else {
                    holder.loveImage.setImageResource (R.drawable.red_love);
                    holder.loveImage.setTag ("red_love");
                }
            }
        });
        return holder;
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
    }

    @Override
    public int getItemCount() {
        return mMyFollowList.size();
    }

}
