package com.brainwellnessspa.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.brainwellnessspa.Utility.MusicService;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean screenOff = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        /*Intent i = new Intent(context, MusicService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        }else {
            context.startService(i);
        }*/
    }
}