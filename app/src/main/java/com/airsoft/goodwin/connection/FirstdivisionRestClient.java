package com.airsoft.goodwin.connection;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.airsoft.goodwin.Linen.LinenHistoryRecord;
import com.airsoft.goodwin.settings.Settings;
import com.airsoft.goodwin.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class FirstdivisionRestClient {
    private static final int REQUEST_TIMEOUT = 30*1000;
    private AsyncHttpClient client;

    private static volatile FirstdivisionRestClient instance;

    public static FirstdivisionRestClient getInstance() {
        FirstdivisionRestClient localInstance = instance;
        if (localInstance == null) {
            synchronized (FirstdivisionRestClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FirstdivisionRestClient();
                }
            }
        }
        return localInstance;
    }

    private FirstdivisionRestClient() {
        client = new AsyncHttpClient();
        initializeClient();
    }

    private void initializeClient() {
        client.setTimeout(REQUEST_TIMEOUT);
        client.setEnableRedirects(true);
        client.setCookieStore(ApplicationCookies.getInstance().getCookiesStore());
    }

    public void makeLogin(String login, String pass, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("personal_number", login);
        params.add("password", pass);

        client.post(String.format("%s/rest_api/user_login.php", Settings.serverAddress), params, responseHandler);
    }

    public void getFullUserInformation(ResponseHandlerInterface responseHandler) {
        client.post(String.format("%s/rest_api/user_personal_office_info.php", Settings.serverAddress), responseHandler);
    }

    public void getDutyUsers(String timestamp, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("date", timestamp);

        client.post(String.format("%s/rest_api/get_duty_list.php", Settings.serverAddress), params, responseHandler);
    }

    public void setDutyUser(String userId, String timestamp, int dutyType, int dutyPosition, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("action", "add");
        params.add("userid", userId);
        params.add("timestamp", timestamp);
        params.put("dutytype", dutyType);
        params.put("dutyposition", dutyPosition);

        client.post(String.format("%s/rest_api/change_duty_user.php", Settings.serverAddress), params, responseHandler);
    }

    public void removeDutyUser(String userId, String timestamp, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("action", "remove");
        params.add("userid", userId);
        params.add("timestamp", timestamp);

        client.post(String.format("%s/rest_api/change_duty_user.php", Settings.serverAddress), params, responseHandler);
    }

    public void getLinenHistory(Integer subdivisionId, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("id", subdivisionId.toString());

        client.post(String.format("%s/rest_api/linen_get_history.php", Settings.serverAddress), params, responseHandler);
    }

    public void sendLinenHistoryRecord(LinenHistoryRecord record, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("type", String.valueOf(record.type));
        params.add("date", Utils.getTimestampFromCalendar(record.date));
        Map<String, String> jsonableMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : record.items.entrySet()) {
            jsonableMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        JSONObject json = new JSONObject(jsonableMap);
        params.add("items", json.toString());

        client.post(String.format("%s/rest_api/linen_save_history_record.php", Settings.serverAddress), params, responseHandler);
    }

    public void logout(ResponseHandlerInterface responseHandler) {
        client.post(String.format("%s/rest_api/logout.php", Settings.serverAddress), responseHandler);
        ApplicationCookies.getInstance().getCookiesStore().clear();
    }

    public void getComradesList(ResponseHandlerInterface responseHandler) {
        client.post(String.format("%s/rest_api/comrades_get_list.php", Settings.serverAddress), responseHandler);
    }

    public void getChatMessages(int lastCommentId, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.put("action", "get_messages");

        client.post(String.format("%s/rest_api/chat.php", Settings.serverAddress), params, responseHandler);
    }

    public void sendChatMessage(String messageText, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.put("action", "save_message");
        params.put("text", messageText);

        client.post(String.format("%s/rest_api/chat.php", Settings.serverAddress), params, responseHandler);
    }

    public void changePersonalImageAvatar(Bitmap img, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, imageStream);
        params.put("image", new ByteArrayInputStream(imageStream.toByteArray()), "image.jpg");

        client.post(String.format("%s/rest_api/change_user_personal_avatar.php", Settings.serverAddress),
                params, responseHandler);
    }

    public void makeRegistration(Map<String, String> fields, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }

        client.post(String.format("%s/registration.php", Settings.serverAddress),
                params, responseHandler);
    }

    public void getDialogUsersList(ResponseHandlerInterface responseHandler) {
        client.get(String.format("%s/rest_api/dialog.php?action=users_list", Settings.serverAddress), responseHandler);
    }

    public void getInventoryItems(int userId, ResponseHandlerInterface responseHandler) {
        client.get(String.format("%s/rest_api/inventory.php?action=get_items&id=%d", Settings.serverAddress,
                userId), responseHandler);
    }
}
