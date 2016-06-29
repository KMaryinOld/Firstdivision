package com.airsoft.goodwin.Staff;

import android.content.Context;
import android.graphics.Bitmap;
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

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class StaffListAdapter extends BaseAdapter implements StickyListHeadersAdapter{
    private LayoutInflater inflater;
    private ArrayList<StaffDepartmentUser> departmentUsers;
    private Context mContext;

    public StaffListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mContext = context;

        departmentUsers = new ArrayList<>();
        UserInfo emptyUser = new UserInfo();
        StaffDepartment department = new StaffDepartment(1, "Штаб", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

        department = new StaffDepartment(6l, "Отделение технического обеспечения", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

        department = new StaffDepartment(7l, "Радио-техничская батарея", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

        department = new StaffDepartment(8l, "Стартовая батарея", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

        department = new StaffDepartment(9l, "Отделение обеспечения", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

        department = new StaffDepartment(10l, "Энерго-механическое отделение", "Управление подразделения");
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));
        departmentUsers.add(new StaffDepartmentUser(department, emptyUser));

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.staff_list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.staff_list_header_text);

            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = departmentUsers.get(position).getDepartment().getName();
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return departmentUsers.get(position).getDepartment().getId();
    }

    @Override
    public int getCount() {
        return departmentUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return departmentUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        UserInfo user = departmentUsers.get(position).getDepartmentUser();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.userinfo_list_item, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.userinfo_list_item_maintext);
            holder.image = (RoundedImageView) convertView.findViewById(R.id.userinfo_list_item_image);
            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.image.setImageBitmap(resource);
                }
            };
            Glide.with(mContext).load(String.format("%s/%s", Settings.serverAddress, user.photoPath))
                    .asBitmap().into( target );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.text.setText(String.format("%s %s %s", user.lastname, user.firstname, user.middlename));

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        RoundedImageView image;
    }

    static class HeaderViewHolder {
        TextView text;
    }
}
