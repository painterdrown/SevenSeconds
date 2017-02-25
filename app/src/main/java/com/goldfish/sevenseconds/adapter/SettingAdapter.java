package com.goldfish.sevenseconds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;
import com.goldfish.sevenseconds.item.SettingItem;

import java.util.List;

/**
 * Created by lenovo on 2017/2/22.
 */

public class SettingAdapter extends ArrayAdapter<SettingItem> {

    private int resourceId;

    class ViewHolder {
        TextView settingString;
    }

    public SettingAdapter(Context context, int textViewResourceId,
                              List<SettingItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        SettingItem settingItem = getItem(position);  // 获取当前项的SettingItem比例
        SettingAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new SettingAdapter.ViewHolder();
            viewHolder.settingString = (TextView) view.findViewById(R.id.setting_string);
            view.setTag(viewHolder);  // 将ViewHolder储存在View中
        } else {
            view = convertView;
            viewHolder = (SettingAdapter.ViewHolder) view.getTag();  // 重新获取ViewHolder
        }

        viewHolder.settingString.setText(settingItem.getString());
        return view;
    }


}
