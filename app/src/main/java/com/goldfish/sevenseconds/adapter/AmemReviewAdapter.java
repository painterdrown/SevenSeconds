package com.goldfish.sevenseconds.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.AmemReviewItem;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by lenovo on 2017/3/1.
 */

public class AmemReviewAdapter extends RecyclerView.Adapter<AmemReviewAdapter.ViewHolder> {

    private List<AmemReviewItem> mAmemReviewList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView amem_face;
        TextView amem_name;
        TextView amem_time;
        TextView amem_message;
        RelativeLayout amem_layout;
        View amem_reviewView;
        TextView amem_like;

        public ViewHolder(View itemView) {
            super(itemView);
            amem_reviewView = itemView;
            amem_face = (ImageView) itemView.findViewById(R.id.amem_review_face);
            amem_name = (TextView) itemView.findViewById(R.id.amem_review_name);
            amem_message = (TextView) itemView.findViewById(R.id.amem_message);
            amem_time = (TextView) itemView.findViewById(R.id.amem_review_time);
            amem_layout = (RelativeLayout) itemView.findViewById(R.id.amem_review);
            //amem_like = (TextView) itemView.findViewById(R.id.amem_review_like_number);
        }
    }

    public AmemReviewAdapter(List<AmemReviewItem> AmemReviewList) { mAmemReviewList = AmemReviewList; }

    @Override
    public AmemReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.amem_review_item, null);
        AmemReviewAdapter.ViewHolder holder = new AmemReviewAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(AmemReviewAdapter.ViewHolder holder, int position) {
        AmemReviewItem amemReviewItem = mAmemReviewList.get(position);
        holder.amem_name.setText(amemReviewItem.getName());
        /*holder.amem_face.setImageDrawable(
                Drawable.createFromStream(
                        new ByteArrayInputStream(amemReviewItem.getImage()), "Face"));*/
        holder.amem_face.setImageBitmap(amemReviewItem.getImage());
        holder.amem_time.setText(amemReviewItem.getTime());
        holder.amem_message.setText(amemReviewItem.getMessage());
        holder.amem_layout.setTag(amemReviewItem.getAccount());
        holder.amem_reviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.amem_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
        //holder.amem_like.setText(amemReviewItem.getLike());
    }

    @Override
    public int getItemCount() {
        return mAmemReviewList.size();
    }


}
