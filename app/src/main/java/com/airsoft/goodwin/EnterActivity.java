package com.airsoft.goodwin;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.airsoft.goodwin.PrivateOffice.PrivateOfficeActivity;
import com.airsoft.goodwin.connection.ApplicationCookies;
import com.airsoft.goodwin.connection.FirstdivisionRestClient;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class EnterActivity extends AppCompatActivity {
    private static final int ENTER_VIEW_INDEX = 0;
    private static final int LOGIN_VIEW_INDEX = 1;
    private static final int REGISTRATION_VIEW_INDEX = 2;

    ViewFlipper mFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter);

        mFlipper = (ViewFlipper) findViewById(R.id.enterScreenFlipper);
        mFlipper.setInAnimation(this, R.anim.enter_screen_flip_animated_in);
        mFlipper.setOutAnimation(this, R.anim.enter_screen_flip_out);

        ApplicationCookies.getInstance().initialize(getApplicationContext());
    }

    public void showLoginForm(View view) {
        showScreen(LOGIN_VIEW_INDEX);
    }

    public void showRegistrationForm(View view) {
        showScreen(REGISTRATION_VIEW_INDEX);
    }

    public void showEnterScreen(View view) {
        showScreen(ENTER_VIEW_INDEX);
    }

    private void showScreen(int index) {
        if (mFlipper.getDisplayedChild() == 0) {
            mFlipper.setInAnimation(this, R.anim.enter_screen_flip_animated_in);
            mFlipper.setOutAnimation(this, R.anim.enter_screen_flip_out);
            mFlipper.setDisplayedChild(index);
        } else {
            mFlipper.setInAnimation(this, R.anim.enter_screen_flip_out);
            mFlipper.setOutAnimation(this, R.anim.enter_screen_flip_animated_out);
            mFlipper.setDisplayedChild(index);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void makeLogin(View view) {
        String login = ((EditText) findViewById(R.id.loginPersonalNumberText)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPasswordText)).getText().toString();

        final Button enterButton = (Button) findViewById(R.id.enterLoginSubmitButton);
        enterButton.setEnabled(false);

        final ProgressBar pb = (ProgressBar) findViewById(R.id.enterActivityEnterProgressBar);
        pb.setVisibility(View.VISIBLE);

        FirstdivisionRestClient.getInstance().makeLogin(login, password, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(EnterActivity.this, getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
                enterButton.setEnabled(true);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Document doc = Utils.getDomElement(responseString);
                Element responseNode = (Element) doc.getFirstChild();
                NodeList resultNode = responseNode.getElementsByTagName("result");
                if (resultNode.getLength() > 0) {
                    Node result = resultNode.item(0);
                    if (result.getTextContent().equals("1")) {
                        NodeList userInfoList = responseNode.getElementsByTagName("user");
                        if (userInfoList.getLength() > 0) {
                            //Utils.initializeUserInfoByXMLElement((Element) userInfoList.item(0));
                        }
                        Intent intent = new Intent(EnterActivity.this, PrivateOfficeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EnterActivity.this, getString(R.string.server_login_error), Toast.LENGTH_SHORT).show();
                    }
                }

                enterButton.setEnabled(true);
                pb.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mFlipper.getDisplayedChild() != 0){
            mFlipper.setInAnimation(this, R.anim.enter_screen_flip_out);
            mFlipper.setOutAnimation(this, R.anim.enter_screen_flip_animated_out);
            mFlipper.setDisplayedChild(0);
        }
    }

    public void makeRegistration(View view) {
        String rank = ((Spinner) findViewById(R.id.enter_registration_rank)).getSelectedItem().toString();
        String subdivision = Long.toString(((Spinner) findViewById(R.id.enter_registration_rank)).getSelectedItemId());
        String login = ((EditText) findViewById(R.id.enter_registration_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.enter_registration_password)).getText().toString();
        String email = ((EditText) findViewById(R.id.enter_registration_email)).getText().toString();
        String lastname = ((EditText) findViewById(R.id.enter_registration_lastname)).getText().toString();
        String firstname = ((EditText) findViewById(R.id.enter_registration_firstname)).getText().toString();
        String middlename = ((EditText) findViewById(R.id.enter_registration_middlename)).getText().toString();

        if (rank.isEmpty() || subdivision.isEmpty() || login.isEmpty() || password.isEmpty() ||
                email.isEmpty()|| lastname.isEmpty() || firstname.isEmpty() || middlename.isEmpty()) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> fields = new HashMap<>();
        fields.put("personalnumber", login);
        fields.put("password", password);
        fields.put("firstname", firstname);
        fields.put("middlename", middlename);
        fields.put("lastname", lastname);
        fields.put("email", email);
        fields.put("rank", rank);
        fields.put("subdivision", subdivision);

        final View buttons = findViewById(R.id.enter_registration_buttons);
        final View pb = findViewById(R.id.enter_registration_progress_layout);
        buttons.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        FirstdivisionRestClient.getInstance().makeRegistration(fields, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                buttons.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                Toast.makeText(EnterActivity.this, getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (ApplicationCookies.getInstance().getCookiesStore().getCookies().size() < 2) {
                    Toast.makeText(EnterActivity.this, getString(R.string.server_register_error), Toast.LENGTH_SHORT).show();
                    buttons.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    return;
                }

                Intent intent = new Intent(EnterActivity.this, PrivateOfficeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
