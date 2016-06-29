package com.airsoft.goodwin.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;

import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.UserInfo.UserInfoListAdapter;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DialogUsersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView mDialogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.applicationUserInfo.can("chatvisiter")) {
            Intent intent = new Intent(DialogUsersActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDialogList = (ListView) findViewById(R.id.dialog_users_list);

        reloadUsersList();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dialog_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reloadUsersList() {
        final View usersListLayout = findViewById(R.id.dialog_users_list_layout);
        final View loadingLayout = findViewById(R.id.dialog_users_loading_layout);
        final View errorLayout = findViewById(R.id.dialog_users_error_layout);

        usersListLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        FirstdivisionRestClient.getInstance().getDialogUsersList(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (Utils.isErrorResponse(responseString)) {
                    loadingLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    return;
                }
                usersListLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);

                List<UserInfo> users = Utils.parseDialogUsersList(responseString);
                DialogUsersListAdapter adapter = new DialogUsersListAdapter(DialogUsersActivity.this, users);
                mDialogList.setAdapter(adapter);
            }
        });
    }
}
