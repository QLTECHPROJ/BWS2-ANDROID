package com.brainwellnessspa.utility

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class NotificationService : Service() {
    var status: Notification? = null
    private val LOG_TAG = "NotificationService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == CONSTANTS.ACTION.STARTFOREGROUND_ACTION) {
//            showNotification();
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        } else if (intent.action == CONSTANTS.ACTION.PREV_ACTION) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show()
            Log.i(LOG_TAG, "Clicked Previous")
        } else if (intent.action == CONSTANTS.ACTION.PLAY_ACTION) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show()
            Log.i(LOG_TAG, "Clicked Play")
        } else if (intent.action == CONSTANTS.ACTION.NEXT_ACTION) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show()
            Log.i(LOG_TAG, "Clicked Next")
        } else if (intent.action == CONSTANTS.ACTION.STOPFOREGROUND_ACTION) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent")
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    } /*  private void showNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                CONSTANTS.getDefaultAlbumArt(this));
        Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);
        notificationIntent.setAction(CONSTANTS.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(CONSTANTS.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);
        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(CONSTANTS.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(CONSTANTS.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);
        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(CONSTANTS.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);
        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");
        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");
        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.square_app_icon;
        status.contentIntent = pendingIntent;
        startForeground(CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }*/
}