package com.goldfish.sevenseconds.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.goldfish.sevenseconds.item.ChattingItem;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.MessageActivity;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by lenovo on 2017/2/23.
 */

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {
    private List<ChattingItem> mChattingList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView face;
        TextView name;
        TextView message;
        TextView time;
        RelativeLayout layout;
        ImageView readOrNot;
        View chatview;

        public ViewHolder (View view) {
            super(view);
            chatview = view;
            face = (ImageView) view.findViewById(R.id.dialogue_image);
            name = (TextView) view.findViewById(R.id.dialogue_name);
            message = (TextView) view.findViewById(R.id.dialogue_message);
            time = (TextView) view.findViewById(R.id.dialogue_time);
            layout = (RelativeLayout) view.findViewById(R.id.dialogue_layout);
            readOrNot = (ImageView) view.findViewById(R.id.dialogue_read);
        }
    }

    public ChattingAdapter(List<ChattingItem> chattingList) {
        mChattingList = chattingList;
    }

    @Override
    public ChattingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatting_item, null);
        ChattingAdapter.ViewHolder holder = new ChattingAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChattingAdapter.ViewHolder holder, int position) {
        ChattingItem chattingItem = mChattingList.get(position);
        holder.name.setText(chattingItem.getName());
        holder.readOrNot.setImageResource(R.drawable.red_background);
        holder.message.setText(chattingItem.getMessage());
        holder.time.setText(chattingItem.getTime());
        holder.layout.setTag(chattingItem.getAccount());
        holder.face.setImageDrawable(
                Drawable.createFromStream(
                        new ByteArrayInputStream(chattingItem.getImage()), "FACE"));
        holder.chatview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MessageActivity.messageActivity,"i am a pig",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChattingList.size();
    }
}
