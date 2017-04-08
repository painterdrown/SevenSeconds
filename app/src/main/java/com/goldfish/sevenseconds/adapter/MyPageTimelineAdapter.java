package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.Addmem;
import com.goldfish.sevenseconds.activities.MemoryActivity;
import com.goldfish.sevenseconds.item.MyPageTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.view.TimelineView;

import java.util.List;

/**
 * Created by lenovo on 2017/4/3.
 */

public class MyPageTimelineAdapter extends RecyclerView.Adapter<MyPageTimelineAdapter.ViewHolder>{

    private List<MyPageTimelineItem> myPageTimelineItems;
    private Orientation orientation;
    private Context context;
    private ViewGroup viewGroup;

    public MyPageTimelineAdapter(List<MyPageTimelineItem> list, Orientation orientation) {
        this.myPageTimelineItems = list;
        this.orientation = orientation;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_page_timeline_item, null, false);
        viewGroup = parent;
        return new MyPageTimelineAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyPageTimelineItem myPageTimelineItem = myPageTimelineItems.get(position);
        holder.time.setText(myPageTimelineItem.getTime());
        holder.title.setText(myPageTimelineItem.getTitle());
        holder.cover.setImageBitmap(myPageTimelineItem.getCover());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (myPageTimelineItem.getMemoryId().equals("add memory"))
                {
                    intent = new Intent(viewGroup.getContext(), Addmem.class);
                }
                else {
                    intent = new Intent(viewGroup.getContext(), MemoryActivity.class);
                    intent.putExtra("memoryID", myPageTimelineItem.getMemoryId());
                }
                viewGroup.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (myPageTimelineItems != null ? myPageTimelineItems.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView time;
        public TextView title;
        public TimelineView myTimelineView;
        public ImageView cover;
        public CardView layout;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            myTimelineView = (TimelineView) itemView.findViewById(R.id.my_page_timeline);
            time = (TextView) itemView.findViewById(R.id.my_page_time);
            title = (TextView) itemView.findViewById(R.id.my_page_title);
            cover = (ImageView) itemView.findViewById(R.id.my_page_cover);
            layout = (CardView) itemView.findViewById(R.id.my_page_layout);
        }
    }
}
