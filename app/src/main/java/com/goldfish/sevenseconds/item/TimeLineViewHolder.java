package com.goldfish.sevenseconds.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vipul.hp_hp.timelineview.TimelineView;
import com.goldfish.sevenseconds.R;



/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}
