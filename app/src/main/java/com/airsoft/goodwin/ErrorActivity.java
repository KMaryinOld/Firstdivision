package com.airsoft.goodwin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.settings.Settings;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UserInfo user = Settings.applicationUserInfo;

        user.photoPath = "";
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
