package com.airsoft.goodwin.UserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.airsoft.goodwin.R;

import java.util.LinkedHashMap;
import java.util.List;

public class UserInfoListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private LinkedHashMap<String, String> informationList;

    public UserInfoListAdapter(Context context, LinkedHashMap<String, String> informationList) {
        inflater = LayoutInflater.from(context);
        this.informationList = informationList;
    }

    @Override
    public int getCount() {
        return informationList.size();
    }

    @Override
    public Object getItem(int position) {
        return ((List)informationList.values()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        String key = (String)informationList.keySet().toArray()[position];
        String value = (String)informationList.values().toArray()[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.userinfo_additional_list_item, parent, false);
            holder.mainText = (TextView)convertView.findViewById(R.id.userinfo_additionallist_maintext);
            holder.secondaryText = (TextView)convertView.findViewById(R.id.userinfo_additionallist_additionaltext);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mainText.setText(key);
        holder.secondaryText.setText(value);

        return convertView;
    }

    private class ViewHolder {
        TextView mainText;
        TextView secondaryText;
    }
}
