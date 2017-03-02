package com.goldfish.sevenseconds.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.MemorySheetActivity;
import com.goldfish.sevenseconds.activities.MemorySheetOthersActivity;
import com.goldfish.sevenseconds.adapter.AmemReviewAdapter;
import com.goldfish.sevenseconds.item.AmemReviewItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/3/1.
 */

public class AmemReviewFragment extends Fragment {

    private List<AmemReviewItem> mAmemReviewList = new ArrayList<AmemReviewItem>();
    private RecyclerView recyclerView;

    public static Fragment getInstance(Bundle bundle) {
        AmemReviewFragment fragment = new AmemReviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        //if (getArguments().getString("title").equals("评论")) {
            view = inflater.inflate(R.layout.fragment_amem_review, null);
            initReviewView();
            recyclerView = (RecyclerView) view.findViewById(R.id.amem_review_layout);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MemorySheetOthersActivity.memorySheetOthersActivity);
            recyclerView.setLayoutManager(layoutManager);
            AmemReviewAdapter adapter = new AmemReviewAdapter(mAmemReviewList);
            recyclerView.setAdapter(adapter);
        //}
        return view;
    }

    private void initReviewView() {
        Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        for (int i = 0; i < 2; i++) {
            AmemReviewItem amemReviewItem = new AmemReviewItem(os.toByteArray(),
                    "穿睡服的金鱼", "这周APP上线啦，今晚整合",
                    "2017-2-24", "noend22", "100");
            mAmemReviewList.add(amemReviewItem);
        }
    }
}
