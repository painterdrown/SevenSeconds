package com.goldfish.sevenseconds.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;

public class MemorySheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amem);

        TextView review = (TextView) findViewById(R.id.amem_review_text);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemorySheetActivity.this, MemorySheetOthersActivity.class);
                startActivity(intent);
            }
        });
    }
}
