package com.airsoft.goodwin.Duty;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UndutyUsersFragment extends DialogFragment {
    private View mView;
    private DutyUsersListAdapter mUsersListAdapter;
    private ListView mUndutyUsersList;
    private List<UserInfo> mUsersList;

    private List<Integer> removedUsers;

    public UndutyUsersFragment() {
        // Required empty public constructor
    }

    public static UndutyUsersFragment newInstance() {
        return new UndutyUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mUsersList == null) {
            mUsersList = new ArrayList<>();
        }

        removedUsers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_unduty_users, container, false);
        mUndutyUsersList = (ListView) mView.findViewById(R.id.undutyUsersListView);

        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.dutyUndutyFragmentToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle(getString(R.string.unduty_free_users));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(UndutyUsersFragment.this).commit();
            }
        });

        final DutyActivity activity = (DutyActivity) getActivity();
        final ArrayList<UserInfo> undutyUsers = activity.getUndutyUsers();
        final Spinner spinner = (Spinner)mView.findViewById(R.id.unduty_duty_position);
        spinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                activity.getDutyPositionTypes().get(activity.getPickedDutyType())));

        if (undutyUsers.size() == 0) {
            mView.findViewById(R.id.undutyUsersListView).setVisibility(View.GONE);
            mView.findViewById(R.id.undutyEmptyListHelpLabel).setVisibility(View.VISIBLE);
            return mView;
        }
        mView.findViewById(R.id.undutyUsersListView).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.undutyEmptyListHelpLabel).setVisibility(View.GONE);
        mUndutyUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                mUsersListAdapter.toggleAdapterView(position, view, DutyUsersListAdapter.FLIPPER_VIEW_LOADING);

                final UserInfo selectedUser = undutyUsers.get(position);
                selectedUser.dutyPosition = ((DutyPosition)spinner.getSelectedItem()).id;

                String timestamp = Utils.getTimestampFromCalendar(activity.getPickedDate());
                final int pickedDutyType = activity.getPickedDutyType();

                FirstdivisionRestClient.getInstance().setDutyUser(selectedUser.id, timestamp, pickedDutyType,
                        selectedUser.dutyPosition, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            mUsersListAdapter.toggleAdapterView(position, view, DutyUsersListAdapter.FLIPPER_VIEW_ERROR);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            removedUsers.add(position);
                            //activity.removeDutyUserFromList(DutyActivity.UNDUTY, position);
                            activity.addToDutyUser(selectedUser, activity.getPickedDutyType());
                            activity.resetFragmentsData();

                            mUsersListAdapter.toggleAdapterView(position, view, DutyUsersListAdapter.FLIPPER_VIEW_SUCCESS);
                        }
                    });
            }
        });
        mUsersListAdapter = new UndutyUsersListAdapter(getActivity(), mUsersList);
        mUndutyUsersList.setAdapter(mUsersListAdapter);
        final ViewTreeObserver vto = mView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // Key area to perform optimization. Fix the dialog size!
                // During ListView's rows animation, dialog will never need to
                // perform computation again.
                int width = getDialog().getWindow().getDecorView().getWidth();
                int height = getDialog().getWindow().getDecorView().getHeight();
                getDialog().getWindow().setLayout(width, height);

                ViewTreeObserver obs = mView.getViewTreeObserver();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mUsersListAdapter == null) {
            mUsersListAdapter = new DutyUsersListAdapter(context, mUsersList);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onStop();
        DutyActivity activity = (DutyActivity)getActivity();
        Collections.sort(removedUsers, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs.equals(rhs) ? 0 : lhs > rhs ? -1 : 1;
            }
        });
        for (Integer position : removedUsers) {
            activity.removeDutyUserFromList(DutyActivity.UNDUTY, position);
        }
        removedUsers.clear();
    }

    public void addUserToList(UserInfo userInfo) {
        if (mUsersListAdapter != null) {
            mUsersListAdapter.addItem(userInfo);
            mUsersListAdapter.notifyDataSetChanged();
        }
    }

    public void setUsersList(List<UserInfo> usersList) {
        mUsersList = usersList;
    }
}
