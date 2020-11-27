package com.brainwellnessspa.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.brainwellnessspa.Utility.MusicService.Broadcast_PLAY_NEW_AUDIO;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO)
                .putExtra("actionname", intent.getAction()));
    }
}
