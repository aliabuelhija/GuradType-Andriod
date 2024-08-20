package com.example.guradtype.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.guradtype.Services.KeyboardMonitorService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "Device booted, starting KeyboardMonitorService");
            Intent serviceIntent = new Intent(context, KeyboardMonitorService.class);
            context.startService(serviceIntent);
        }
    }
}
