package com.qltech.bws.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qltech.bws.EncryptDecryptUtils.DownloadMedia;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
         intent = new Intent(context, DownloadMedia.class);
        context.startService(intent);
    }
}