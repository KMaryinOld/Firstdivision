package com.airsoft.goodwin.connection;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

public class ApplicationCookies {
    private Context mContext;

    private static volatile ApplicationCookies instance;

    public static ApplicationCookies getInstance() {
        ApplicationCookies localInstance = instance;
        if (localInstance == null) {
            synchronized (ApplicationCookies.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ApplicationCookies();
                }
            }
        }
        return localInstance;
    }

    private ApplicationCookies() {
    }

    public void initialize(Context c) {
        mContext = c;
    }

    public PersistentCookieStore getCookiesStore() {
        return new PersistentCookieStore(mContext);
    }
}
