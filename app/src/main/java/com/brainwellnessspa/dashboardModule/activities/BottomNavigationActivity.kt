package com.brainwellnessspa.dashboardModule.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.*
import android.text.TextUtils
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
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CTInboxStyleConfig
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.drax.netwatch.NetWatch
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator
import java.util.*

class BottomNavigationActivity : AppCompatActivity(), NetworkChangeReceiver_navigator, CTInboxListener, CTPushNotificationListener {
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
    lateinit var ctx: Context
    lateinit var act: Activity
    lateinit var dialog: Dialog
    private var uiModeManager: UiModeManager? = null
    private var myNetworkReceiver: MyNetworkReceiver? = null
    private var myBatteryReceiver: MyBatteryReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navigation)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_Home, R.id.navigation_Manage, R.id.navigation_Wellness, R.id.navigation_Elevate, R.id.navigation_Profile).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        act = this@BottomNavigationActivity
        ctx = this@BottomNavigationActivity

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Log.e("Nite Mode :", AppCompatDelegate.getDefaultNightMode().toString())
        }
        val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        var fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        if (TextUtils.isEmpty(fcmId)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this@BottomNavigationActivity) { task: Task<InstallationTokenResult> ->
                val newToken = task.result.token
                Log.e("newToken", newToken)
                val editor = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit()
                editor.putString(CONSTANTS.Token, newToken) // Friend
                editor.apply()
            }
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        }
        clevertapDefaultInstance!!.pushFcmRegistrationId(fcmId, true)
        //        CleverTapAPI.getDefaultInstance(this@SplashActivity)?.pushNotificationViewedEvent(extras)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CleverTapAPI.createNotificationChannel(applicationContext, getString(R.string.default_notification_channel_id), "Brain Wellness App", "BWS Notification", NotificationManager.IMPORTANCE_MAX, true)
        }
        clevertapDefaultInstance.apply {
            this!!.ctNotificationInboxListener = this@BottomNavigationActivity
            initializeInbox()
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

        if (isFirst.equals("1")) {
            showToast("You're in, $userName!! \nLet's explore your path to inner peace!", act)
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
                GlobalInitExoPlayer.callResumePlayer(ctx)
                val shareded = getSharedPreferences(CONSTANTS.PREF_KEY_USER_ACTIVITY, Service.MODE_PRIVATE)
                val gson = Gson()
                val json = shareded.getString(CONSTANTS.PREF_KEY_USER_TRACK_ARRAY, gson.toString())
                var userActivityTrackModel = ArrayList<UserActivityTrackModel>()
                if (!json.equals(gson.toString())) {
                    val type = object : TypeToken<ArrayList<UserActivityTrackModel?>?>() {}.type
                    userActivityTrackModel = gson.fromJson(json, type)
                    if (userActivityTrackModel.size != 0) {
                        val global = GlobalInitExoPlayer()
                        global.getUserActivityCall(ctx, "", "", "")
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
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(notificationId)
        GlobalInitExoPlayer.relesePlayer(ctx)
        //        unregisterReceiver(myNetworkReceiver);
        deleteCache(ctx)
        super.onDestroy()
    }

    override fun onConnected(source: Int) {
        GlobalInitExoPlayer.callResumePlayer(ctx)
    }

    override fun onDisconnected(): View? {
        return null
    }

    /*  This function is use for  back press event handle */
    override fun onBackPressed() {
        if (isFirst.equals("1")) {
            finishAffinity()
        }
        if (binding.navView.selectedItemId == R.id.navigation_Home) {
            binding.navView.selectedItemId = R.id.navigation_Home
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }
            doubleBackToExitPressedOnce = true
            showToast("Press again to exit", act)
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else if (binding.navView.selectedItemId == R.id.navigation_Manage) {
            binding.navView.selectedItemId = R.id.navigation_Home
        } else if (binding.navView.selectedItemId == R.id.navigation_Wellness) {
            binding.navView.selectedItemId = R.id.navigation_Home
        } else if (binding.navView.selectedItemId == R.id.navigation_Elevate) {
            binding.navView.selectedItemId = R.id.navigation_Home
        } else if (binding.navView.selectedItemId == R.id.navigation_Profile) {
            binding.navView.selectedItemId = R.id.navigation_Home
        } else {
            super.onBackPressed()
        }
    }
    override fun inboxDidInitialize() {
        var cleverTapAPI: CleverTapAPI? = null
        cleverTapAPI = CleverTapAPI.getDefaultInstance(this@BottomNavigationActivity)
        val inboxTabs =
            arrayListOf("Promotions", "Offers", "Others")//Anything after the first 2 will be ignored
        CTInboxStyleConfig().apply {
            tabs = inboxTabs //Do not use this if you don't want to use tabs
            tabBackgroundColor = "#FF0000"
            selectedTabIndicatorColor = "#0000FF"
            selectedTabColor = "#000000"
            unselectedTabColor = "#FFFFFF"
            backButtonColor = "#FF0000"
            navBarTitleColor = "#FF0000"
            navBarTitle = "MY INBOX"
            navBarColor = "#FFFFFF"
            inboxBackgroundColor = "#00FF00"
            firstTabTitle = "First Tab"
            cleverTapAPI?.showAppInbox(this) //Opens activity With Tabs

        }
        //OR
        cleverTapAPI!!.showAppInbox()//Opens Activity with default style config
    }

    override fun inboxMessagesDidUpdate() {

    }

    override fun onNotificationClickedPayloadReceived(payload: HashMap<String, Any>?) {

    }
}