package com.airsoft.goodwin.PrivateOffice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.EnterActivity;
import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.Linen.LinenActionFragment;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.StartActivity;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.UserInfo.UserInfoFragment;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.repository.FirstdivisionImagesCache;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.AfterPhotoUpdateHandler;
import com.airsoft.goodwin.utils.UpdateUserPhotoTask;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class PrivateOfficeActivity extends FirstdivisionAppCompatActivity {
    private static final int REQUEST = 1;
    private static final int RESULT_OK = 1;
    private UserInfoFragment userInfoFragment;
    private ImageChangerFragment imageChangerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Settings.applicationUserInfo.id.equals("-")) {
            Intent intent = new Intent(PrivateOfficeActivity.this, StartActivity.class);
            intent.putExtra("back", StartActivity.class);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_office);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.animate().translationY(350);
                final int imageResourceId = R.drawable.ic_pencil;
                //final int imageResourceId = R.drawable.ic_check;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(imageResourceId);
                        fab.animate().translationY(0);
                    }
                }, 200);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userInfoFragment = (UserInfoFragment)getFragmentManager().findFragmentById(R.id.private_office_user_info_fragment);
        imageChangerFragment = ImageChangerFragment.newInstance();
        userInfoFragment.getImageryHead().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChangerFragment.show(getFragmentManager(), "image_changer");
            }
        });

        reloadUserInformation();
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

    private void reloadUserInformation() {
        final View loadingLayout = findViewById(R.id.private_office_loading_data_layout);
        final View failureLayout = findViewById(R.id.private_office_loading_error_layout);
        final View fragmentLayout = findViewById(R.id.private_office_user_info_fragment_layout);

        loadingLayout.setVisibility(View.VISIBLE);
        failureLayout.setVisibility(View.GONE);
        fragmentLayout.setVisibility(View.GONE);

        if (Settings.applicationUserInfo.id.equals("-")) {

        } else {
            if (!Settings.applicationUserInfo.can("viewpersonaloffice")) {
                Intent intent = new Intent(PrivateOfficeActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }

            userInfoFragment.setUserInfo(Settings.applicationUserInfo);

            loadingLayout.setVisibility(View.GONE);
            fragmentLayout.setVisibility(View.VISIBLE);
        }
    }

    public void reloadButtonOnClick(View view) {
        reloadUserInformation();
    }

    public void changeImageOnClick(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, REQUEST);
    }

    public void confirmImageOnClick(View view) {
        imageChangerFragment.getSelectedBitmap();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap img = null;

        if (requestCode == REQUEST && resultCode != 0) {
            Uri selectedImage = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageChangerFragment.getPickedImage().setImageBitmap(img);
            imageChangerFragment.showImgAreaSelectLayout();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
