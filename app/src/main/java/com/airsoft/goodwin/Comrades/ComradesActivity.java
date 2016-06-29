package com.airsoft.goodwin.Comrades;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.EnterActivity;
import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.UserInfo.UserInfoFragment;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ComradesActivity extends FirstdivisionAppCompatActivity {
    private View loadingLayout, usersListLayout, failureLoadingLayout;
    private FragmentManager fm;
    private UserInfoFragment userInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.applicationUserInfo.can("viewcomrades")) {
            Intent intent = new Intent(ComradesActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comrades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Downloading and setting users list
        loadingLayout = findViewById(R.id.comrades_loading_layout);
        usersListLayout = findViewById(R.id.comrades_users_list_layout);
        failureLoadingLayout = findViewById(R.id.comrades_failure_loading);

        fm = getFragmentManager();
        userInfoFragment = (UserInfoFragment) fm.findFragmentById(R.id.comrades_userinfo_fragment);
        fm.beginTransaction().hide(userInfoFragment).commit();

        initializeUsersList();
    }

    private void initializeUsersList() {
        usersListLayout.setVisibility(View.GONE);
        failureLoadingLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        FirstdivisionRestClient.getInstance().getComradesList(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingLayout.setVisibility(View.GONE);
                failureLoadingLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingLayout.setVisibility(View.GONE);
                usersListLayout.setVisibility(View.VISIBLE);

                ComradesListAdapter adapter = new ComradesListAdapter(ComradesActivity.this,
                        Utils.parseComradesList(responseString));
                final ListView listView = (ListView)usersListLayout.findViewById(R.id.comrades_users_list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserInfo user = (UserInfo) listView.getAdapter().getItem(position);
                        userInfoFragment.setUserInfo(user);
                        fm.beginTransaction()
                                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                                .show(userInfoFragment)
                                .commit();
                        /*Toast.makeText(ComradesActivity.this, String.format("%s %s %s", user.lastname,
                                user.firstname, user.middlename), Toast.LENGTH_SHORT).show();*/
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!userInfoFragment.isHidden()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                    .hide(userInfoFragment)
                    .commit();
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
            Intent intent = new Intent(ComradesActivity.this, PrivateOfficeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_duty) {
            Intent intent = new Intent(ComradesActivity.this, DutyActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_linen) {
            Intent intent = new Intent(ComradesActivity.this, LinenActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_comrades) {
            return false;
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(ComradesActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exit) {
            FirstdivisionRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(ComradesActivity.this, getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Intent intent = new Intent(ComradesActivity.this, EnterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void refreshData(View view) {
        initializeUsersList();
    }
}
