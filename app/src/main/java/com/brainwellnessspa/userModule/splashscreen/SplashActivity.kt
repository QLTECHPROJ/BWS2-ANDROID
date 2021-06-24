package com.brainwellnessspa.userModule.splashscreen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.analytics
import com.brainwellnessspa.BWSApplication.key
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivitySplashBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.models.CoUserDetailsModel
import com.brainwellnessspa.userModule.models.VersionModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.userModule.signupLogin.SignUpActivity
import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.AppSignatureHashHelper
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.segment.analytics.Analytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var emailUser: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var indexScore: String? = ""
    var avgSleepTime: String? = ""

    /* TODO function for app started  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        key = appSignatureHashHelper.appSignatures[0]
        val sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, MODE_PRIVATE)
        val editor = sharedx.edit()
        editor.putString(
            CONSTANTS.PREF_KEY_SplashKey,
            appSignatureHashHelper.getAppSignatures().get(0)
        )
        editor.commit()
        if (key.equals("", ignoreCase = true)) {
            key = BWSApplication.getKey(this)
        }

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
    }

    override fun onResume() {
        /* TODO conditions for check user exist or not */
        if (userId.equals("", ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this@SplashActivity, SignInActivity::class.java)
                i.putExtra("mobileNo", "")
                i.putExtra("countryCode", "")
                i.putExtra("name", "")
                i.putExtra("email", "")
                i.putExtra("countryShortName", "")
                startActivity(i)
                finish()
            }, (2 * 800).toLong())
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                val isIgnoringBatteryOptimizations: Boolean =
                    pm.isIgnoringBatteryOptimizations(packageName)
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                    callDashboard()
                } else {
                    // Not ignoring battery optimization
                    callDashboard()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /* TODO function for check app version  */
    private fun checkAppVersion() {
        val appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa"
        if (BWSApplication.isNetworkConnected(this)) {
            val listCall: Call<VersionModel> = APINewClient.getClient()
                .getAppVersions(BuildConfig.VERSION_CODE.toString(), CONSTANTS.FLAG_ONE)
            listCall.enqueue(object : Callback<VersionModel> {
                override fun onResponse(
                    call: Call<VersionModel>,
                    response: Response<VersionModel>
                ) {
                    try {
                        val versionModel: VersionModel = response.body()!!
                        try {
                            setAnalytics(versionModel.responseData!!.segmentKey!!)

                            when {
                                versionModel.responseData!!.isForce
                                    .equals("0", ignoreCase = true) -> {
                                    val builder = AlertDialog.Builder(this@SplashActivity)
                                    builder.setTitle("Update Brain Wellness Spa")
                                    builder.setCancelable(false)
                                    builder.setMessage("Brain Wellness Spa recommends that you update to the latest version")
                                        .setPositiveButton("UPDATE") { dialog: DialogInterface, _: Int ->
                                            this@SplashActivity.startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(appURI)
                                                )
                                            )
                                            dialog.cancel()
                                        }
                                        .setNegativeButton("NOT NOW") { dialog: DialogInterface, _: Int ->
                                            askBattyPermission()
                                            dialog.dismiss()
                                        }
                                    builder.create().show()
                                }
                                versionModel.responseData!!.isForce
                                    .equals("1", ignoreCase = true) -> {
                                    val builder = AlertDialog.Builder(this@SplashActivity)
                                    builder.setTitle("Update Required")
                                    builder.setCancelable(false)
                                    builder.setMessage("To keep using Brain Wellness Spa, download the latest version")
                                        .setCancelable(false)
                                        .setPositiveButton("UPDATE") { _: DialogInterface?, _: Int ->
                                            this@SplashActivity.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(appURI))
                                            )
                                        }
                                    builder.create().show()
                                }
                                versionModel.responseData!!.isForce
                                    .equals("", ignoreCase = true) -> {
                                    askBattyPermission()
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
        if (BWSApplication.isNetworkConnected(this)) {
            val listCall: Call<CoUserDetailsModel> =
                APINewClient.getClient().getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<CoUserDetailsModel> {
                override fun onResponse(
                    call: Call<CoUserDetailsModel>,
                    response: Response<CoUserDetailsModel>
                ) {
                    try {
                        val coUserDetailsModel: CoUserDetailsModel = response.body()!!
                        isProfileCompleted =
                            coUserDetailsModel.ResponseData!!.isProfileCompleted.toString()
                        isAssessmentCompleted =
                            coUserDetailsModel.ResponseData.isAssessmentCompleted.toString()
                        indexScore = coUserDetailsModel.ResponseData.indexScore.toString()
                        avgSleepTime = coUserDetailsModel.ResponseData.AvgSleepTime.toString()
                        val shared = getSharedPreferences(
                            CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                            Context.MODE_PRIVATE
                        )
                        val editor = shared.edit()
                        editor.putString(
                            CONSTANTS.PREFE_ACCESS_INDEXSCORE,
                            coUserDetailsModel.ResponseData.indexScore
                        )
                        editor.putString(
                            CONSTANTS.PREFE_ACCESS_MOBILE,
                            coUserDetailsModel.ResponseData.Mobile
                        )
                        editor.putString(
                            CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED,
                            coUserDetailsModel.ResponseData.isProfileCompleted
                        )
                        editor.putString(
                            CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED,
                            coUserDetailsModel.ResponseData.isAssessmentCompleted
                        )
                        editor.apply()
                        val shred =
                            getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                        val edited = shred.edit()
                        edited.putString(
                            CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                            coUserDetailsModel.ResponseData.AvgSleepTime
                        )
                        val selectedCategoriesTitle = arrayListOf<String>()
                        val selectedCategoriesName = arrayListOf<String>()
                        val gson = Gson()
                        for (i in coUserDetailsModel.ResponseData.AreaOfFocus!!) {
                            selectedCategoriesTitle.add(i.MainCat!!)
                            selectedCategoriesName.add(i.RecommendedCat!!)
                        }
                        edited.putString(
                            CONSTANTS.selectedCategoriesTitle,
                            gson.toJson(selectedCategoriesTitle)
                        )
                        edited.putString(
                            CONSTANTS.selectedCategoriesName,
                            gson.toJson(selectedCategoriesName)
                        )
                        edited.apply()
                        checkAppVersion()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CoUserDetailsModel>, t: Throwable) {
                }
            })
        } else {
            setAnalytics(getString(R.string.segment_key_real))
            askBattyPermission()
            BWSApplication.showToast(getString(R.string.no_server_found), this@SplashActivity)
        }
    }

    /* TODO function for battery permission  */
    @SuppressLint("BatteryLife")
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
    }

    private fun callDashboard() {
        Log.e("isProfileCompleted", isProfileCompleted.toString())
        Log.e("isAssessmentCompleted", isAssessmentCompleted.toString())
        Log.e("indexScore", indexScore.toString())
        Log.e("avgSleepTime", avgSleepTime.toString())
        if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(
                    this@SplashActivity,
                    AssProcessActivity::class.java
                )
                intent.putExtra(
                    CONSTANTS.ASSPROCESS,
                    "0"
                )
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())
        } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, WalkScreenActivity::class.java)
                intent.putExtra(CONSTANTS.ScreenView, "2")
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())
        } else if (avgSleepTime.equals("", ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, SleepTimeActivity::class.java)
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())
        } else if (isProfileCompleted.equals("1", ignoreCase = true) &&
            isAssessmentCompleted.equals("1", ignoreCase = true)
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "0")
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())
        }
    }

    /* TODO function for segment analytics  */
    fun setAnalytics(segmentKey: String) {
        try {
//     TODO : Live segment key
//                            analytics = new Analytics.Builder(getApplication(), "Al8EubbxttJtx0GvcsQymw9ER1SR2Ovy")//live
            analytics = Analytics.Builder(application, segmentKey)
                .trackApplicationLifecycleEvents()
                .logLevel(Analytics.LogLevel.VERBOSE).trackAttributionInformation()
                .trackAttributionInformation()
                .trackDeepLinks()
                .collectDeviceId(true)
                .build()
            /*.use(FirebaseIntegration.FACTORY) */Analytics.setSingletonInstance(analytics)
        } catch (e: java.lang.Exception) {
//            catch = true;
//            Log.e("in Catch", "True");
//            Properties p = new Properties();
//            p.putValue("Application Crashed", e.toString());
//            YupITApplication.addtoSegment("Application Crashed", p,  CONSTANTS.track);
        }
    }
}