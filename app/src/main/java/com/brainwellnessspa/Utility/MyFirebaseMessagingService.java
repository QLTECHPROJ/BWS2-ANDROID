package com.brainwellnessspa.Utility;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity;
import com.brainwellnessspa.dashboardModule.manage.MyPlaylistListingActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.segment.analytics.Properties;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    private NotificationCompat.Builder notificationBuilder;
    String title = "", image = "", message = "", flag = "", id = "", IsLock = "";
    TaskStackBuilder taskStackBuilder;
    PendingIntent resultPendingIntent = null;
    Intent resultIntent = null;
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String TAGs = "MyFirebaseIDService";
    public static String fcm_Tocken;
    Context context;
    MyFirebaseMessagingService activity;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
//        Log.e("AAAAAAAAAAAAAAAAAAAAA", "" + remoteMessage.toString());
//        String tag = remoteMessage.getData().get("tag");

//        FirebaseMessaging.getInstance().subscribeToTopic("BWS");

//        image = getBitmapFromURL(img);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        if (remoteMessage == null)
            return;
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification rendom number: " + m);
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
            String flag = remoteMessage.getNotification().getBody();
            String id = remoteMessage.getData().get("id");
            String IsLock = remoteMessage.getData().get("IsLock");
            sendNotification(title, message, flag, id, String.valueOf(m), IsLock);
        }
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                title = remoteMessage.getData().get("title");
                image = remoteMessage.getData().get("image");
                message = remoteMessage.getData().get("body");
                flag = remoteMessage.getData().get("flag");
                id = remoteMessage.getData().get("id");
                IsLock = remoteMessage.getData().get("IsLock");
                sendNotification(title, message, flag, id, String.valueOf(m), IsLock);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        Properties p = new Properties();
    /*    AnalyticsContext.Campaign campaign = new AnalyticsContext.Campaign();
        campaign.putValue("id", id);
        campaign.putName(title);
        campaign.putContent(message);
        campaign.putMedium("Push");
        campaign.putSource("Admin");
        p.putValue("campaign", campaign);*/

        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String   UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        p.putValue("userId", UserID);
        p.putValue("playlistId", id);
        p.putName(title);
        p.putValue("message",message);
        BWSApplication.addToSegment("Push Notification Received", p, CONSTANTS.track);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Intent registrationComplete = new Intent(CONSTANTS.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

        if (token.isEmpty()) {
            token = FirebaseInstanceId.getInstance().getToken();
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        SharedPreferences.Editor editor1 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit();
        editor1.putString(CONSTANTS.Token, token); //Friend
        editor1.apply();
        editor1.commit();
        fcm_Tocken = token;
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences.Editor editor1 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit();
        editor1.putString(CONSTANTS.Token, token); //Friend
        editor1.apply();
        editor1.commit();
        Log.e(TAGs, "sendRegistrationToServer: " + token);
    }

    private void sendNotification(String title, String message, String flag, String id, String m, String IsLock) {
        context = MyFirebaseMessagingService.this;
        activity = MyFirebaseMessagingService.this;
        taskStackBuilder = TaskStackBuilder.create(this);
        String channelId = context.getString(R.string.default_notification_channel_id);
        int requestID = (int) System.currentTimeMillis();
        CharSequence channelName = "Brain Wellness App";
        int importance = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        try {
            if (flag != null && flag.equalsIgnoreCase("Playlist")) {
               /* if (!IsLock.equalsIgnoreCase("0")) {
                    resultIntent = new Intent(this, BottomNavigationActivity.class);
                    taskStackBuilder.addParentStack(BottomNavigationActivity.class);
                    taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {*/
                    resultIntent = new Intent(this, MyPlaylistListingActivity.class);
                    resultIntent.putExtra("New", "0");
                    resultIntent.putExtra("Goplaylist", "1");
                    resultIntent.putExtra("PlaylistID", id);
                    resultIntent.putExtra("PlaylistName", title);
                    resultIntent.putExtra("notification", "0");
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("PlaylistImage", "");
                    taskStackBuilder.addParentStack(MyPlaylistListingActivity.class);
                    taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
//                }
            } else {
                resultIntent = new Intent(this, BottomNavigationActivity.class);
                taskStackBuilder.addParentStack(BottomNavigationActivity.class);
                taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Uri defaultSoundUri = Uri.parse("android.resource://"
                    + getApplicationContext().getPackageName() + "/" + R.raw.ringtone);
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            long[] v = {500, 1000};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.setLightColor(Color.GRAY);
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationChannel.setDescription("BWS Notification");
                notificationChannel.setVibrationPattern(v);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            }

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }

            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationBuilder.setSmallIcon(R.drawable.app_logo_transparent);
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL | Notification.FLAG_AUTO_CANCEL);
            notificationBuilder.setContentText(message);
            notificationBuilder.setColor(getResources().getColor(R.color.blue));
            notificationBuilder.setShowWhen(true);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setChannelId(channelId);
            notificationBuilder.setContentIntent(resultPendingIntent);

            Notification notification = notificationBuilder.build();

            if (notificationManager != null) {
                notificationManager.notify(Integer.parseInt(m), notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, e.getMessage() + channelId, Toast.LENGTH_SHORT).show();
        }
    }
}
