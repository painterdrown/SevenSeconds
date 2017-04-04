package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.MemoryActivity;
import com.goldfish.sevenseconds.activities.MessageActivity;
import com.goldfish.sevenseconds.activities.UserHomePageActivity;
import com.goldfish.sevenseconds.item.MyReviewItem;

import java.util.List;

/**
 * Created by lenovo on 2017/3/29.
 */

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {

    private List<MyReviewItem> myReviewItemList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_review_item, null);
        MyReviewAdapter.ViewHolder holder = new MyReviewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MyReviewItem myReviewItem = myReviewItemList.get(position);
        holder.name.setText(myReviewItem.getName());
        holder.face.setImageBitmap(myReviewItem.getFace());
        holder.time.setText(myReviewItem.getTime());
        if (myReviewItem.getIsMemory()) {
            holder.cover.setImageBitmap(myReviewItem.getCover());
            holder.cover.setVisibility(View.VISIBLE);
            holder.title.setText(myReviewItem.getTitle());
            holder.title.setVisibility(View.VISIBLE);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MessageActivity.messageActivity, MemoryActivity.class);
                    intent.putExtra("memoryID", myReviewItem.getMemoryId());
                    MessageActivity.messageActivity.startActivity(intent);
                }
            });
        }
        else {
            holder.myMessage.setText(myReviewItem.getMyMessage());
            holder.myMessage.setVisibility(View.VISIBLE);
        }
        holder.otherMessage.setText(myReviewItem.getOtherMessage());
        holder.face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.messageActivity, UserHomePageActivity.class);
                intent.putExtra("account", myReviewItem.getAccount());
                MessageActivity.messageActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myReviewItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView face;
        TextView name;
        TextView time;
        TextView otherMessage;
        TextView myMessage;
        ImageView cover;
        TextView title;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            face = (ImageView) itemView.findViewById(R.id.my_review_face);
            name = (TextView) itemView.findViewById(R.id.my_review_name);
            time = (TextView) itemView.findViewById(R.id.my_review_time);
            otherMessage = (TextView) itemView.findViewById(R.id.my_review_other);
            myMessage = (TextView) itemView.findViewById(R.id.my_review_my);
            cover = (ImageView) itemView.findViewById(R.id.my_review_cover);
            title = (TextView) itemView.findViewById(R.id.my_review_title);
            layout = (LinearLayout) itemView.findViewById(R.id.my_review_layout);
        }
    }

    public MyReviewAdapter(List<MyReviewItem> myReviewItemList) {
        this.myReviewItemList = myReviewItemList;
    }
}
