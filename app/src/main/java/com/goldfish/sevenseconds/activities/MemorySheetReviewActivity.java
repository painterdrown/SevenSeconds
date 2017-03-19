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

public class MemorySheetReviewActivity extends AppCompatActivity {

    private List<AmemReviewItem> reviewItemList = new ArrayList<>();
    public static MemorySheetReviewActivity memorySheetReviewActivity;
    private RecyclerView recyclerView;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_amem_review);
        initReview();
        recyclerView = (RecyclerView) findViewById(R.id.amem_review_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MemorySheetReviewActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        AmemReviewAdapter adapter = new AmemReviewAdapter(reviewItemList);
        recyclerView.setAdapter(adapter);
    }

    private void initReview() {
        Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        for (int i = 0; i < 2; i++) {
            AmemReviewItem amemReviewItem = new AmemReviewItem(os.toByteArray(),
                    "穿睡服的金鱼", "这周APP上线啦，今晚整合",
                    "2017-2-24", "noend22", "100");
            reviewItemList.add(amemReviewItem);
        }
    }
}
