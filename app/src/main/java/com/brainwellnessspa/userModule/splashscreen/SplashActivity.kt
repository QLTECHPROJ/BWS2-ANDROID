package com.brainwellnessspa.userModule.splashscreen

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.enhance.MyPlaylistListingActivity
import com.brainwellnessspa.databinding.ActivitySplashBinding
import com.brainwellnessspa.membershipModule.activities.EnhanceActivity
import com.brainwellnessspa.membershipModule.activities.EnhanceDoneActivity
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.VersionModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.AppSignatureHashHelper
import com.brainwellnessspa.utility.CONSTANTS
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CTInboxStyleConfig
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.firebase.FirebaseIntegration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class SplashActivity : AppCompatActivity(), CTInboxListener, CTPushNotificationListener {
    lateinit var binding: ActivitySplashBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var mobileNo: String? = ""
    private var emailUser: String? = ""
    private var name: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var coUserCount: String? = ""
    var indexScore: String? = ""
    var scoreLevel: String? = ""
    var avgSleepTime: String? = ""
    var isPinSet: String? = ""
    var image: String? = ""
    var directLogin: String? = ""
    var isMainAccount: String? = ""
    var isSetLoginPin: String? = ""
    var planId: String? = ""
    var flag: String? = null
    var id: String? = null
    var title: String? = null
    var message: String? = null
    var IsLockNoti: String? = null
    var planContent: String? = ""
    lateinit var activity: Activity
    lateinit var context: Context

    /* TODO function for app started  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        context = this@SplashActivity
        activity = this@SplashActivity
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        key = appSignatureHashHelper.appSignatures[0]
        val sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE)
        val editor = sharedx.edit()
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.appSignatures[0])
        editor.apply()
        if (key.equals("")) {
            key = getKey(this)
        }

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context)
        val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        var fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        if (TextUtils.isEmpty(fcmId)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this@SplashActivity) { task: Task<InstallationTokenResult> ->
                val newToken = task.result.token
                Log.e("newToken", newToken)
                val editor = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit()
                editor.putString(CONSTANTS.Token, newToken) // Friend
                editor.apply()
            }
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        }
        clevertapDefaultInstance?.pushFcmRegistrationId(fcmId, true)
//        CleverTapAPI.getDefaultInstance(this@SplashActivity)?.pushNotificationViewedEvent(extras)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CleverTapAPI.createNotificationChannel(applicationContext, getString(R.string.default_notification_channel_id), "Brain Wellness App", "BWS Notification", NotificationManager.IMPORTANCE_MAX, true)
        }
        clevertapDefaultInstance?.apply {
            ctNotificationInboxListener = this@SplashActivity
            initializeInbox()
        }

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        name = shared.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        mobileNo = shared.getString(CONSTANTS.PREFE_ACCESS_MOBILE, "")
        indexScore = shared.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, "")
        scoreLevel = shared.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")
        image = shared.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        coUserCount = shared.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "")
        directLogin = shared.getString(CONSTANTS.PREFE_ACCESS_directLogin, "")
        isSetLoginPin = shared.getString(CONSTANTS.PREFE_ACCESS_isSetLoginPin, "")
        isPinSet = shared.getString(CONSTANTS.PREFE_ACCESS_isPinSet, "")
        planId = shared.getString(CONSTANTS.PREFE_ACCESS_PlanId, "")
        isMainAccount = shared.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
    }

    override fun onResume() {
        if (userId.equals("")) {
            checkAppVersion()
        } else {
            checkUserDetails()
        }
        super.onResume()
    }

    /* TODO function for battery permission result  */
    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             if (requestCode == 15695) {
                 val pm = getSystemService(POWER_SERVICE) as PowerManager
                 val isIgnoringBatteryOptimizations: Boolean = pm.isIgnoringBatteryOptimizations(packageName)
                 if (isIgnoringBatteryOptimizations) { // Ignoring battery optimization
                     callDashboard()
                 } else { // Not ignoring battery optimization
                     callDashboard()
                 }
             }
         }
         super.onActivityResult(requestCode, resultCode, data)
     }*/

    /* TODO function for check app version  */
    private fun checkAppVersion() {
        val appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa"
        if (isNetworkConnected(this)) {
            val listCall: Call<VersionModel> = APINewClient.client.getAppVersions(BuildConfig.VERSION_CODE.toString(), CONSTANTS.FLAG_ONE)
            listCall.enqueue(object : Callback<VersionModel> {
                override fun onResponse(call: Call<VersionModel>, response: Response<VersionModel>) {
                    try {
                        val versionModel: VersionModel = response.body()!!
                        try {
                            setAnalytics(versionModel.ResponseData.segmentKey, context)

                            when {
                                versionModel.ResponseData.IsForce == "0" -> {
                                    val builder = AlertDialog.Builder(this@SplashActivity)
                                    builder.setTitle("Update Brain Wellness App")
                                    builder.setCancelable(false)
                                    builder.setMessage("Brain Wellness App recommends that you update to the latest version").setPositiveButton("UPDATE") { dialog: DialogInterface, _: Int ->
                                        this@SplashActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
                                        dialog.cancel()
                                    }.setNegativeButton("NOT NOW") { dialog: DialogInterface, _: Int ->
                                        //                                        askBattyPermission()
                                        callDashboard()
                                        dialog.dismiss()
                                    }
                                    builder.create().show()
                                }
                                versionModel.ResponseData.IsForce == "1" -> {
                                    val builder = AlertDialog.Builder(this@SplashActivity)
                                    builder.setTitle("Update Required")
                                    builder.setCancelable(false)
                                    builder.setMessage("To keep using Brain Wellness App, download the latest version").setCancelable(false).setPositiveButton("UPDATE") { _: DialogInterface?, _: Int ->
                                        this@SplashActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
                                    }
                                    builder.create().show()
                                }
                                versionModel.ResponseData.IsForce == "" -> {
//                                    askBattyPermission()
                                    callDashboard()
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<VersionModel>, t: Throwable) {
                }
            })
        }
    }

    /* TODO function for check user details  */
    private fun checkUserDetails() {
        if (isNetworkConnected(this)) {
            val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<AuthOtpModel> {
                override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                    try {
                        val authOtpModel: AuthOtpModel = response.body()!!
                        if (authOtpModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess))) {

                            IsLock = authOtpModel.ResponseData.Islock
                            isProfileCompleted = authOtpModel.ResponseData.isProfileCompleted
                            isAssessmentCompleted = authOtpModel.ResponseData.isAssessmentCompleted
                            indexScore = authOtpModel.ResponseData.indexScore
                            avgSleepTime = authOtpModel.ResponseData.AvgSleepTime
                            isPinSet = authOtpModel.ResponseData.isPinSet
                            coUserCount = authOtpModel.ResponseData.CoUserCount
                            directLogin = authOtpModel.ResponseData.directLogin
                            isMainAccount = authOtpModel.ResponseData.isMainAccount
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, authOtpModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, authOtpModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, authOtpModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, authOtpModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, authOtpModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, authOtpModel.ResponseData.CountryCode)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, authOtpModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, authOtpModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, authOtpModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, authOtpModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, authOtpModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, authOtpModel.ResponseData.isAssessmentCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, authOtpModel.ResponseData.directLogin)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, authOtpModel.ResponseData.isPinSet)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, authOtpModel.ResponseData.isEmailVerified)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, authOtpModel.ResponseData.isMainAccount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, authOtpModel.ResponseData.CoUserCount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, authOtpModel.ResponseData.IsInCouser)
                            try {
                                if (authOtpModel.ResponseData.planDetails.isNotEmpty()) {
                                    planId = authOtpModel.ResponseData.planDetails[0].PlanId
                                    planContent = authOtpModel.ResponseData.planDetails[0].PlanContent
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, authOtpModel.ResponseData.planDetails[0].PlanId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, authOtpModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, authOtpModel.ResponseData.planDetails[0].PlanExpireDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, authOtpModel.ResponseData.planDetails[0].TransactionId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, authOtpModel.ResponseData.planDetails[0].TrialPeriodStart)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, authOtpModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, authOtpModel.ResponseData.planDetails[0].PlanStatus)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, authOtpModel.ResponseData.planDetails[0].PlanContent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            editor.apply()
                            val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val edited = shred.edit()
                            edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, authOtpModel.ResponseData.AvgSleepTime)
                            val selectedCategoriesTitle = arrayListOf<String>()
                            val selectedCategoriesName = arrayListOf<String>()
                            val gson = Gson()
                            for (i in authOtpModel.ResponseData.AreaOfFocus) {
                                selectedCategoriesTitle.add(i.MainCat)
                                selectedCategoriesName.add(i.RecommendedCat)
                            }
                            edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                            edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                            edited.apply()
                            checkAppVersion()
                        } else if (authOtpModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted))) {
                            deleteCall(activity)
                            showToast(authOtpModel.ResponseMessage, activity)
                            val i = Intent(activity, SignInActivity::class.java)
                            i.putExtra("mobileNo", "")
                            i.putExtra("countryCode", "")
                            i.putExtra("name", "")
                            i.putExtra("email", "")
                            i.putExtra("countryShortName", "")
                            startActivity(i)
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                }
            })
        } else {
            setAnalytics(getString(R.string.segment_key_real_2_staging), context)
//            askBattyPermission()
            callDashboard()
            showToast(getString(R.string.no_server_found), this@SplashActivity)
        }
    }

    /* TODO function for battery permission  */
    /* @SuppressLint("BatteryLife")
     private fun askBattyPermission() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             val packageName = packageName
             val pm = getSystemService(POWER_SERVICE) as PowerManager
             val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
             if (!isIgnoringBatteryOptimizations) {
                 val intent = Intent()
                 intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                 intent.data = Uri.parse("package:$packageName")
                 startActivityForResult(intent, 15695)
             } else {
                 callDashboard()
             }
         } else {
             callDashboard()
         }
     }*/

    private fun callDashboard() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (userId.equals("")) {
                val i = Intent(this@SplashActivity, SignInActivity::class.java)
                i.putExtra("mobileNo", "")
                i.putExtra("countryCode", "")
                i.putExtra("name", "")
                i.putExtra("email", "")
                i.putExtra("countryShortName", "")
                startActivity(i)
                finish()
            } else if (intent.hasExtra("flag")) {
                val resultIntent: Intent?
                flag = intent.getStringExtra("flag");
                id = intent.getStringExtra("id");
                title = intent.getStringExtra("title");
                message = intent.getStringExtra("message");
                IsLockNoti = intent.getStringExtra("IsLock");
                if (flag != null && flag.equals("Playlist")) {
                    if (!IsLockNoti.equals("0")) {
                        resultIntent = Intent(this, BottomNavigationActivity::class.java)
                        resultIntent.putExtra("IsFirst", "0")
                        startActivity(resultIntent)
                        finish()
                    } else {
                        resultIntent = Intent(this, MyPlaylistListingActivity::class.java)
                        resultIntent.putExtra("New", "0")
                        resultIntent.putExtra("Goplaylist", "1")
                        resultIntent.putExtra("PlaylistID", id)
                        resultIntent.putExtra("PlaylistName", title)
                        resultIntent.putExtra("notification", "0")
                        resultIntent.putExtra("message", message)
                        resultIntent.putExtra("PlaylistImage", "")
                        startActivity(resultIntent)
                        finish()
                    }
                }
            } else {
                Log.e("isMainAccount", isMainAccount.toString())
                Log.e("isProfileCompleted", isProfileCompleted.toString())
                Log.e("isAssessmentCompleted", isAssessmentCompleted.toString())
                Log.e("WellnessScore", indexScore.toString())
                Log.e("avgSleepTime", avgSleepTime.toString())
                Log.e("coUserCount", coUserCount.toString())
                Log.e("isSetLoginPin", isSetLoginPin.toString())
                if (isMainAccount.equals("1")) {
                    if (isAssessmentCompleted.equals("0")) {
                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                        intent.putExtra("Navigation", "Enhance")
                        startActivity(intent)
                        finish()
                    } else if (planId.equals("")) {
                        val intent = Intent(applicationContext, EnhanceActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(intent)
                        finish()
                    } else if (isPinSet.equals("1")) {
                        if (isSetLoginPin.equals("1")) {
                            when {
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                planId.equals("") -> {
                                    val intent = Intent(applicationContext, EnhanceActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            when {
                                coUserCount.toString() > "0" -> {
                                    val intent = Intent(activity, UserListActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                planId.equals("") -> {
                                    val intent = Intent(applicationContext, EnhanceActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    } else if (isPinSet.equals("0") || isPinSet.equals("")) {
                        if (isSetLoginPin.equals("1")) {
                            when {
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            if (coUserCount.toString() > "0") {
                                val intent = Intent(activity, UserListActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(activity, BottomNavigationActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                intent.putExtra("IsFirst", "0")
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    if (isAssessmentCompleted.equals("0")) {
                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                        intent.putExtra("Navigation", "Enhance")
                        startActivity(intent)
                        finish()
                    } else if (isPinSet.equals("1")) {
                        if (isSetLoginPin.equals("1")) {
                            when {
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            when {
                                coUserCount.toString() > "0" -> {
                                    val intent = Intent(activity, UserListActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    } else if (isPinSet.equals("0") || isPinSet.equals("")) {
                        val intent = Intent(applicationContext, EnhanceDoneActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }, (2 * 800).toLong())
    }

    /* TODO function for segment analytics  */
    fun setAnalytics(segmentKey: String, context: Context) {
        try {
            analytics = Analytics.Builder(context, segmentKey).use(FirebaseIntegration.FACTORY).trackApplicationLifecycleEvents().logLevel(Analytics.LogLevel.VERBOSE).trackAttributionInformation().trackAttributionInformation().trackDeepLinks().collectDeviceId(true).build()/*.use(FirebaseIntegration.FACTORY) */
            Analytics.setSingletonInstance(analytics)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun inboxDidInitialize() {
        var cleverTapAPI: CleverTapAPI? = null
        cleverTapAPI = CleverTapAPI.getDefaultInstance(this@SplashActivity)
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