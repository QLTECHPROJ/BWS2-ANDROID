package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.SplashModule.Models.VersionModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    lateinit var ctx: Context
    lateinit var binding: ActivitySplashBinding
    var UserID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        checkAppVersion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                var isIgnoringBatteryOptimizations: Boolean = false
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
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

    private fun checkAppVersion() {
        val appURI = "https://play.google.com/store/apps/details?id=com.brainwellnessspa"

        if (BWSApplication.isNetworkConnected(this)) {
            val listCall: Call<VersionModel> = APINewClient.getClient().getAppVersions(BuildConfig.VERSION_CODE.toString(), CONSTANTS.FLAG_ONE)
            listCall.enqueue(object : Callback<VersionModel> {
                override fun onResponse(call: Call<VersionModel>, response: Response<VersionModel>) {
                    try {
                        val versionModel: VersionModel = response.body()!!
                        try {
                            if (versionModel.getResponseData().getIsForce().equals("0", ignoreCase = true)) {
                                val builder = AlertDialog.Builder(ctx)
                                builder.setTitle("Update Brain Wellness Spa")
                                builder.setCancelable(false)
                                builder.setMessage("Brain Wellness Spa recommends that you update to the latest version")
                                        .setPositiveButton("UPDATE") { dialog: DialogInterface, _: Int ->
                                            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI)))
                                            dialog.cancel()
                                        }
                                        .setNegativeButton("NOT NOW") { dialog: DialogInterface, _: Int ->
                                            askBattryParmition()
                                            dialog.dismiss()
                                        }
                                builder.create().show()
                            } else if (versionModel.getResponseData().getIsForce().equals("1", ignoreCase = true)) {
                                val builder = AlertDialog.Builder(ctx)
                                builder.setTitle("Update Required")
                                builder.setCancelable(false)
                                builder.setMessage("To keep using Brain Wellness Spa, download the latest version")
                                        .setCancelable(false)
                                        .setPositiveButton("UPDATE") { _: DialogInterface?, _: Int -> ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appURI))) }
                                builder.create().show()
                            } else if (versionModel.getResponseData().getIsForce().equals("", ignoreCase = true)) {
                                askBattryParmition()
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
        } else {
        }
    }

    private fun askBattryParmition() {
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
        if (UserID.equals("", ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, GetStartedActivity::class.java)
                intent.putExtra(CONSTANTS.ScreenVisible, "1")
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, BottomNavigationActivity::class.java)
                intent.putExtra(CONSTANTS.ScreenVisible, "1")
                startActivity(intent)
                finish()
            }, (2 * 800).toLong())

        }


    }

}