package com.airsoft.goodwin.Comrades;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.RoundedImageView;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComradesListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<UserInfo> mUsers;
    private Map<Integer, Bitmap> mUserAvatars;
    private Bitmap mEmptyUserPhoto;

    public ComradesListAdapter(Context context, List<UserInfo> users) {
        inflater = LayoutInflater.from(context);
        mUsers = users;
        mUserAvatars = new HashMap<>();
        mEmptyUserPhoto = BitmapFactory.decodeResource(inflater.getContext().getResources(),
                R.drawable.empty_user_photo);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        UserInfo user = mUsers.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.userinfo_list_item, parent, false);
            holder.mainText = (TextView)convertView.findViewById(R.id.userinfo_list_item_maintext);
            holder.secondaryText = (TextView)convertView.findViewById(R.id.userinfo_list_item_secondarytext);
            holder.image = (RoundedImageView) convertView.findViewById(R.id.userinfo_list_item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mainText.setText(String.format("%s %s", user.lastname, user.firstname));
        holder.secondaryText.setText(String.format("Дата регистрации: %s", user.dateregistration));
        if (mUserAvatars.containsKey(position)) {
            holder.image.setImageBitmap(mUserAvatars.get(position));
        } else {
            if (!user.photoPath.equals("-")) {
                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mUserAvatars.put(position, resource);
                        holder.image.setImageBitmap(resource);
                    }
                };
                Glide.with(inflater.getContext()).load(String.format("%s/%s", Settings.serverAddress, user.photoPath))
                        .asBitmap().into(target);
            } else {
                holder.image.setImageBitmap(mEmptyUserPhoto);
                mUserAvatars.put(position, mEmptyUserPhoto);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        RoundedImageView image;
        TextView mainText;
        TextView secondaryText;
    }
}
