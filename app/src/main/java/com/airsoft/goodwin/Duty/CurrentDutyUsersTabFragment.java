package com.airsoft.goodwin.Duty;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CurrentDutyUsersTabFragment extends Fragment {
    private final static int MENU_ITEM_PERSONAL_OFFICE = 0;
    private final static int MENU_ITEM_DELETE_FROM_LIST = 1;

    private ArrayList<UserInfo> mDutyUsers;
    private View mFragmentView;

    private int selectedUserContextMenu;

    public CurrentDutyUsersTabFragment() {
        // Required empty public constructor
    }

    public static CurrentDutyUsersTabFragment newInstance() {
        return new CurrentDutyUsersTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mDutyUsers == null) {
            mDutyUsers = new ArrayList<>();
        }

        selectedUserContextMenu = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_current_duty_users_tab, container, false);
        updateFragmentContent();
        return mFragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setDutyUsers(ArrayList<UserInfo> dutyUsers) {
        mDutyUsers = dutyUsers;
        updateFragmentContent();
    }

    private void updateFragmentContent() {
        if (mFragmentView != null) {
            ListView usersList = (ListView) mFragmentView.findViewById(R.id.dutyCurrentBusyUsers);
            if (mDutyUsers.size() == 0) {
                usersList.setVisibility(View.GONE);
                mFragmentView.findViewById(R.id.dutyUsersTabEmptyListText).setVisibility(View.VISIBLE);
            } else {
                usersList.setVisibility(View.VISIBLE);
                mFragmentView.findViewById(R.id.dutyUsersTabEmptyListText).setVisibility(View.GONE);
                usersList.setAdapter(new DutyUsersListAdapter(getContext(), mDutyUsers));

                registerForContextMenu(usersList);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedUserContextMenu = acmi.position;

        menu.add(0, MENU_ITEM_PERSONAL_OFFICE, 0, getString(R.string.user_personal_office));
        menu.add(0, MENU_ITEM_DELETE_FROM_LIST, 0, getString(R.string.delete_from_duty));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedUserContextMenu != -1) {
            switch (item.getItemId()) {
                case MENU_ITEM_PERSONAL_OFFICE:
                    break;
                case MENU_ITEM_DELETE_FROM_LIST:
                    final int currentSelectedUser = selectedUserContextMenu;
                    final DutyActivity activity = (DutyActivity) getActivity();
                    final UserInfo user = mDutyUsers.get(currentSelectedUser);
                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentLayout).setVisibility(View.GONE);
                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentProgressBar).setVisibility(View.VISIBLE);
                    FirstdivisionRestClient.getInstance().removeDutyUser(user.id, Utils.getTimestampFromCalendar(activity.getPickedDate()),
                            new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentLayout).setVisibility(View.VISIBLE);
                                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentProgressBar).setVisibility(View.GONE);

                                    Toast.makeText(getContext(), getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    activity.addToDutyUser(user, DutyActivity.UNDUTY);
                                    activity.removeDutyUserFromList(activity.getPickedDutyType(), currentSelectedUser);
                                    activity.resetFragmentsData();

                                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentLayout).setVisibility(View.VISIBLE);
                                    mFragmentView.findViewById(R.id.currentDutyUsersTabContentProgressBar).setVisibility(View.GONE);
                                }
                            });
                    break;
            }
        }

        selectedUserContextMenu = -1;
        return super.onContextItemSelected(item);
    }
}
