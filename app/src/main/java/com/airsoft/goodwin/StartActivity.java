package com.airsoft.goodwin;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.Dialog.DialogUsersActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.Inventory.InventoryActivity;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.connection.ApplicationCookies;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.repository.FirstdivisionImagesCache;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.testing.ScrollingActivity;
import com.airsoft.goodwin.utils.ImageryView;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class StartActivity extends AppCompatActivity {
    private Class<?> backActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            backActivity = (Class) extras.get("back");
        }

        ApplicationCookies.getInstance().initialize(getApplicationContext());
        FirstdivisionImagesCache.getInstance().initialize(getApplicationContext());

        if (Settings.applicationUserInfo.id.equals("-") &&
                ApplicationCookies.getInstance().getCookiesStore().getCookies().size() > 0) {
            loadUserInfo();
        } else {
            goToWork();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUserInfo() {
        final View loadingLayout = findViewById(R.id.start_loading_layout);
        final View failureLayout = findViewById(R.id.start_error_connection_layout);

        loadingLayout.setVisibility(View.VISIBLE);
        failureLayout.setVisibility(View.GONE);

        FirstdivisionRestClient.getInstance().getFullUserInformation(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingLayout.setVisibility(View.GONE);
                failureLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingLayout.setVisibility(View.GONE);

                Settings.applicationUserInfo = Utils.parsePersonalOfficeUserInfo(responseString);
                goToWork();
            }
        });
    }

    private void goToWork() {
        Intent intent;
        if (ApplicationCookies.getInstance().getCookiesStore().getCookies().size() > 0) {
            if (backActivity == null) {
                intent = new Intent(StartActivity.this, InventoryActivity.class);
            } else {
                intent = new Intent(StartActivity.this, backActivity);
            }
        } else {
            intent = new Intent(StartActivity.this, EnterActivity.class);
        }
        startActivity(intent);
        finish();
    }
}