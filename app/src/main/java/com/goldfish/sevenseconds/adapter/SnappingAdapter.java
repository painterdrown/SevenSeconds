package com.goldfish.sevenseconds.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.fragment.SquareFragment;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by bytrain on 2017/3/30.
 */

public class SnappingAdapter extends RecyclerView.Adapter<SnappingAdapter.SimpleItemViewHolder>{
    // id in list
    private final List<String> memids;

    public SnappingAdapter(List<String> ids) {
        memids = ids;
    }
    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext ()).inflate(R.layout.item_snapping, parent, false );
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView ;

        public SimpleItemViewHolder (View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById (R. id.text);
        }
    }
}
