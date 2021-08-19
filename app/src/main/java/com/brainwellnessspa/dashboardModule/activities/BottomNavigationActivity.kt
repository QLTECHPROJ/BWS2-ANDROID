package com.brainwellnessspa.dashboardModule.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.app.Service
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityBottomNavigationBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.MyBatteryReceiver
import com.brainwellnessspa.utility.MyNetworkReceiver
import com.brainwellnessspa.utility.UserActivityTrackModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.drax.netwatch.NetWatch
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator
import java.util.*

class BottomNavigationActivity : AppCompatActivity(), NetworkChangeReceiver_navigator {
    /* main dashboard bottom activity for all menu */
    lateinit var binding: ActivityBottomNavigationBinding
    var doubleBackToExitPressedOnce = false
    var backpressed = false
    var isFirst: String? = ""
    var userId: String? = ""
    var coUserId: String? = ""
    var userName: String? = ""
    var playlistID = ""
    var playlistName = ""
    var playlistImage = ""
    var playlistType = ""
    var new = ""
    lateinit var dialog: Dialog
    private var uiModeManager: UiModeManager? = null
    private var myNetworkReceiver: MyNetworkReceiver? = null
    private var myBatteryReceiver: MyBatteryReceiver? = null

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_Home, R.id.navigation_Manage, R.id.navigation_Wellness, R.id.navigation_Elevate, R.id.navigation_Profile).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Log.e("Nite Mode :", AppCompatDelegate.getDefaultNightMode().toString())
        }
        /* get user id and main account id*/
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        if (intent.extras != null) {
            isFirst = intent.getStringExtra("IsFirst")
        }
        val unicode = 0x2B05
        val textIcon = String(Character.toChars(unicode))

        if (isFirst.equals("1", ignoreCase = true)) {
            showToast("You're in, $userName!! \nLet's explore your path to inner peace!", this@BottomNavigationActivity)
        }

    }

    /* on Activity Result method use for battery optimization permission allow or deny*/

    override fun onResume() {
        uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager!!.nightMode == UiModeManager.MODE_NIGHT_AUTO || uiModeManager!!.nightMode == UiModeManager.MODE_NIGHT_YES || uiModeManager!!.nightMode == UiModeManager.MODE_NIGHT_CUSTOM) {
            uiModeManager!!.nightMode = UiModeManager.MODE_NIGHT_NO
            Log.e("Nite Mode :", uiModeManager!!.nightMode.toString())
        }
        /* register receiver for batttery state change */
        registerReceiver(MyBatteryReceiver().also { myBatteryReceiver = it }, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        /* register receiver for*/
        registerReceiver(MyNetworkReceiver().also { myNetworkReceiver = it }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        /* This condition use for battery optimization permission*/
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "com.brainwellnessspa::MyWakelockTag")
        /* Net Watcher for resume player when data connection again fetch after gone*/
        NetWatch.builder(this).setCallBack(object : NetworkChangeReceiver_navigator {
            override fun onConnected(source: Int) {
                // do some thing
                GlobalInitExoPlayer.callResumePlayer(this@BottomNavigationActivity)
                val shareded = getSharedPreferences(CONSTANTS.PREF_KEY_USER_ACTIVITY, Service.MODE_PRIVATE)
                val gson = Gson()
                val json = shareded.getString(CONSTANTS.PREF_KEY_USER_TRACK_ARRAY, gson.toString())
                var userActivityTrackModel = ArrayList<UserActivityTrackModel>()
                if (!json.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<UserActivityTrackModel?>?>() {}.type
                    userActivityTrackModel = gson.fromJson(json, type)
                    if (userActivityTrackModel.size != 0) {
                        val global = GlobalInitExoPlayer()
                        global.getUserActivityCall(this@BottomNavigationActivity, "", "", "")
                    }
                }
            }

            override fun onDisconnected(): View? {
                // do some other stuff
                return null //To display a dialog simply return a custom view or just null to ignore it
            }
        }).setNotificationCancelable(false).build()
        super.onResume()
    }

    override fun onDestroy() {
        NetWatch.unregister(this)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        GlobalInitExoPlayer.relesePlayer(this@BottomNavigationActivity)
        //        unregisterReceiver(myNetworkReceiver);
        deleteCache(this@BottomNavigationActivity)
        super.onDestroy()
    }

    override fun onConnected(source: Int) {
        GlobalInitExoPlayer.callResumePlayer(this@BottomNavigationActivity)
    }

    override fun onDisconnected(): View? {
        return null
    }

    /*  This function is use for  back press event handle */
    override fun onBackPressed() {
        if (isFirst.equals("1", ignoreCase = true)) {
            finishAffinity()
        }
        if (binding.navView.selectedItemId == R.id.navigation_Home) {
            binding.navView.selectedItemId = R.id.navigation_Home
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }
            doubleBackToExitPressedOnce = true
            showToast("Press again to exit", this@BottomNavigationActivity)
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        var audioClick = false
    }
}