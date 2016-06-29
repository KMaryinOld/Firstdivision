package com.airsoft.goodwin.UserInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.airsoft.goodwin.Inventory.InventoryActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.ImageryView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserInfoFragment extends Fragment {
    private ImageryView imageryHead;
    private ListView contentList;
    private Button inventoryButton;

    private UserInfo userInfo;
    public UserInfoFragment() {
        userInfo = new UserInfo();
    }

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        imageryHead = (ImageryView)view.findViewById(R.id.user_info_header_imagery);
        imageryHead.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty_user_photo));
        imageryHead.setMainText("-");
        imageryHead.setSecondaryText("-");
        contentList = (ListView) view.findViewById(R.id.user_info_information_list);

        inventoryButton = (Button) view.findViewById(R.id.user_info_inventory_button);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;

        reinitializeFragmentInformation();
    }

    private void reinitializeFragmentInformation() {
        imageryHead.setMainText(userInfo.lastname);
        imageryHead.setSecondaryText(String.format("%s %s", userInfo.firstname, userInfo.middlename));
        imageryHead.invalidate();
        if (userInfo.photoPath.equals("-")) {
            imageryHead.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty_user_photo));
        } else {
            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageryHead.setImageBitmap(resource);
                }
            };
            Glide.with(getActivity()).load(String.format("%s/%s", Settings.serverAddress, userInfo.photoPath))
                    .asBitmap().into(target);
        }

        LinkedHashMap<String, String> informationList = new LinkedHashMap<>();
        informationList.put("Звание", userInfo.rank);
        informationList.put("Прозвище", userInfo.nickname);
        informationList.put("Дата рождения", userInfo.birth);
        informationList.put("Национальность", userInfo.nation);
        informationList.put("Подразделение", userInfo.position);
        informationList.put("Батарея", userInfo.battery);
        informationList.put("Телефонный номер", userInfo.telnum);
        informationList.put("Дата регистрации", userInfo.dateregistration);

        UserInfoListAdapter adapter = new UserInfoListAdapter(getActivity(), informationList);
        contentList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(contentList);

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InventoryActivity.class);
                intent.putExtra("userid", userInfo.id);
                startActivity(intent);
            }
        });
    }

    private class InformationTypes {
        public static final int USER_INFO_GENERAL_INFORMATION = 0;
        public static final int USER_INFO_ADDITIONAL_INFORMATION = 1;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public ImageryView getImageryHead() {
        return imageryHead;
    }
}
