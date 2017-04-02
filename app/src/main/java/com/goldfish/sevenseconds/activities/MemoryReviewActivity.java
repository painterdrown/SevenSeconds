package com.goldfish.sevenseconds.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.adapter.AmemReviewAdapter;
import com.goldfish.sevenseconds.item.AmemReviewItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/3/5.
 */

public class MemoryReviewActivity extends AppCompatActivity {

    private List<AmemReviewItem> reviewItemList = new ArrayList<>();
    public static MemoryReviewActivity memoryReviewActivity;
    private RecyclerView recyclerView;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_amem_review);
        initReview();
        recyclerView = (RecyclerView) findViewById(R.id.amem_review_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MemoryReviewActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        AmemReviewAdapter adapter = new AmemReviewAdapter(reviewItemList);
        recyclerView.setAdapter(adapter);
    }

    private void initReview() {
    }
}
