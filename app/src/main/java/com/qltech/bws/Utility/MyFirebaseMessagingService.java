package com.qltech.bws.Utility;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qltech.bws.DashboardModule.Activities.DashboardActivity;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.R;

import java.util.Random;

import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    private NotificationCompat.Builder notificationBuilder;
    String title = "", image = "", message = "", flag = "", id = "";
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

//        FirebaseMessaging.getInstance().subscribeToTopic("YupIT");

//        image = getBitmapFromURL(img);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        if (remoteMessage == null)
            return;
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
            String flag = remoteMessage.getNotification().getBody();
            String id = remoteMessage.getData().get("id");


            Log.e("bundle.....", "" + flag);
            sendNotification(title, message, flag, id, String.valueOf(m));
//                NotificationUtils notificationUtils = new NotificationUtils(this);
//                notificationUtils.playNotificationSound();
//            NotificationUtils.setNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), this);

        }
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                title = remoteMessage.getData().get("title");
                image = remoteMessage.getData().get("image");
                message = remoteMessage.getData().get("body");
                flag = remoteMessage.getData().get("flag");
                id = remoteMessage.getData().get("id");
                sendNotification(title, message, flag, id, String.valueOf(m));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
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
        Log.e(TAGs, "sendRegistrationToServer: " + token.toString());
    }

    private void sendNotification(String title, String message, String flag, String id, String m) {
        context = MyFirebaseMessagingService.this;
        activity = MyFirebaseMessagingService.this;
        taskStackBuilder = TaskStackBuilder.create(this);
        String channelId = context.getString(R.string.default_notification_channel_id);
        CharSequence channelName = "Name";
        int importance = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        try {
            if (flag != null && flag.equalsIgnoreCase("Reminder")) {
                /*Bundle bundle = new Bundle();
                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                bundle.putString("New", "0");
                bundle.putString("PlaylistID", PlaylistId);
                bundle.putString("PlaylistName", PlaylistName);
                bundle.putString("PlaylistImage", PlaylistImage);
                bundle.putString("MyDownloads", "1");
                myPlaylistsFragment.setArguments(bundle);
                comefrom_search = 3;
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, myPlaylistsFragment)
                        .commit();*/
                resultIntent = new Intent(this, DashboardActivity.class);
                /*resultIntent.putExtra(CONSTANTS.back_flag, CONSTANTS.FLAG_ONE);
                resultIntent.putExtra(CONSTANTS.refer_a_freind, CONSTANTS.FLAG_ONE);
                resultIntent.putExtra(CONSTANTS.notification, "0");
                resultIntent.putExtra(CONSTANTS.title, title);
                resultIntent.putExtra(CONSTANTS.message, message);*/
                taskStackBuilder.addParentStack(DashboardActivity.class);
                taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            } /*else if (flag != null && flag.equalsIgnoreCase(CONSTANTS.refer_restaurant)) {
                resultIntent = new Intent(this, ShareAndEarnActivity.class);
                resultIntent.putExtra(CONSTANTS.back_flag, CONSTANTS.FLAG_ONE);
                resultIntent.putExtra(CONSTANTS.refer_a_freind, CONSTANTS.FLAG_TWO);
                resultIntent.putExtra(CONSTANTS.notification, "0");
                resultIntent.putExtra(CONSTANTS.title, title);
                resultIntent.putExtra(CONSTANTS.message, message);
                taskStackBuilder.addParentStack(NavigationActivity.class);
                taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            }*/ else {
                resultIntent = new Intent(this, DashboardActivity.class);
//                resultIntent.putExtra(CONSTANTS.flag, flag);
//                resultIntent.putExtra(CONSTANTS.id, id);
                taskStackBuilder.addParentStack(DashboardActivity.class);
                taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("YupIt Notification");
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

            notificationBuilder.setSmallIcon(R.drawable.square_app_icon);
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setDefaults(Notification.FLAG_INSISTENT |
                    Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND |
                    Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL);

            notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationBuilder.setContentText(message);
            notificationBuilder.setColor(getResources().getColor(R.color.blue));
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setChannelId(channelId);
            notificationBuilder.setContentIntent(resultPendingIntent);

            Notification notification = notificationBuilder.build();
            notification.flags = Notification.FLAG_INSISTENT;
            notification.flags = Notification.DEFAULT_VIBRATE;
            notification.flags = Notification.DEFAULT_SOUND;
            notification.flags = Notification.DEFAULT_LIGHTS;
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            if (notificationManager != null) {
                notificationManager.notify(Integer.parseInt(m), notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage() + channelId, Toast.LENGTH_SHORT).show();
        }
    }
}
