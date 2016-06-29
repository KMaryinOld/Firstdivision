package com.airsoft.goodwin.PrivateOffice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.airsoft.goodwin.R;

import java.util.Vector;

public class AdditionalInformationListAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<View> mElementViews;

    public AdditionalInformationListAdapter(Context context, Vector<AdditionalInformationListElement> elements) {
        mContext = context;
        mElementViews = new Vector<>();

        for (AdditionalInformationListElement e : elements) {
            addView(e);
        }
    }
    @Override
    public int getCount() {
        return mElementViews.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mElementViews.elementAt(position);
    }

    private void addView(AdditionalInformationListElement element) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView additionalInformationList = (ListView) ((PrivateOfficeActivity)mContext).findViewById(R.id.privateOfficeAdditionalInformationList);
        View view = inflater.inflate(R.layout.private_office_additional_information_list_row, additionalInformationList, false);

        TextView description = (TextView) view.findViewById(R.id.privateOfficeAdditionalInformationRowDescriptionText);
        TextView content = (TextView) view.findViewById(R.id.privateOfficeAdditionalInformationRowContentText);

        description.setText(element.description);
        content.setText(element.content);

        mElementViews.add(view);
    }
}
