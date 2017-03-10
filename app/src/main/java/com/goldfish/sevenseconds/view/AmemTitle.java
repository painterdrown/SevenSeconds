package com.goldfish.sevenseconds.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;

/**
 * Created by lenovo on 2017/3/4.
 */

public class AmemTitle extends LinearLayout {

    public AmemTitle (Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.amem_title_bar, this);
    }

}
