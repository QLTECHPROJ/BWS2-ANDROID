package com.brainwellnessspa.Services

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import com.brainwellnessspa.Services.MyIntentService

class MyWakefulReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        val service = Intent(context, MyIntentService::class.java)
        startWakefulService(context, service)
    }
}