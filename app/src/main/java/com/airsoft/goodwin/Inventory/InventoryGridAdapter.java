package com.airsoft.goodwin.Inventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.airsoft.goodwin.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryGridAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private InventoryActivity mParentActivity;
    private List<InventoryThing> mItems;
    private Bitmap mEmptyItemImage;

    public InventoryGridAdapter(Context context) {
        mParentActivity = (InventoryActivity) context;
        mInflater = LayoutInflater.from(context);
        mItems = new ArrayList<>();

        mEmptyItemImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_cube_grey600_48dp);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        InventoryThing item = mItems.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.inventory_cell_layout, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.inventory_cell_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bitmap image = mParentActivity.getItemImage(item.thingPhotoFilename);
        if (image != null) {
            holder.image.setImageBitmap(image);
        } else {
            holder.image.setImageBitmap(mEmptyItemImage);
        }

        return convertView;
    }

    public void addAllThings(List<InventoryThing> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addThing(InventoryThing item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public ImageView image;
    }
}
