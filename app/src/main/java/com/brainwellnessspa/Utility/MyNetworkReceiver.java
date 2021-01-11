package com.brainwellnessspa.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.brainwellnessspa.BWSApplication;
import com.downloader.PRDownloader;
import com.downloader.Status;

import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;

public class MyNetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean status = BWSApplication.isNetworkConnected(context);

        Log.d("network",String.valueOf(status));
        if(!status) {
            if (isDownloading) {
                  PRDownloader.pause(downloadIdOne);
                    BWSApplication.showToast(String.valueOf(status),context);
                    isDownloading = false;
                    BWSApplication.showToast(String.valueOf(status)+Status.valueOf(PRDownloader.getStatus(downloadIdOne).name()),context);
            }
        }else {
            if (!isDownloading) {
                if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.resume(downloadIdOne);
                    BWSApplication.showToast(String.valueOf(status)+Status.valueOf(PRDownloader.getStatus(downloadIdOne).name()),context);
                }
            }
        }
    }
}