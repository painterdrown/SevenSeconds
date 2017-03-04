package com.goldfish.sevenseconds.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.TimeLineModel;
import com.goldfish.sevenseconds.item.TimeLineViewHolder;
import com.goldfish.sevenseconds.item.Orientation;


import com.vipul.hp_hp.timelineview.TimelineView;

import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineSideBarAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;

    public TimeLineSideBarAdapter(List<TimeLineModel> feedList, Orientation orientation) {
        mFeedList = feedList;
        mOrientation = orientation;
    }

    //key
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_timelinesidebar, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);

        //holder.name.setText("name：" + timeLineModel.getName() + "    age：" + timeLineModel.getAge());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
