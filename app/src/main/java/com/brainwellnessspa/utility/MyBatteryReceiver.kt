package com.brainwellnessspa.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.brainwellnessspa.BWSApplication

class MyBatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            BWSApplication.BatteryStatus = "Charging"
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            BWSApplication.BatteryStatus = "Discharging"
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL) {
            BWSApplication.BatteryStatus = "Battery Full"
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            BWSApplication.BatteryStatus = "Unknown"
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            BWSApplication.BatteryStatus = "Not Charging"
        }
    }
}