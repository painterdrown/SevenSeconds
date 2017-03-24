package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.goldfish.sevenseconds.tools.DensityUtil;
import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.BarActivity;
import com.goldfish.sevenseconds.activities.MemoryActivity;
import com.goldfish.sevenseconds.item.MemorySheetPreview;

import java.util.List;

public class MemAdapter extends BaseRecyclerAdapter<MemAdapter.memViewHolder> {
    private List<MemorySheetPreview> list;
    private int largeCardHeight, smallCardHeight;

    public static class memViewHolder extends RecyclerView.ViewHolder {

        public View memView;
        public TextView name;
        public TextView contents;
        public ImageView userhead;
        public int position;

        public memViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                this.name = (TextView) itemView
                        .findViewById(R.id.name);
                this.contents = (TextView) itemView
                        .findViewById(R.id.contents);
                this.userhead = (ImageView) itemView.findViewById(R.id.user_head);
                this.memView = itemView.findViewById(R.id.card_view);
            }
        }
    }

    public MemorySheetPreview getItem(int position) {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }


    public MemAdapter(List<MemorySheetPreview> list, Context context) {
        this.list = list;
        largeCardHeight = DensityUtil.dip2px(context, 150);
        smallCardHeight = DensityUtil.dip2px(context, 100);
    }

    @Override
    public void onBindViewHolder(final memViewHolder holder, final int position, boolean isItem) {
        MemorySheetPreview now = list.get(position);
        holder.name.setText(now.getName());
        holder.contents.setText(now.getContents());
        holder.userhead.setImageResource(now.getImageid());
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            holder.memView.getLayoutParams().height = position % 2 != 0 ? largeCardHeight : smallCardHeight;
        }
        holder.memView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemorySheetPreview now = list.get(position);
                Intent intent = new Intent(BarActivity.barActivity,MemoryActivity.class);
                intent.putExtra("images",now.getImageid());
                intent.putExtra("name",now.getName());
                intent.putExtra("contents",now.getContents());
                intent.putExtra("account", now.getAccount());
                intent.putExtra("memoryID", now.getMemoryID());
                BarActivity.barActivity.startActivity(intent);
            }
        });
    }

    @Override
    public  memViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.square_memory_item, parent, false);
        memViewHolder vh = new memViewHolder(v, true);
        return vh;
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    @Override
    public memViewHolder getViewHolder(View view) {
        return new memViewHolder(view, false);
    }

    public void setData(List<MemorySheetPreview> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void insert(MemorySheetPreview now, int position) {
        insert(list, now, position);
    }

    public void remove(int position) {
        remove(list, position);
    }

    public void clear() {
        clear(list);
    }

}
