package com.brainwellnessspa.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            screenOff = true
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            screenOff = false
        }
    }

    companion object {
        var screenOff = true
    }
}