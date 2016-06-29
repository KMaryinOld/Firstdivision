package com.airsoft.goodwin.Inventory;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.GridView;

import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.UserInfo.UserInfo;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class InventoryActivity extends FirstdivisionAppCompatActivity {

    private View mContentLayout;
    private View mLoadingLayout;
    private View mErrorLayout;

    private Integer mUserId;

    private InventoryGridAdapter mAdapter;
    private InventoryItemFragment mItemFragment;
    private InventoryAddGridFragment mAddGridFragment;

    private Map<String, Bitmap> mItemImages;
    private Map<Integer, UserInfo> mItemUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("userid")) {
            mUserId = Integer.parseInt((String)extras.get("userid"));
        } else {
            mUserId = Integer.parseInt(Settings.applicationUserInfo.id);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAddGridFragment.show(getFragmentManager(), "ADDITIONAL_GRID");
                }
            });
        }
        if (Settings.applicationUserInfo.id.equals(mUserId.toString())) {
            fab.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoadingLayout = findViewById(R.id.inventory_loading_layout);
        mContentLayout = findViewById(R.id.inventory_content_layout);
        mErrorLayout = findViewById(R.id.inventory_error_layout);

        mItemImages = new HashMap<>();
        mItemUsers = new HashMap<>();
        mAdapter = new InventoryGridAdapter(this);
        mItemFragment = InventoryItemFragment.newInstance();
        mAddGridFragment = InventoryAddGridFragment.newInstance();

        GridView grid = (GridView)mContentLayout.findViewById(R.id.inventory_content_grid);
        grid.setAdapter(mAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InventoryThing item = (InventoryThing) mAdapter.getItem(position);
                mItemFragment.setCurrentItem(item);
                mItemFragment.show(getFragmentManager(), "item");
            }
        });

        refreshData();
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

    private void refreshData() {
        mContentLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);

        FirstdivisionRestClient.getInstance().getInventoryItems(mUserId, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mLoadingLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                    List<InventoryThing> items = Utils.parseInventoryItems(responseString);

                    mAdapter.addAllThings(items);

                    for (final InventoryThing item : items) {
                        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mItemImages.put(item.thingPhotoFilename, resource);
                                mAdapter.notifyDataSetChanged();
                            }
                        };
                        Glide.with(InventoryActivity.this).load(String.format(Locale.GERMAN, "%s/data/%s",
                                Settings.serverAddress, item.thingPhotoFilename)).asBitmap().into(target);
                    }

                    mItemUsers.putAll(Utils.parseInventoryUsers(responseString));
                }
            });
    }

    public Bitmap getItemImage(String photoFilename) {
        return mItemImages.get(photoFilename);
    }

    public UserInfo getUser(int userId) {
        return mItemUsers.get(userId);
    }

    private void updateAllThingsList() {
        FirstdivisionRestClient.getInstance().getInventoryItems(Integer.parseInt(Settings.applicationUserInfo.id),
                new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                updateAllThingsList();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                List<InventoryThing> items = Utils.parseInventoryItems(responseString);
                mAddGridFragment.initializeItems(items);

                for (final InventoryThing item : items) {
                    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mItemImages.put(item.thingPhotoFilename, resource);
                        }
                    };
                    Glide.with(InventoryActivity.this).load(String.format(Locale.GERMAN, "%s/data/%s",
                            Settings.serverAddress, item.thingPhotoFilename)).asBitmap().into(target);
                }

                mItemUsers.putAll(Utils.parseInventoryUsers(responseString));
            }
        });
    }
}
