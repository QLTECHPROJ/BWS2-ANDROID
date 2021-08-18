package com.brainwellnessspa.userModule.splashscreen

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
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
import com.google.gson.Gson
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.firebase.FirebaseIntegration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
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
        val sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, MODE_PRIVATE)
        val editor = sharedx.edit()
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.appSignatures[0])
        editor.apply()
        if (key.equals("", ignoreCase = true)) {
            key = getKey(this)
        }

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
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


//        BWSApplication.showToast("Notify Me "+ ("\ud83d\ude01")+("\ud83d\udc34"),this)
    }

    override fun onResume() {/* TODO conditions for check user exist or not */
        if (userId.equals("", ignoreCase = true)) {
            checkAppVersion()
        } /*else if (!userId.equals("", ignoreCase = true) && coUserId.equals(
                        "",
                        ignoreCase = true
                )
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, UserListActivity::class.java)
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())

        }*/ else {
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
                                versionModel.ResponseData.IsForce.equals("0", ignoreCase = true) -> {
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
                                versionModel.ResponseData.IsForce.equals("1", ignoreCase = true) -> {
                                    val builder = AlertDialog.Builder(this@SplashActivity)
                                    builder.setTitle("Update Required")
                                    builder.setCancelable(false)
                                    builder.setMessage("To keep using Brain Wellness App, download the latest version").setCancelable(false).setPositiveButton("UPDATE") { _: DialogInterface?, _: Int ->
                                        this@SplashActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
                                    }
                                    builder.create().show()
                                }
                                versionModel.ResponseData.IsForce.equals("", ignoreCase = true) -> {
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
                        if (authOtpModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {

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
                        } else if (authOtpModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
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
            } else {
                Log.e("isMainAccount", isMainAccount.toString())
                Log.e("isProfileCompleted", isProfileCompleted.toString())
                Log.e("isAssessmentCompleted", isAssessmentCompleted.toString())
                Log.e("WellnessScore", indexScore.toString())
                Log.e("avgSleepTime", avgSleepTime.toString())
                Log.e("coUserCount", coUserCount.toString())
                Log.e("isSetLoginPin", isSetLoginPin.toString())
                if (isMainAccount.equals("1", ignoreCase = true)) {
                    if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                        startActivity(intent)
                        finish()
                    } else if (planId.equals("", ignoreCase = true)) {
                        IsBackFromEnhance = "0"
                        val intent = Intent(applicationContext, EnhanceActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (isPinSet.equals("1", ignoreCase = true)) {
                        if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                            startActivity(intent)
                            finish()
                        } else if (planId.equals("", ignoreCase = true)) {
                            IsBackFromEnhance = "0"
                            val intent = Intent(applicationContext, EnhanceActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (avgSleepTime.equals("", ignoreCase = true)) {
                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            if (isSetLoginPin.equals("1", ignoreCase = true)) {
                                when {
                                    isAssessmentCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                    planId.equals("", ignoreCase = true) -> {
                                        IsBackFromEnhance = "0"
                                        val intent = Intent(applicationContext, EnhanceActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        val intent = Intent(activity, BottomNavigationActivity::class.java)
                                        intent.putExtra("IsFirst", "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            } else {
                                when {
                                    coUserCount.toString() > "0" -> {
                                        val intent = Intent(activity, UserListActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    isAssessmentCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                    planId.equals("", ignoreCase = true) -> {
                                        IsBackFromEnhance = "0"
                                        val intent = Intent(applicationContext, EnhanceActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        val intent = Intent(activity, BottomNavigationActivity::class.java)
                                        intent.putExtra("IsFirst", "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    } else if (isPinSet.equals("0", ignoreCase = true) || isPinSet.equals("", ignoreCase = true)) {
                        if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                            startActivity(intent)
                            finish()
                        } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (avgSleepTime.equals("", ignoreCase = true)) {
                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            if (isSetLoginPin.equals("1", ignoreCase = true)) {
                                when {
                                    isAssessmentCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        val intent = Intent(activity, BottomNavigationActivity::class.java)
                                        intent.putExtra("IsFirst", "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            } else {
                                if (coUserCount.toString() > "0") {
                                    val intent = Intent(activity, UserListActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                } else {
                    if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                        startActivity(intent)
                        finish()
                    } else if (isPinSet.equals("1", ignoreCase = true)) {
                        if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                            startActivity(intent)
                            finish()
                        } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (avgSleepTime.equals("", ignoreCase = true)) {
                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            if (isSetLoginPin.equals("1", ignoreCase = true)) {
                                when {
                                    isAssessmentCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        val intent = Intent(activity, BottomNavigationActivity::class.java)
                                        intent.putExtra("IsFirst", "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            } else {
                                when {
                                    coUserCount.toString() > "0" -> {
                                        val intent = Intent(activity, UserListActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    isAssessmentCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                    isProfileCompleted.equals("0", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    avgSleepTime.equals("", ignoreCase = true) -> {
                                        val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        val intent = Intent(activity, BottomNavigationActivity::class.java)
                                        intent.putExtra("IsFirst", "0")
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    } else if (isPinSet.equals("0", ignoreCase = true) || isPinSet.equals("", ignoreCase = true)) {
                        val intent = Intent(applicationContext, EnhanceDoneActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }, (2 * 800).toLong())
    }

    /* TODO function for segment analytics  */
    fun setAnalytics(segmentKey: String, context: Context) {
        try { //     TODO : Live segment key
            //                            analytics = new Analytics.Builder(getApplication(), "Al8EubbxttJtx0GvcsQymw9ER1SR2Ovy")//live
            analytics = Analytics.Builder(context, segmentKey).use(FirebaseIntegration.FACTORY).trackApplicationLifecycleEvents().logLevel(Analytics.LogLevel.VERBOSE).trackAttributionInformation().trackAttributionInformation().trackDeepLinks().collectDeviceId(true).build()/*.use(FirebaseIntegration.FACTORY) */
            Analytics.setSingletonInstance(analytics)
        } catch (e: java.lang.Exception) { //            catch = true;
            //            Log.e("in Catch", "True");
            //            Properties p = new Properties();
            //            p.putValue("Application Crashed", e.toString());
            //            YupITApplication.addtoSegment("Application Crashed", p,  CONSTANTS.track);
        }
    }
}