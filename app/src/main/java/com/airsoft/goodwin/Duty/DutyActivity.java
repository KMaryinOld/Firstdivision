package com.airsoft.goodwin.Duty;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.EnterActivity;
import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class DutyActivity extends FirstdivisionAppCompatActivity {
    public static final int UNDUTY = 0;
    public static final int COY_DUTY = 1;
    public static final int DIVISION_DUTY = 2;
    public static final int OXPAHA_DUTY = 3;

    public static final int DUTY_COLOR_RED = 0;
    public static final int DUTY_COLOR_YELLOW = 1;
    public static final int DUTY_COLOR_GREEN = 2;

    private TextView mDutyDateTextLabel;
    private DutyPadgerAdapter mPagerAdapter;
    private UndutyUsersFragment mUndutyFragment;
    private TabLayout mTabLayout;

    private Map<Integer, ArrayList<UserInfo>> mDutyUsers;
    private Calendar mPickedDate;

    private Map<Integer, List<DutyPosition>> mPositionTypes;
    private Map<Integer, DutyPosition> mPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.applicationUserInfo.can("dutyview")) {
            Intent intent = new Intent(DutyActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dutyToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.dutyEditFabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUndutyFragment.show(getFragmentManager(), "unduty_users");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //initialize tabs
        mTabLayout = (TabLayout) findViewById(R.id.dutyTypeTabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.coy)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.division)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.oxpaha)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.dutyTypeTabsVPager);
        mPagerAdapter = new DutyPadgerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mUndutyFragment = UndutyUsersFragment.newInstance();

        mDutyUsers = new HashMap<>();
        mPickedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        reinitializeDutyFragments();

        mDutyDateTextLabel = (TextView) findViewById(R.id.dutyDateTextLabel);
        mDutyDateTextLabel.setText(formatDutyExplanationText(mPickedDate));

        mDutyDateTextLabel.setText(formatDutyExplanationText(mPickedDate));
        mDutyDateTextLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(DutyActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mPickedDate.set(year, monthOfYear, dayOfMonth);
                        mDutyDateTextLabel.setText(formatDutyExplanationText(mPickedDate));
                        reinitializeDutyFragments();
                    }
                }, mPickedDate.get(Calendar.YEAR), mPickedDate.get(Calendar.MONTH),
                        mPickedDate.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_private_office) {
            Intent intent = new Intent(DutyActivity.this, PrivateOfficeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_duty) {
            return false;
        } else if (id == R.id.nav_linen) {
            Intent intent = new Intent(DutyActivity.this, LinenActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_comrades) {
            Intent intent = new Intent(DutyActivity.this, ComradesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(DutyActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exit) {
            FirstdivisionRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(DutyActivity.this, getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Intent intent = new Intent(DutyActivity.this, EnterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reinitializeDutyFragments() {
        final View pb = findViewById(R.id.dutyActivityMainContentProgressbar);
        final View contentLayout = findViewById(R.id.dutyActivityMainContentLayout);

        pb.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        String timestamp = Utils.getTimestampFromCalendar(mPickedDate);
        FirstdivisionRestClient.getInstance().getDutyUsers(timestamp, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(DutyActivity.this, "Произошла ошибка при загрузке данных. Повторите попытку позже",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mDutyUsers = Utils.parseDutyUsersInfo(responseString);
                mPositions = Utils.parseDutyPositions(responseString);
                mPositionTypes = new HashMap<>();
                for (Map.Entry<Integer, DutyPosition> entry : mPositions.entrySet()) {
                    if (!mPositionTypes.containsKey(entry.getValue().dutyId)) {
                        mPositionTypes.put(entry.getValue().dutyId, new ArrayList<DutyPosition>());
                    }
                    mPositionTypes.get(entry.getValue().dutyId).add(entry.getValue());
                }
                resetFragmentsData();

                pb.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void resetFragmentsData() {
        mUndutyFragment.setUsersList(mDutyUsers.get(UNDUTY));

        for (int i = 1; i <= 3; ++i) {
            CurrentDutyUsersTabFragment fragment = CurrentDutyUsersTabFragment.newInstance();
            fragment.setDutyUsers(mDutyUsers.get(i));

            mPagerAdapter.setCoyDutyTabFragment(fragment, i);
        }
        mPagerAdapter.notifyDataSetChanged();

        findViewById(R.id.dutyActivityMainContentProgressbar).setVisibility(View.GONE);
        findViewById(R.id.dutyActivityMainContentLayout).setVisibility(View.VISIBLE);
    }

    private String formatDutyExplanationText(Calendar cal) {
        Calendar date = (Calendar) cal.clone();

        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        date.add(Calendar.HOUR, 25);
        int tommorowDay = date.get(Calendar.DAY_OF_MONTH);
        int tommorowMonth = date.get(Calendar.MONTH);
        int tommorowYear = date.get(Calendar.YEAR);

        return String.format("Наряд \n с %d %s %d по %d %s %d",
                day, Utils.getMonth(month), year, tommorowDay, Utils.getMonth(tommorowMonth), tommorowYear);
    }

    private String getDutyNameByType(int type) {
        switch(type) {
            case DutyActivity.COY_DUTY:
                return getString(R.string.coy);
            case DutyActivity.DIVISION_DUTY:
                return getString(R.string.division);
            case DutyActivity.OXPAHA_DUTY:
                return getString(R.string.oxpaha);
        }
        return "";
    }

    public ArrayList<UserInfo> getUndutyUsers() {
        return mDutyUsers.get(UNDUTY);
    }

    public Calendar getPickedDate() {
        return mPickedDate;
    }

    public int getPickedDutyType() {
        return mTabLayout.getSelectedTabPosition() + 1;
    }

    public void addToDutyUser(UserInfo user, int dutyType) {
        mDutyUsers.get(dutyType).add(user);
    }

    public void removeDutyUserFromList(int dutyType, int position) {
        mDutyUsers.get(dutyType).remove(position);
    }

    public Map<Integer, DutyPosition> getDutyPositions() {
        return mPositions;
    }

    public Map<Integer, List<DutyPosition>> getDutyPositionTypes() {
        return mPositionTypes;
    }
}
