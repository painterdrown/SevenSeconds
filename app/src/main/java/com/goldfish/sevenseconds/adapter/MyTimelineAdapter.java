package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.MyTimelineItem;
import com.goldfish.sevenseconds.item.Orientation;
import com.goldfish.sevenseconds.item.TimeLineModel;
import com.goldfish.sevenseconds.item.TimeLineViewHolder;
import com.goldfish.sevenseconds.view.TimelineView;

import java.util.List;

/**
 * Created by lenovo on 2017/3/31.
 */

public class MyTimelineAdapter extends RecyclerView.Adapter<MyTimelineAdapter.ViewHolder> {

    private List<MyTimelineItem> myTimelineItems;
    private Orientation myOrientation;
    private Context myContext;

    public MyTimelineAdapter(List<MyTimelineItem> list, Orientation orientation) {
        myTimelineItems = list;
        myOrientation = orientation;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public MyTimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_timeline_item, null, false);
        return new MyTimelineAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MyTimelineAdapter.ViewHolder holder, int position) {
        MyTimelineItem timelineItem = myTimelineItems.get(position);
        holder.months.setText(timelineItem.getMonth());
    }

    @Override
    public int getItemCount() {
        return (myTimelineItems != null ? myTimelineItems.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView months;
        public TimelineView myTimelineView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            myTimelineView = (TimelineView) itemView.findViewById(R.id.amem_timeline);
            myTimelineView.initLine(viewType);
            months = (TextView) itemView.findViewById(R.id.my_timeline_month);
        }
    }
}
