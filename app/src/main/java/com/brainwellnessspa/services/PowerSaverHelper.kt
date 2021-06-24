package com.brainwellnessspa.services

import android.Manifest
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat

object PowerSaverHelper {
    fun getDozeState(context: Context): DozeState {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return DozeState.IRRELEVANT_OLD_ANDROID_API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return DozeState.UNKNOWN_TOO_OLD_ANDROID_API_FOR_CHECKING
        }
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (pm.isDeviceIdleMode) DozeState.DOZE_TURNED_ON_IDLE else if (pm.isInteractive) DozeState.NORMAL_INTERACTIVE else DozeState.NORMAL_NON_INTERACTIVE
    }

    fun getPowerSaveState(context: Context): PowerSaveState {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return PowerSaveState.IRRELEVANT_OLD_ANDROID_API
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (pm.isPowerSaveMode) PowerSaveState.ON else PowerSaveState.OFF
    }

    fun getIfAppIsWhiteListedFromBatteryOptimizations(context: Context, packageName: String): WhiteListedInBatteryOptimizations {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return WhiteListedInBatteryOptimizations.IRRELEVANT_OLD_ANDROID_API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return WhiteListedInBatteryOptimizations.UNKNOWN_TOO_OLD_ANDROID_API_FOR_CHECKING
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (pm.isIgnoringBatteryOptimizations(packageName)) WhiteListedInBatteryOptimizations.WHITE_LISTED else WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
    fun prepareIntentForWhiteListingOfBatteryOptimization(context: Context, packageName: String, alsoWhenWhiteListed: Boolean): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_DENIED) return null
        val appIsWhiteListedFromPowerSave = getIfAppIsWhiteListedFromBatteryOptimizations(context, packageName)
        var intent: Intent? = null
        when (appIsWhiteListedFromPowerSave) {
            WhiteListedInBatteryOptimizations.WHITE_LISTED -> if (alsoWhenWhiteListed) intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED -> intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$packageName"))
            WhiteListedInBatteryOptimizations.ERROR_GETTING_STATE, WhiteListedInBatteryOptimizations.UNKNOWN_TOO_OLD_ANDROID_API_FOR_CHECKING, WhiteListedInBatteryOptimizations.IRRELEVANT_OLD_ANDROID_API -> {
            }
            else -> {
            }
        }
        return intent
    }

    /**
     * registers a receiver to listen to power-save events. returns true iff succeeded to register the broadcastReceiver.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun registerPowerSaveReceiver(context: Context, receiver: BroadcastReceiver): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        val filter = IntentFilter()
        filter.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        context.registerReceiver(receiver, filter)
        return true
    }

    enum class PowerSaveState {
        ON, OFF, ERROR_GETTING_STATE, IRRELEVANT_OLD_ANDROID_API
    }

    enum class WhiteListedInBatteryOptimizations {
        WHITE_LISTED, NOT_WHITE_LISTED, ERROR_GETTING_STATE, UNKNOWN_TOO_OLD_ANDROID_API_FOR_CHECKING, IRRELEVANT_OLD_ANDROID_API
    }

    enum class DozeState {
        NORMAL_INTERACTIVE, DOZE_TURNED_ON_IDLE, NORMAL_NON_INTERACTIVE, ERROR_GETTING_STATE, IRRELEVANT_OLD_ANDROID_API, UNKNOWN_TOO_OLD_ANDROID_API_FOR_CHECKING
    }
}