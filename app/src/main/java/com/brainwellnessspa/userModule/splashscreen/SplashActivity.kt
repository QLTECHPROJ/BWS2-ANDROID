 package com.brainwellnessspa.userModule.splashscreen

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.R
import com.brainwellnessspa.areaOfFocusModule.activities.SleepTimeActivity
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.enhance.MyPlaylistListingActivity
import com.brainwellnessspa.databinding.ActivitySplashBinding
import com.brainwellnessspa.membershipModule.activities.EnhanceDoneActivity
import com.brainwellnessspa.membershipModule.activities.MembershipActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.VersionModel
import com.brainwellnessspa.userModule.signupLogin.SignUpActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.AppSignatureHashHelper
import com.brainwellnessspa.utility.AppUtils
import com.brainwellnessspa.utility.CONSTANTS
//import com.clevertap.android.sdk.CTInboxListener
//import com.clevertap.android.sdk.CTInboxStyleConfig
//import com.clevertap.android.sdk.CleverTapAPI
//import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.firebase.FirebaseIntegration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity()/*, CTInboxListener, CTPushNotificationListener */{
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
    var IsLoginFirstTime: String? = ""
    var isSetLoginPin: String? = ""
    var isInCouser: String? = ""
    var timezoneName: String? = ""
    var planId: String? = ""
    var paymentType: String? = ""
    var flag: String? = ""
    var id: String? = ""
    var title: String? = ""
    var message: String? = ""
    var IsLockNoti: String? = ""
    var planContent: String? = ""
    lateinit var activity: Activity
    lateinit var context: Context
    override fun onStart() {
        super.onStart()
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if(deepLink != null){
                    var screen = deepLink.getQueryParameter("screen")
                    showToast("Hiiiiiii  "+ screen,activity)
                    if(screen.equals("splash")){

                    }else if(screen.equals("setreminder")){

                    }else if(screen.equals("reassessment")){

                    }else if(screen.equals("invite")){

                    }else if(screen.equals("signup")){

                    }else if(screen.equals("updatesubscription")){

                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.e("DeeeeeeepLinkkkkk", "getDynamicLink:onFailure    " +  e.toString()) }
    }
    /* TODO function for app started  */
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
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
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {/* TODO conditions for check user exist or not */
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
        IsLoginFirstTime = shared.getString(CONSTANTS.PREFE_ACCESS_IsLoginFirstTime, "")
        isInCouser = shared.getString(CONSTANTS.PREFE_ACCESS_isInCouser, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        Log.e("DeviceID", Settings.Secure.getString(getContext().contentResolver, Settings.Secure.ANDROID_ID))
//        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context)
        val sharedPreferences2 = getSharedPreferences(CONSTANTS.FCMToken, MODE_PRIVATE)
        var fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        Log.e("token", fcmId.toString())
//        clevertapDefaultInstance?.pushFcmRegistrationId(fcmId, true)
        //        CleverTapAPI.getDefaultInstance(this@SplashActivity)?.pushNotificationViewedEvent(extras)
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CleverTapAPI.createNotificationChannel(applicationContext, getString(R.string.default_notification_channel_id), "Brain Wellness App", "BWS Notification", NotificationManager.IMPORTANCE_HIGH, true)
        }
        clevertapDefaultInstance?.apply {
            ctNotificationInboxListener = this@SplashActivity
            initializeInbox()
        }*/
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
        val simpleDateFormat1 = SimpleDateFormat("hh:mm a")
        simpleDateFormat1.timeZone = TimeZone.getDefault()
        timezoneName = simpleDateFormat1.timeZone.id
        val appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa"
        if (isNetworkConnected(this)) {
            val listCall: Call<VersionModel> = APINewClient.client.getAppVersions(coUserId, BuildConfig.VERSION_CODE.toString(), CONSTANTS.FLAG_ONE, timezoneName)
            listCall.enqueue(object : Callback<VersionModel> {
                override fun onResponse(call: Call<VersionModel>, response: Response<VersionModel>) {
                    try {
                        val versionModel: VersionModel = response.body()!!
                        try {
                            callFCMRegMethod(context)
                            FirebaseMessaging.getInstance().isAutoInitEnabled = true
                            val shared1 = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor1 = shared1.edit()
                            editor1.putString(CONSTANTS.PREFE_ACCESS_segmentKey, versionModel.ResponseData.segmentKey)
                            editor1.apply()
                            setAnalytics(versionModel.ResponseData.segmentKey, context)
                            val shared = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_supportTitle, versionModel.ResponseData.supportTitle)
                            editor.putString(CONSTANTS.PREFE_ACCESS_supportText, versionModel.ResponseData.supportText)
                            editor.putString(CONSTANTS.PREFE_ACCESS_supportEmail, versionModel.ResponseData.supportEmail)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IsLoginFirstTime, versionModel.ResponseData.IsLoginFirstTime)
                            editor.apply()
                            when {
                                versionModel.ResponseData.IsForce == "0" -> {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("Update Brain Wellness App")
                                    builder.setCancelable(false)
                                    builder.setMessage("Brain Wellness App recommends that you update to the latest version").setPositiveButton("UPDATE") { dialog: DialogInterface, _: Int ->
                                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
                                        dialog.cancel()
                                    }.setNegativeButton("NOT NOW") { dialog: DialogInterface, _: Int ->
                                        //                                        askBattyPermission()
                                        callDashboard()
                                        dialog.dismiss()
                                    }
                                    builder.create().show()
                                }
                                versionModel.ResponseData.IsForce == "1" -> {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("Update Required")
                                    builder.setCancelable(false)
                                    builder.setMessage("To keep using Brain Wellness App, download the latest version").setCancelable(false).setPositiveButton("UPDATE") { _: DialogInterface?, _: Int ->
                                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
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
                        val listModel: AuthOtpModel = response.body()!!
                        if (listModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess))) {

                            IsLock = listModel.ResponseData.Islock
                            isProfileCompleted = listModel.ResponseData.isProfileCompleted
                            isAssessmentCompleted = listModel.ResponseData.isAssessmentCompleted
                            indexScore = listModel.ResponseData.indexScore
                            avgSleepTime = listModel.ResponseData.AvgSleepTime
                            isPinSet = listModel.ResponseData.isPinSet
                            coUserCount = listModel.ResponseData.CoUserCount
                            directLogin = listModel.ResponseData.directLogin
                            isMainAccount = listModel.ResponseData.isMainAccount
                            isInCouser = listModel.ResponseData.IsInCouser
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, listModel.ResponseData.CountryCode)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, listModel.ResponseData.directLogin)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, listModel.ResponseData.isPinSet)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, listModel.ResponseData.isEmailVerified)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, listModel.ResponseData.isMainAccount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, listModel.ResponseData.CoUserCount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, listModel.ResponseData.IsInCouser)
                            editor.putString(CONSTANTS.PREFE_ACCESS_paymentType, listModel.ResponseData.paymentType)
                            paymentType = listModel.ResponseData.paymentType
                            if (listModel.ResponseData.planDetails.isEmpty() && listModel.ResponseData.oldPaymentDetails.isEmpty()) {
                                planId = ""
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStr, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_OrderTotal, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_CardId, "")
                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, "")
                            } else {
                                if (listModel.ResponseData.paymentType == "0") {
                                    // Stripe
                                    try {
                                        if (listModel.ResponseData.oldPaymentDetails.isNotEmpty()) {
                                            planId = listModel.ResponseData.oldPaymentDetails[0].PlanId
                                            planContent = listModel.ResponseData.oldPaymentDetails[0].PlanContent
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.oldPaymentDetails[0].PlanId)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.oldPaymentDetails[0].purchaseDate)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.oldPaymentDetails[0].expireDate)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "")
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "")
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanStr, listModel.ResponseData.oldPaymentDetails[0].PlanStr)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_OrderTotal, listModel.ResponseData.oldPaymentDetails[0].OrderTotal)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.oldPaymentDetails[0].PlanStatus)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_CardId, listModel.ResponseData.oldPaymentDetails[0].CardId)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.oldPaymentDetails[0].PlanContent)

                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else if (listModel.ResponseData.paymentType == "1") {
                                    // IAP
                                    try {
                                        if (listModel.ResponseData.planDetails.isNotEmpty()) {
                                            planId = listModel.ResponseData.planDetails[0].PlanId
                                            planContent = listModel.ResponseData.planDetails[0].PlanContent
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.planDetails[0].PlanId)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.planDetails[0].PlanExpireDate)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, listModel.ResponseData.planDetails[0].TransactionId)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, listModel.ResponseData.planDetails[0].TrialPeriodStart)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, listModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.planDetails[0].PlanStatus)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.planDetails[0].PlanContent)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            editor.apply()

                            val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val edited = shred.edit()
                            edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            val selectedCategoriesTitle = arrayListOf<String>()
                            val selectedCategoriesName = arrayListOf<String>()
                            val gson = Gson()
                            for (i in listModel.ResponseData.AreaOfFocus) {
                                selectedCategoriesTitle.add(i.MainCat)
                                selectedCategoriesName.add(i.RecommendedCat)
                            }
                            edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                            edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                            edited.apply()

                            val sharedded = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            userId = sharedded.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
                            coUserId = sharedded.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
                            emailUser = sharedded.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
                            name = sharedded.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
                            mobileNo = sharedded.getString(CONSTANTS.PREFE_ACCESS_MOBILE, "")
                            indexScore = sharedded.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, "")
                            scoreLevel = sharedded.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")
                            image = sharedded.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
                            isProfileCompleted = sharedded.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
                            isAssessmentCompleted = sharedded.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
                            coUserCount = sharedded.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "")
                            directLogin = sharedded.getString(CONSTANTS.PREFE_ACCESS_directLogin, "")
                            isSetLoginPin = sharedded.getString(CONSTANTS.PREFE_ACCESS_isSetLoginPin, "")
                            isPinSet = sharedded.getString(CONSTANTS.PREFE_ACCESS_isPinSet, "")
                            planId = sharedded.getString(CONSTANTS.PREFE_ACCESS_PlanId, "")
                            isMainAccount = sharedded.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "")
                            isInCouser = sharedded.getString(CONSTANTS.PREFE_ACCESS_isInCouser, "")
                            val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                            checkAppVersion()
                        } else if (listModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted))) {
                            callDelete403(activity, listModel.ResponseMessage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                }
            })
        } else {
            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
            var segmentKey = shared.getString(CONSTANTS.PREFE_ACCESS_segmentKey, "")
            if (segmentKey == "") {
                if (AppUtils.New_BASE_URL == "https://brainwellnessapp.com.au/bwsapi/api/staging/v2/") {
                    segmentKey = getString(R.string.segment_key_real_2_staging)
                } else {
                    segmentKey = getString(R.string.segment_key_real_2_live)
                }
            }
            setAnalytics(segmentKey!!, context)
            //            askBattyPermission()
            callDashboard()
            showToast(getString(R.string.no_server_found), activity)
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
            /*if (IsLoginFirstTime.equals("1")) {
                val i = Intent(activity, SignUpActivity::class.java)
                i.putExtra("mobileNo", "")
                i.putExtra("countryCode", "")
                i.putExtra("name", "")
                i.putExtra("email", "")
                i.putExtra("countryShortName", "")
                startActivity(i)
                finish()
            } else */
            if (userId.equals("")) {
                callSignActivity(activity)
            } else if (intent.hasExtra("flag")) {
                val resultIntent: Intent?
                flag = intent.getStringExtra("flag");
                id = intent.getStringExtra("id");
                title = intent.getStringExtra("title");
                message = intent.getStringExtra("message")
                IsLockNoti = intent.getStringExtra("IsLock")
                val requestID = System.currentTimeMillis().toInt()
                if (flag != null && flag.equals("Playlist")) {
                    if (!IsLockNoti.equals("0")) {
                        NotificationPlaylistCheck = "1"
                        resultIntent = Intent(this, BottomNavigationActivity::class.java)
                        resultIntent.putExtra("IsFirst", "0")
                        startActivity(resultIntent)
                    } else {
                        NotificationPlaylistCheck = "1"
                        resultIntent = Intent(this, MyPlaylistListingActivity::class.java)
                        resultIntent.putExtra("New", "0")
                        resultIntent.putExtra("Goplaylist", "1")
                        resultIntent.putExtra("PlaylistID", id)
                        resultIntent.putExtra("PlaylistName", title)
                        resultIntent.putExtra("notification", "0")
                        resultIntent.putExtra("message", message)
                        resultIntent.putExtra("PlaylistImage", "")
                        startActivity(resultIntent)
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
                isInCouser = shared.getString(CONSTANTS.PREFE_ACCESS_isInCouser, "")
                IsLoginFirstTime = shared.getString(CONSTANTS.PREFE_ACCESS_IsLoginFirstTime, "")
                val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                if (isMainAccount.equals("1")) {
                    if (isAssessmentCompleted.equals("0")) {
                        val intent = Intent(activity, AssProcessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                        intent.putExtra("Navigation", "Enhance")
                        startActivity(intent)
                        finish()
                    } else if (planId.equals("")) {
//                        if (paymentType == "0") {
                            // stripe
                            val intent = Intent(activity, MembershipActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        /*} else if (paymentType == "1") {
                            isEnhanceBack = "1"
                            //IAP
                            val intent = Intent(activity, EnhanceActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                            startActivity(intent)
                            finish()
                        }*/
                    } else if (isPinSet.equals("1")) {
                        if (isSetLoginPin.equals("1")) {
                            when {
                                isAssessmentCompleted.equals("0") -> {
                                    val intent = Intent(activity, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                planId.equals("") -> {
//                                    if (paymentType == "0") {
                                        val intent = Intent(activity, MembershipActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                        startActivity(intent)
                                        finish()
//                                    } else if (paymentType == "1") {
//                                        isEnhanceBack = "1"
//                                        val intent = Intent(activity, EnhanceActivity::class.java)
//                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                                        startActivity(intent)
//                                        finish()
//                                    }
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(activity, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(activity, SleepTimeActivity::class.java)
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
                                    val intent = Intent(activity, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                planId.equals("") -> {
//                                    if (paymentType == "0") {
                                        val intent = Intent(activity, MembershipActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                        startActivity(intent)
                                        finish()
//                                    } else if (paymentType == "1") {
//                                        isEnhanceBack = "1"
//                                        val intent = Intent(activity, EnhanceActivity::class.java)
//                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                                        startActivity(intent)
//                                        finish()
//                                    }
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(activity, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(activity, SleepTimeActivity::class.java)
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
                                    val intent = Intent(activity, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(activity, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(activity, SleepTimeActivity::class.java)
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
                                    val intent = Intent(activity, AssProcessActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    intent.putExtra("Navigation", "Enhance")
                                    startActivity(intent)
                                    finish()
                                }
                                isProfileCompleted.equals("0") -> {
                                    val intent = Intent(activity, ProfileProgressActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(intent)
                                    finish()
                                }
                                avgSleepTime.equals("") -> {
                                    val intent = Intent(activity, SleepTimeActivity::class.java)
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
                    }
                } else {
                    if (isInCouser.equals("0")) {
                        if (isAssessmentCompleted.equals("0")) {
                            val intent = Intent(activity, AssProcessActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                            intent.putExtra("Navigation", "Enhance")
                            startActivity(intent)
                            finish()
                        } else if (isPinSet.equals("1")) {
                            if (isSetLoginPin.equals("1")) {
                                when {
                                    isAssessmentCompleted.equals("0") -> {
                                        val intent = Intent(activity, AssProcessActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        intent.putExtra("Navigation", "Enhance")
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0") -> {
                                        val intent = Intent(activity, ProfileProgressActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("") -> {
                                        val intent = Intent(activity, SleepTimeActivity::class.java)
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
                            val intent = Intent(activity, EnhanceDoneActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NO_HISTORY
                            startActivity(intent)
                            finish()
                        }
                    } else {
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
                    }
                }
            }
        }, (2 * 600).toLong())
    }

    /* TODO function for segment analytics  */
    fun setAnalytics(segmentKey: String, context: Context) {
        try {
            analytics = Analytics.Builder(context, segmentKey).use(FirebaseIntegration.FACTORY).trackApplicationLifecycleEvents().logLevel(Analytics.LogLevel.VERBOSE).trackAttributionInformation().trackAttributionInformation().trackDeepLinks().collectDeviceId(true).build()/*.use(FirebaseIntegration.FACTORY) */
            Analytics.setSingletonInstance(analytics)
        } catch (e: java.lang.Exception) {
        }
    }

   /* override fun inboxDidInitialize() {
        var cleverTapAPI: CleverTapAPI? = null
        cleverTapAPI = CleverTapAPI.getDefaultInstance(context)
        val inboxTabs = arrayListOf("Promotions", "Offers", "Others") //Anything after the first 2 will be ignored
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
        cleverTapAPI!!.showAppInbox() //Opens Activity with default style config
    }

    override fun inboxMessagesDidUpdate() {

    }

    override fun onNotificationClickedPayloadReceived(payload: HashMap<String, Any>?) {

    }*/
}