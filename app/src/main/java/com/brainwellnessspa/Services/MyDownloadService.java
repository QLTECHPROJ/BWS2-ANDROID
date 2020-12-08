/*
package com.brainwellnessspa.Services;

import android.app.Notification;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;

import java.util.List;

public class MyDownloadService extends DownloadService implements DownloadManager.Listener {
    public MyDownloadService(int foregroundNotificationId) {
        super(foregroundNotificationId);
    }

    public MyDownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        super(foregroundNotificationId, foregroundNotificationUpdateInterval);
    }

    public MyDownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, @Nullable String channelId, int channelNameResourceId, int channelDescriptionResourceId) {
        super(foregroundNotificationId, foregroundNotificationUpdateInterval, channelId, channelNameResourceId, channelDescriptionResourceId);
    }

    @Override
    public DownloadManager getDownloadManager() {
        return null;
    }

    @Nullable
    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public Notification getForegroundNotification(List<Download> downloads) {
        return null;
    }

}
*/
