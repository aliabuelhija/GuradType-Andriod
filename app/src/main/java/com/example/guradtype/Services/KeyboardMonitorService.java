package com.example.guradtype.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class KeyboardMonitorService extends Service {
    private static final String TAG = "KeyboardMonitorService";
    private static final String SHARED_PREFS_NAME = "GuardTypePrefs";
    private static final String PREF_GUARD_TYPE_KEYBOARD_ID = "GuardTypeKeyboardId";

    private KeyboardObserver keyboardObserver;
    private String guardTypeKeyboardId;
    private Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        guardTypeKeyboardId = preferences.getString(PREF_GUARD_TYPE_KEYBOARD_ID, "");

        handler = new Handler();
        keyboardObserver = new KeyboardObserver(handler, this, guardTypeKeyboardId);
        getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, keyboardObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");

        getContentResolver().unregisterContentObserver(keyboardObserver);
        keyboardObserver.stopObserving();
    }
}
