package com.airsoft.goodwin.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.RoundedImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MessagesListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Map<Integer, Message> messages;
    private List<Message> unsentMessages;
    private Map<Integer, Bitmap> mUserAvatars;
    private Bitmap mEmptyUserPhoto;

    public MessagesListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        messages = new TreeMap<>();
        unsentMessages = new ArrayList<>();
        mUserAvatars = new HashMap<>();

        mEmptyUserPhoto = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.empty_user_photo);
    }

    @Override
    public int getCount() {
        return messages.size() + unsentMessages.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > messages.size()) {
            return unsentMessages.get(position - messages.size());
        }
        return messages.values().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        Message message;
        if (position >= messages.size()) {
            message = unsentMessages.get(position - messages.size());
        } else {
            message = (Message)messages.values().toArray()[position];
        }
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_message_layout, parent, false);
            holder.image = (RoundedImageView) convertView.findViewById(R.id.chat_message_user_photo);
            holder.userNameText = (TextView) convertView.findViewById(R.id.chat_message_username);
            holder.messageText = (TextView)convertView.findViewById(R.id.chat_message_text);

            holder.messageLayout = (LinearLayout)convertView.findViewById(R.id.chat_message_layout);
            holder.messageContentLayout = convertView.findViewById(R.id.chat_message_message_layout);
            holder.paddingLayout = convertView.findViewById(R.id.chat_message_padding_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageLayout.removeAllViews();
        if (message.userId == Integer.parseInt(Settings.applicationUserInfo.id) || message.userId == -1) {
            holder.messageLayout.addView(holder.paddingLayout);
            holder.messageLayout.addView(holder.messageContentLayout);
            holder.messageContentLayout.setBackgroundResource(R.drawable.chat_own_message_layout);
        } else {
            holder.messageLayout.addView(holder.messageContentLayout);
            holder.messageLayout.addView(holder.paddingLayout);
            holder.messageContentLayout.setBackgroundResource(R.drawable.chat_another_message_layout);
        }

        holder.userNameText.setText(String.format("%s %c.", message.lastname, message.firstname.charAt(0)));
        holder.messageText.setText(message.text);

        if (mUserAvatars.containsKey(position)) {
            holder.image.setImageBitmap(mUserAvatars.get(position));
        } else {
            if (!message.photo.equals("-")) {
                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mUserAvatars.put(position, resource);
                        holder.image.setImageBitmap(resource);
                    }
                };
                Glide.with(inflater.getContext()).load(String.format("%s/%s", Settings.serverAddress, message.photo))
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
        TextView userNameText;
        TextView messageText;

        LinearLayout messageLayout;
        View paddingLayout;
        View messageContentLayout;
    }

    public void addMessages(List<Message> messages) {
        boolean addedNewMessages = false;

        for (Message message : messages) {
            if (!this.messages.containsKey(message.id)) {
                this.messages.put(message.id, message);
                addedNewMessages = true;
            }
        }

        if (addedNewMessages) {
            notifyDataSetChanged();
        }
    }

    public void addUnsentMessage(Message message) {
        unsentMessages.add(message);
        notifyDataSetChanged();
    }

    public void clearUnsentMessages() {
        unsentMessages.clear();
        notifyDataSetChanged();
    }
}
