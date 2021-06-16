package com.brainwellnessspa.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import static com.brainwellnessspa.BWSApplication.BatteryStatus;

public class MyBatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
        if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){
            BatteryStatus = "Charging";
        }
        if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){
            BatteryStatus = "Discharging";
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){
            BatteryStatus = "Battery Full";
        }
        if(deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){
            BatteryStatus = "Unknown";
        }
        if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
            BatteryStatus = "Not Charging";
        }
    }
}