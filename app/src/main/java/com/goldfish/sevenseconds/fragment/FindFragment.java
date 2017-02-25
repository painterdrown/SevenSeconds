package com.goldfish.sevenseconds.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldfish.sevenseconds.R;

/**
 * Created by zzz87 on 2017/2/23.
 */

public class FindFragment extends Fragment {
    private String name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svaedInstanceState){
        View view = inflater.inflate(R.layout.fragment_find,container,false);
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    public static FindFragment newInstance(String libargument)
    {
        Bundle bundle = new Bundle();
        FindFragment mm = new FindFragment();
        return mm;
    }
}
