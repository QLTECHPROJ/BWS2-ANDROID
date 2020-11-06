package com.brainwellnessspa.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new Intent(context, MusicService.class);
//        context.stopService(context);
    }
}
