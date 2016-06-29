package com.airsoft.goodwin.Duty;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.RoundedImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

public class UndutyUsersListAdapter extends DutyUsersListAdapter {
    public UndutyUsersListAdapter(Context context, List<UserInfo> users) {
        super(context, users);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        UserInfo user = mUsers.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.duty_users_listadapter_row_item, parent, false);
            holder.mainText = (TextView) convertView.findViewById(R.id.dutyUsersListadapterMainTextview);
            holder.additionalText = (TextView) convertView.findViewById(R.id.dutyUsersListadapterAdditionalTextview);
            holder.image = (RoundedImageView) convertView.findViewById(R.id.dutyUsersListadapterImage);
            holder.flipper = (ViewFlipper) convertView.findViewById(R.id.duty_users_listadapter_flipper);
            holder.contentLayout = convertView.findViewById(R.id.duty_users_listadapter_content_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mainText.setText(user.lastname);
        holder.additionalText.setText(String.format("%s %s", user.firstname, user.middlename));
        if (mUserFlipperState.containsKey(position)) {
            holder.flipper.setDisplayedChild(mUserFlipperState.get(position));
        } else {
            holder.flipper.setDisplayedChild(FLIPPER_VIEW_USERINFO);
        }
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

        switch (user.dutyColor) {
            case DutyActivity.DUTY_COLOR_RED:
                holder.contentLayout.setBackgroundColor(ContextCompat.getColor(inflater.getContext(),
                        R.color.duty_red_background));
                break;
            case DutyActivity.DUTY_COLOR_YELLOW:
                holder.contentLayout.setBackgroundColor(ContextCompat.getColor(inflater.getContext(),
                        R.color.duty_yellow_background));
                break;
            case DutyActivity.DUTY_COLOR_GREEN:
                holder.contentLayout.setBackgroundColor(ContextCompat.getColor(inflater.getContext(),
                        R.color.duty_green_background));
                break;
        }

        return convertView;
    }
}
