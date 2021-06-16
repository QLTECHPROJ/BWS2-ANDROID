package com.brainwellnessspa.Services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import androidx.core.app.NotificationCompat

class MyIntentService : IntentService("MyIntentService") {
    private val notificationManager: NotificationManager? = null
    var builder: NotificationCompat.Builder? = null
    override fun onHandleIntent(intent: Intent?) {
        val extras = intent!!.extras
        // Do the work that requires your app to keep the CPU running.
        // ...
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        MyWakefulReceiver.completeWakefulIntent(intent)
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}