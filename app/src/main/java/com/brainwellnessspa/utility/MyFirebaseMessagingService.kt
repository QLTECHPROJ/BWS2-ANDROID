package com.brainwellnessspa.utility

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.enhance.MyPlaylistListingActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.segment.analytics.Properties
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: NotificationCompat.Builder
    var title: String? = ""
    var image: String? = ""
    var message: String? = ""
    var flag: String? = ""
    var id: String? = ""
    var isLock: String? = ""
    private lateinit var taskStackBuilder: TaskStackBuilder
    lateinit var resultPendingIntent: PendingIntent
    lateinit var resultIntent: Intent
    lateinit var context: Context
    lateinit var activity: MyFirebaseMessagingService
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
//        String tag = remoteMessage.getData().get("tag");

//        FirebaseMessaging.getInstance().subscribeToTopic("BWS");

//        image = getBitmapFromURL(img);
        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification rendom number: $m")
            title = remoteMessage.notification!!.title
            message = remoteMessage.notification!!.body
            val flag = remoteMessage.notification!!.body
            val id = remoteMessage.data["id"]
            val isLock = remoteMessage.data["IsLock"]
            sendNotification(title, message, flag, id, m.toString(), isLock)
        }
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            try {
                title = remoteMessage.data["title"]
                image = remoteMessage.data["image"]
                message = remoteMessage.data["body"]
                flag = remoteMessage.data["flag"]
                id = remoteMessage.data["id"]
                isLock = remoteMessage.data["IsLock"]
                sendNotification(title, message, flag, id, m.toString(), isLock)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }
        }
        val p = Properties()
        /*    AnalyticsContext.Campaign campaign = new AnalyticsContext.Campaign();
        campaign.putValue("id", id);
        campaign.putName(title);
        campaign.putContent(message);
        campaign.putMedium("Push");
        campaign.putSource("Admin");
        p.putValue("campaign", campaign);*/
        val shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val UserID = shared2.getString(CONSTANTS.PREF_KEY_UserID, "")
        p.putValue("userId", UserID)
        p.putValue("playlistId", id)
        p.putName(title)
        p.putValue("message", message)
        BWSApplication.addToSegment("Push Notification Received", p, CONSTANTS.track)
    }

    override fun onNewToken(token: String) {
        var token = token
        super.onNewToken(token)
        val registrationComplete = Intent(CONSTANTS.REGISTRATION_COMPLETE)
        registrationComplete.putExtra("token", token)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
        if (token.isEmpty()) {
            token = FirebaseInstanceId.getInstance().token!!
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        val editor1 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
        editor1.putString(CONSTANTS.Token, token) //Friend
        editor1.apply()
        editor1.commit()
        fcm_Tocken = token
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val editor1 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
        editor1.putString(CONSTANTS.Token, token) //Friend
        editor1.apply()
        editor1.commit()
        Log.e(TAGs, "sendRegistrationToServer: $token")
    }

    private fun sendNotification(title: String?, message: String?, flag: String?, id: String?, m: String, IsLock: String?) {
        context = this@MyFirebaseMessagingService
        activity = this@MyFirebaseMessagingService
        taskStackBuilder = TaskStackBuilder.create(this)
        val channelId = context.getString(R.string.default_notification_channel_id)
        val requestID = System.currentTimeMillis().toInt()
        val channelName: CharSequence = "Brain Wellness App"
        var importance = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH
        }
        try {
            if (flag != null && flag.equals("Playlist", ignoreCase = true)) {
                /* if (!IsLock.equalsIgnoreCase("0")) {
                    resultIntent = new Intent(this, BottomNavigationActivity.class);
                    taskStackBuilder.addParentStack(BottomNavigationActivity.class);
                    taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                    resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {*/
                resultIntent = Intent(this, MyPlaylistListingActivity::class.java)
                resultIntent.putExtra("New", "0")
                resultIntent.putExtra("Goplaylist", "1")
                resultIntent.putExtra("PlaylistID", id)
                resultIntent.putExtra("PlaylistName", title)
                resultIntent.putExtra("notification", "0")
                resultIntent.putExtra("message", message)
                resultIntent.putExtra("PlaylistImage", "")
                taskStackBuilder.addParentStack(MyPlaylistListingActivity::class.java)
                taskStackBuilder.addNextIntentWithParentStack(resultIntent)
                resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT)
                //                }
            } else {
                resultIntent = Intent(this, BottomNavigationActivity::class.java)
                resultIntent.putExtra("IsFirst", "0")
                taskStackBuilder.addParentStack(BottomNavigationActivity::class.java)
                taskStackBuilder.addNextIntentWithParentStack(resultIntent)
                resultPendingIntent = taskStackBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val defaultSoundUri = Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.ringtone)
            //            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val v = longArrayOf(500, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, channelName, importance)
                notificationChannel.lightColor = Color.GRAY
                notificationChannel.enableVibration(true)
                notificationChannel.enableLights(true)
                notificationChannel.description = "BWS Notification"
                notificationChannel.vibrationPattern = v
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationBuilder = NotificationCompat.Builder(this, notificationChannel.id)
            } else {
                notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notificationBuilder.setSmallIcon(R.drawable.app_new_icon)
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            notificationBuilder.setContentTitle(title)
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL or Notification.FLAG_AUTO_CANCEL)
            notificationBuilder.setContentText(message)
            notificationBuilder.color = ContextCompat.getColor(activity, R.color.blue)
            notificationBuilder.setShowWhen(true)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setChannelId(channelId)
            notificationBuilder.setContentIntent(resultPendingIntent)
            val notification = notificationBuilder.build()
            notificationManager.notify(m.toInt(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
            //            Toast.makeText(context, e.getMessage() + channelId, Toast.LENGTH_SHORT).show();
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val TAGs = "MyFirebaseIDService"
        var fcm_Tocken: String? = null
    }
}