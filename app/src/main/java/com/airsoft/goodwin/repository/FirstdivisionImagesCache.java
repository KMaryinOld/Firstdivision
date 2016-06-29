package com.airsoft.goodwin.repository;

import android.content.Context;
import android.graphics.Bitmap;

public class FirstdivisionImagesCache {
    private final int CACHE_SIZE = 1024 * 1024 * 50; // 50MB
    private final String UNIQUE_NAME = "images_cache";

    private DiskLruImageCache mDiskCache;
    private final Object mDiskCacheLock = new Object();

    private Context mContext;

    private static volatile FirstdivisionImagesCache instance;

    public static FirstdivisionImagesCache getInstance() {
        FirstdivisionImagesCache localInstance = instance;
        if (localInstance == null) {
            synchronized (FirstdivisionImagesCache.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FirstdivisionImagesCache();
                }
            }
        }
        return localInstance;
    }

    private FirstdivisionImagesCache() {
    }

    public void initialize(Context c) {
        mContext = c;
        mDiskCache = new DiskLruImageCache(mContext, UNIQUE_NAME, CACHE_SIZE, Bitmap.CompressFormat.PNG, 70);
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null && mDiskCache.getBitmap(key) == null) {
                mDiskCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null) {
                return mDiskCache.getBitmap(key);
            }
        }
        return null;
    }
}
