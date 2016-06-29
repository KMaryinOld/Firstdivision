package com.airsoft.goodwin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.Inventory.InventoryActivity;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.connection.ApplicationCookies;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.repository.FirstdivisionImagesCache;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

public class FirstdivisionAppCompatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onResume() {
        initialize();
        super.onResume();

        List<Cookie> cookies = ApplicationCookies.getInstance().getCookiesStore().getCookies();
        if (cookies.size() < 2) {
            Intent intent = new Intent(this, EnterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initialize() {
        Context appContext = getApplicationContext();
        ApplicationCookies.getInstance().initialize(appContext);
        FirstdivisionImagesCache.getInstance().initialize(appContext);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_private_office) {
            Intent intent = new Intent(this, PrivateOfficeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_inventory) {
            Intent intent = new Intent(this, InventoryActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.nav_duty) {
            Intent intent = new Intent(this, DutyActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_linen) {
            Intent intent = new Intent(this, LinenActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_comrades) {
            Intent intent = new Intent(this, ComradesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exit) {
            FirstdivisionRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(FirstdivisionAppCompatActivity.this, getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Intent intent = new Intent(FirstdivisionAppCompatActivity.this, EnterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
