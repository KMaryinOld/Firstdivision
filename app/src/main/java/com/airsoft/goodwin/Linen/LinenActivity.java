package com.airsoft.goodwin.Linen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.airsoft.goodwin.Chat.ChatActivity;
import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.EnterActivity;
import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class LinenActivity extends FirstdivisionAppCompatActivity {

    private TableLayout table;
    private TableLayout balanceTable;
    private FloatingActionButton mainActionButton;
    private FloatingActionButton receiveActionButton;
    private FloatingActionButton returnActionButton;
    private Map<Integer, String> linenTypes;
    private Map<Integer, Integer> linenBalance;

    private LinenActionFragment actionFragment;

    private boolean isFabOpen;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.applicationUserInfo.can("linenwatch")) {
            Intent intent = new Intent(LinenActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mainActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        receiveActionButton = (FloatingActionButton) findViewById(R.id.linen_action_receive);
        receiveActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionFragment != null) {
                    actionFragment.setActionType(LinenHistoryRecord.LINEN_TYPE_RECEIPT);
                    actionFragment.show(getFragmentManager(), "FRAGMENT_RECEIVE");
                }
            }
        });
        returnActionButton = (FloatingActionButton) findViewById(R.id.linen_action_return);
        returnActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionFragment != null) {
                    actionFragment.setActionType(LinenHistoryRecord.LINEN_TYPE_RETURN);
                    actionFragment.show(getFragmentManager(), "FRAGMENT_RETURN");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        table = (TableLayout) findViewById(R.id.linen_table);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);

        balanceTable = (TableLayout) findViewById(R.id.linen_balance_table);
        balanceTable.setStretchAllColumns(true);
        balanceTable.setShrinkAllColumns(true);

        linenBalance = new HashMap<>();
        initializeTable();
        isFabOpen = false;
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
    }

    private void initializeTable() {
        final ProgressBar pb = (ProgressBar) findViewById(R.id.linen_loading_progressbar);
        pb.setVisibility(View.VISIBLE);
        final View tables = findViewById(R.id.linen_tables);

        Animation anim = AnimationUtils.loadAnimation(LinenActivity.this, android.R.anim.fade_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainActionButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainActionButton.setAnimation(anim);
        mainActionButton.animate();

        final TableRow tableHeader = (TableRow) table.findViewById(R.id.linen_table_header_row);
        tableHeader.setBackgroundColor(ContextCompat.getColor(LinenActivity.this, R.color.colorColoredBackground));
        TextView numberPP = new TextView(this);
        numberPP.setText("№");
        tableHeader.addView(numberPP);
        TextView date = new TextView(this);
        date.setText("Дата последней \nсдачи/получения");
        tableHeader.addView(date);

        TextView type = new TextView(this);
        type.setText("Тип");
        tableHeader.addView(type);

        FirstdivisionRestClient.getInstance().getLinenHistory(1, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pb.setVisibility(View.GONE);
                findViewById(R.id.linen_failure_loading_layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (Utils.isErrorResponse(responseString)) {
                    Toast.makeText(LinenActivity.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    linenTypes = Utils.parseLinenTypes(responseString);
                    actionFragment = LinenActionFragment.newInstance((HashMap<Integer, String>) linenTypes);

                    for (Map.Entry<Integer, String> entry : linenTypes.entrySet()) {
                        TextView type = new TextView(LinenActivity.this);
                        type.setText(entry.getValue());
                        tableHeader.addView(type);

                        linenBalance.put(entry.getKey(), 0);
                    }

                    List<LinenHistoryRecord> linenHistoryRecords = Utils.parseLinenHistory(responseString);
                    for (int i = 0; i < linenHistoryRecords.size(); ++i) {
                        addTableRecordRow(linenHistoryRecords.get(i));
                    }
                }

                pb.setVisibility(View.GONE);
                tables.setVisibility(View.VISIBLE);

                mainActionButton.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(LinenActivity.this, android.R.anim.fade_in);
                mainActionButton.setAnimation(anim);
                mainActionButton.animate();
            }
        });
    }

    public void addTableRecordRow(LinenHistoryRecord linenHistoryRecord) {
        TableRow row = new TableRow(LinenActivity.this);

        TextView numberText = new TextView(LinenActivity.this);
        numberText.setText(String.valueOf(table.getChildCount()));
        numberText.setBackgroundColor(ContextCompat.getColor(LinenActivity.this, R.color.colorPrimaryLight));
        row.addView(numberText);

        TextView dateText = new TextView(LinenActivity.this);
        dateText.setText(String.format("%d.%d.%d", linenHistoryRecord.date.get(Calendar.DAY_OF_MONTH),
                Calendar.MONTH, linenHistoryRecord.date.get(Calendar.YEAR)));
        row.addView(dateText);

        TextView typeText = new TextView(LinenActivity.this);
        typeText.setText(linenHistoryRecord.type == LinenHistoryRecord.LINEN_TYPE_RETURN ?
                getResources().getString(R.string.linen_type_return) :
                getResources().getString(R.string.linen_type_receipt));
        row.addView(typeText);

        for (Map.Entry<Integer, String> entry : linenTypes.entrySet()) {
            TextView itemText = new TextView(LinenActivity.this);
            itemText.setText(String.valueOf(linenHistoryRecord.items.get(entry.getKey())));
            row.addView(itemText);

            Integer currentBalance = linenBalance.get(entry.getKey());
            if (linenHistoryRecord.type == LinenHistoryRecord.LINEN_TYPE_RECEIPT) {
                currentBalance += linenHistoryRecord.items.get(entry.getKey());
            } else {
                currentBalance -= linenHistoryRecord.items.get(entry.getKey());
            }
            linenBalance.put(entry.getKey(), currentBalance);
        }

        table.addView(row);

        updateBalanceTable();
    }

    private void updateBalanceTable() {
        TableRow namesRow = (TableRow) balanceTable.findViewById(R.id.linen_balance_table_names);
        TableRow valuesRow = (TableRow) balanceTable.findViewById(R.id.linen_balance_table_values);

        namesRow.removeAllViews();
        valuesRow.removeAllViews();

        for (Map.Entry<Integer, String> entry : linenTypes.entrySet()) {
            TextView type = new TextView(LinenActivity.this);
            type.setText(entry.getValue());
            namesRow.addView(type);

            TextView value = new TextView(LinenActivity.this);
            value.setText(String.valueOf(linenBalance.get(entry.getKey())));
            valuesRow.addView(value);
        }
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
            Intent intent = new Intent(LinenActivity.this, PrivateOfficeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_duty) {
            Intent intent = new Intent(LinenActivity.this, DutyActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_linen) {
            return false;
        } else if (id == R.id.nav_comrades) {
            Intent intent = new Intent(LinenActivity.this, ComradesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(LinenActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exit) {
            FirstdivisionRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(LinenActivity.this, getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Intent intent = new Intent(LinenActivity.this, EnterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void animateFAB() {
        if(isFabOpen){
            mainActionButton.startAnimation(rotate_backward);
            receiveActionButton.startAnimation(fab_close);
            returnActionButton.startAnimation(fab_close);
            receiveActionButton.setClickable(false);
            returnActionButton.setClickable(false);
            isFabOpen = false;
        } else {
            mainActionButton.startAnimation(rotate_forward);
            receiveActionButton.startAnimation(fab_open);
            returnActionButton.startAnimation(fab_open);
            receiveActionButton.setClickable(true);
            returnActionButton.setClickable(true);
            isFabOpen = true;
        }
    }

    public Map<Integer, Integer> getLinenBalance() {
        return linenBalance;
    }
}
