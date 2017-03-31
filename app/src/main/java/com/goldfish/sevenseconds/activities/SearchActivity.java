package com.goldfish.sevenseconds.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.MyReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bytrain on 2017/3/31.
 */

public class SearchActivity extends Activity{
    String query;
    private List<String> SearchItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle a){
        super.onCreate(a);
        Intent getData = getIntent();
        query = getData.getStringExtra("querydata");
        setContentView(R.layout.activity_search);
        ImageView back = (ImageView) findViewById(R.id.my_review_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_list);

    }
}
