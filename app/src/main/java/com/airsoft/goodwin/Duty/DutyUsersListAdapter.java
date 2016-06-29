package com.airsoft.goodwin.Duty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.utils.RoundedImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DutyUsersListAdapter extends BaseAdapter {
    public static final int FLIPPER_VIEW_USERINFO = 0;
    public static final int FLIPPER_VIEW_LOADING = 1;
    public static final int FLIPPER_VIEW_SUCCESS = 2;
    public static final int FLIPPER_VIEW_ERROR = 3;

    protected LayoutInflater inflater;
    protected List<UserInfo> mUsers;
    protected Map<Integer, Bitmap> mUserAvatars;
    protected Map<Integer, Integer> mUserFlipperState;
    protected Bitmap mEmptyUserPhoto;

    protected Map<Integer, DutyPosition> mPositions;

    public DutyUsersListAdapter(Context context, List<UserInfo> users) {
        inflater = LayoutInflater.from(context);
        mUsers = users;
        mUserAvatars = new HashMap<>();
        mUserFlipperState = new HashMap<>();
        mPositions = ((DutyActivity) context).getDutyPositions();

        mEmptyUserPhoto = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.empty_user_photo);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public UserInfo getItem(int position) {
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
            convertView = inflater.inflate(R.layout.duty_users_listadapter_row_item, parent, false);
            holder.mainText = (TextView) convertView.findViewById(R.id.dutyUsersListadapterMainTextview);
            holder.additionalText = (TextView) convertView.findViewById(R.id.dutyUsersListadapterAdditionalTextview);
            holder.image = (RoundedImageView) convertView.findViewById(R.id.dutyUsersListadapterImage);
            holder.flipper = (ViewFlipper) convertView.findViewById(R.id.duty_users_listadapter_flipper);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mainText.setText(mPositions.get(user.dutyPosition).name);
        holder.additionalText.setText(String.format("%s %s %s", user.lastname, user.firstname, user.middlename));
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

        return convertView;
    }

    protected class ViewHolder{
        public RoundedImageView image;
        public TextView mainText;
        public TextView additionalText;
        public ViewFlipper flipper;
        public View contentLayout;
    }

    public void toggleAdapterView(int position, View view, int flipperType) {
        ViewHolder holder = (ViewHolder) view.getTag();
        mUserFlipperState.put(position, flipperType);

        holder.flipper.setInAnimation(inflater.getContext(), android.R.anim.slide_in_left);
        holder.flipper.setOutAnimation(inflater.getContext(), android.R.anim.slide_out_right);
        holder.flipper.setDisplayedChild(flipperType);

        holder.flipper.setFlipInterval(0);
        holder.flipper.setInAnimation(null);
        holder.flipper.setOutAnimation(null);
    }

    public void addItem(UserInfo userInfo){
        mUsers.add(userInfo);

        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mUsers.remove(position);

        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
