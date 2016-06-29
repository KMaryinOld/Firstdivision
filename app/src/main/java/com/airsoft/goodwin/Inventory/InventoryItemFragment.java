package com.airsoft.goodwin.Inventory;


import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.utils.Utils;

public class InventoryItemFragment extends DialogFragment {
    private InventoryThing currentItem;

    public InventoryItemFragment() {
        currentItem = null;
    }

    public static InventoryItemFragment newInstance() {
        return new InventoryItemFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_item, container, false);
        ImageView itemImage = (ImageView)view.findViewById(R.id.fragment_item_image);
        TextView itemName = (TextView)view.findViewById(R.id.fragment_item_name_text);
        TextView whoEquipText = (TextView)view.findViewById(R.id.fragment_item_who_equip_text);
        TextView equipDateText = (TextView) view.findViewById(R.id.fragment_item_equip_date_text);

        if (currentItem != null) {
            Bitmap image = ((InventoryActivity)getActivity()).getItemImage(currentItem.thingPhotoFilename);
            if (image == null) {
                image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cube_grey600_48dp);
            }
            itemImage.requestLayout();
            itemImage.setImageBitmap(image);

            itemName.setText(currentItem.thingName);
            UserInfo whoEquip = ((InventoryActivity)getActivity()).getUser(currentItem.equippedUserId);
            String equipText;
            if (whoEquip != null) {
                equipText = String.format("%s %s %s", whoEquip.lastname, whoEquip.firstname, whoEquip.middlename);
            } else {
                equipText = "Unknown";
            }
            whoEquipText.setText(equipText);

            equipDateText.setText(Utils.getDate(currentItem.timeIssued * 1000));
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.Toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(InventoryItemFragment.this).commit();
            }
        });

        return view;
    }

    public void setCurrentItem(InventoryThing item) {
        this.currentItem = item;
    }
}
