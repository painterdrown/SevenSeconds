package com.goldfish.sevenseconds.fragment;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.activities.MessageActivity;
import com.goldfish.sevenseconds.adapter.ChattingAdapter;
import com.goldfish.sevenseconds.item.ChattingItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by guxiuzhong on 2015/7/6.
 */
public class MessageFragment extends Fragment {

    private List<ChattingItem> mChattingList = new ArrayList<ChattingItem>();
    private RecyclerView recyclerView;

    // 返回上一层的标签的信息
    public static Fragment getInstance(Bundle bundle) {
        MessageFragment fragment = new MessageFragment();
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
        View view = inflater.inflate(R.layout.fragmnet_message, null);
        initView();
        recyclerView = (RecyclerView) view.findViewById(R.id.myMessageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.messageActivity);
        recyclerView.setLayoutManager(layoutManager);
        ChattingAdapter adapter = new ChattingAdapter(mChattingList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initView() {
        Resources res = getResources();
        Bitmap bmp = ((BitmapDrawable) res.getDrawable(R.drawable.app_icon)).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        for (int i = 0; i < 5; i++) {
            ChattingItem chattingItem = new ChattingItem(os.toByteArray(),
                    "穿睡服的金鱼", "这周APP上线啦，今晚整合",
                    "2017-2-24 22:14", "noend22", 1);
            mChattingList.add(chattingItem);
        }
    }
}
