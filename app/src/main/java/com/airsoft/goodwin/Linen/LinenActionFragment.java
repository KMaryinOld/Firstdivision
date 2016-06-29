package com.airsoft.goodwin.Linen;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class LinenActionFragment extends DialogFragment {
    private static final String ARG_LINEN_TYPES = "LINEN_TYPES";

    private Toolbar headerToolbar;
    private HashMap<Integer, String> linenTypes;
    private int actionType;

    private FloatingActionButton actionButton;

    public LinenActionFragment() {
        actionType = LinenHistoryRecord.LINEN_TYPE_RECEIPT;
        // Required empty public constructor
    }

    public static LinenActionFragment newInstance(HashMap<Integer, String> linenTypes) {
        LinenActionFragment fragment = new LinenActionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LINEN_TYPES, linenTypes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            linenTypes = (HashMap<Integer, String>)getArguments().getSerializable(ARG_LINEN_TYPES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_linen_action, container, false);

        LinearLayout actionContent = (LinearLayout) view.findViewById(R.id.linenActionContentLayout);

        headerToolbar = (Toolbar) view.findViewById(R.id.linenActionFragmentToolbar);
        headerToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        headerToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(LinenActionFragment.this).commit();
            }
        });

        setActionType(actionType);
        for (Map.Entry<Integer, String> entry : linenTypes.entrySet()) {
            EditText text = new EditText(getActivity());
            text.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filter = new InputFilter[1];
            filter[0] = new InputFilter.LengthFilter(4);
            text.setFilters(filter);
            text.setHint(entry.getValue());
            text.setTag(entry.getKey());

            actionContent.addView(text);
        }

        actionButton = (FloatingActionButton) view.findViewById(R.id.linenActionSubmitButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitLinenValues()) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
                    actionButton.startAnimation(anim);

                    view.findViewById(R.id.linenActionSubmittedProgress).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.linen_input_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean submitLinenValues() {
        final View view = getView();
        if (view == null) {
            return false;
        }

        Map<Integer, Integer> linenBalance = ((LinenActivity) getActivity()).getLinenBalance();

        LinearLayout actionContent = (LinearLayout) view.findViewById(R.id.linenActionContentLayout);
        final LinenHistoryRecord record = new LinenHistoryRecord();
        record.type = actionType;
        record.date = Calendar.getInstance();
        record.items = new HashMap<>();
        for (int i = 0; i < actionContent.getChildCount(); ++i) {
            TextView tv = (TextView) actionContent.getChildAt(i);
            String countText = tv.getText().toString();
            Integer count;
            if (countText.equals("")) {
                count = 0;
            } else {
                count = Integer.parseInt(countText);
            }
            Integer id = (Integer) tv.getTag();

            if (count > linenBalance.get(id)) {
                return false;
            }

            record.items.put(id, count);
        }

        FirstdivisionRestClient.getInstance().sendLinenHistoryRecord(record, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), getResources().getString(R.string.server_connection_error),
                        Toast.LENGTH_SHORT).show();
                view.findViewById(R.id.linenActionSubmittedProgress).setVisibility(View.INVISIBLE);
                actionButton.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (!Utils.isErrorResponse(responseString)) {
                    ((LinenActivity) getActivity()).addTableRecordRow(record);
                    getFragmentManager().beginTransaction().remove(LinenActionFragment.this).commit();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                    view.findViewById(R.id.linenActionSubmittedProgress).setVisibility(View.INVISIBLE);
                    actionButton.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open));
                }
            }
        });
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (actionButton != null) {
            actionButton.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;

        if (headerToolbar != null) {
            switch (actionType) {
                case LinenHistoryRecord.LINEN_TYPE_RECEIPT:
                    headerToolbar.setTitle("Добавление информации о выданном белье");
                    break;
                case LinenHistoryRecord.LINEN_TYPE_RETURN:
                    headerToolbar.setTitle("Добавление информации о сданном белье");
                    break;
                default:
                    headerToolbar.setTitle("Header information");
                    break;
            }
        }
    }
}
