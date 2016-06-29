package com.airsoft.goodwin.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airsoft.goodwin.Comrades.ComradesActivity;
import com.airsoft.goodwin.Duty.DutyActivity;
import com.airsoft.goodwin.EnterActivity;
import com.airsoft.goodwin.ErrorActivity;
import com.airsoft.goodwin.FirstdivisionAppCompatActivity;
import com.airsoft.goodwin.Linen.LinenActivity;
import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.R;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ChatActivity extends FirstdivisionAppCompatActivity {

    private ListView messagesList;
    private TextView messageTextView;
    private View loadingLayout;
    private View contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.applicationUserInfo.can("chatvisiter")) {
            Intent intent = new Intent(ChatActivity.this, ErrorActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadingLayout = findViewById(R.id.chat_loading_layout);
        contentLayout = findViewById(R.id.chat_content_layout);
        messagesList = (ListView) findViewById(R.id.chat_messages_list_view);
        MessagesListAdapter adapter = new MessagesListAdapter(ChatActivity.this);
        messagesList.setAdapter(adapter);

        messageTextView = (TextView) findViewById(R.id.chat_sending_message_text);

        if (Settings.applicationUserInfo.id.equals("-")) {
            updateUserInfo();
        } else {
            updateChatList();
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
            Intent intent = new Intent(ChatActivity.this, PrivateOfficeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_duty) {
            Intent intent = new Intent(ChatActivity.this, DutyActivity.class);
            startActivity(intent);
            finish();;
        } else if (id == R.id.nav_linen) {
            Intent intent = new Intent(ChatActivity.this, LinenActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_comrades) {
            Intent intent = new Intent(ChatActivity.this, ComradesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_chat) {
            return false;
        } else if (id == R.id.nav_exit) {
            FirstdivisionRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(ChatActivity.this, getString(R.string.server_connection_error),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Intent intent = new Intent(ChatActivity.this, EnterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUserInfo() {
        FirstdivisionRestClient.getInstance().getFullUserInformation(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                updateUserInfo();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Settings.applicationUserInfo = Utils.parsePersonalOfficeUserInfo(responseString);

                updateChatList();
            }
        });
    }

    private void updateChatList() {
        FirstdivisionRestClient.getInstance().getChatMessages(Integer.MAX_VALUE, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                updateChatList();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingLayout.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                List<Message> messages = Utils.parseMessagesList(responseString);

                MessagesListAdapter adapter = (MessagesListAdapter)messagesList.getAdapter();
                adapter.addMessages(messages);

                new Handler() {{
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // do something here
                            updateChatList();
                        }
                    }, 1000);
                }};
            }
        });
    }

    public void sendMessage(final View view) {
        String messageText = messageTextView.getText().toString();
        messageTextView.setText("");

        View viewFocused = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Message message = new Message();
        message.userId = Integer.parseInt(Settings.applicationUserInfo.id);
        message.photo = Settings.applicationUserInfo.photoPath;
        message.firstname = Settings.applicationUserInfo.firstname;
        message.middlename = Settings.applicationUserInfo.middlename;
        message.lastname = Settings.applicationUserInfo.lastname;
        message.text = messageText;

        final MessagesListAdapter adapter = (MessagesListAdapter) messagesList.getAdapter();
        adapter.addUnsentMessage(message);

        FirstdivisionRestClient.getInstance().sendChatMessage(messageText, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                adapter.clearUnsentMessages();
                Toast.makeText(ChatActivity.this, R.string.server_connection_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                List<Message> messages = Utils.parseMessagesList(responseString);
                adapter.addMessages(messages);
                adapter.clearUnsentMessages();
            }
        });
    }
}
