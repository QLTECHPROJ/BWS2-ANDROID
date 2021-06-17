package com.brainwellnessspa.faqModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityFaqBinding
import com.brainwellnessspa.faqModule.models.FaqListModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FaqActivity : AppCompatActivity() {
    lateinit var binding: ActivityFaqBinding
    var faqListModel: FaqListModel? = null
    private var modelList: ArrayList<FaqListModel.ResponseData>? = null
    lateinit var activity: Activity
    var userID: String? = ""
    var coUserId: String? = ""
    var section: ArrayList<String>? = null
    var gsonBuilder: GsonBuilder? = null
    var p: Properties? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq)
        activity = this@FaqActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        userID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        modelList = ArrayList()
        section = ArrayList()
        gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder!!.create()
        prepareData()
        binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        p = Properties()
        p!!.putValue("coUserId", coUserId)
        section!!.add("Audio")
        section!!.add("Playlist")
        section!!.add("General")
        p!!.putValue("faqCategories", gson.toJson(section))
        BWSApplication.addToSegment("FAQ Viewed", p, CONSTANTS.screen)
        binding.llAudio.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this@FaqActivity)) {
                myBackPress = true
                try {
                    modelList!!.clear()
                    modelList = ArrayList()
                    for (i in faqListModel!!.responseData!!.indices) {
                        if (faqListModel!!.responseData!![i].category!!.contains("Audio")) {
                            modelList!!.add(faqListModel!!.responseData!![i])
                        }
                    }
                    val i = Intent(this@FaqActivity, AudioFaqActivity::class.java)
                    i.putExtra("Flag", "Audio")
                    i.putParcelableArrayListExtra("faqListModel", modelList)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this@FaqActivity)
            }
        }
        binding.llHelp.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this@FaqActivity)) {
                myBackPress = true
                try {
                    modelList!!.clear()
                    modelList = ArrayList()
                    for (i in faqListModel!!.responseData!!.indices) {
                        if (faqListModel!!.responseData!![i].category!!.contains("General")) {
                            modelList!!.add(faqListModel!!.responseData!![i])
                        }
                    }
                    val i = Intent(this@FaqActivity, AudioFaqActivity::class.java)
                    i.putExtra("Flag", "General")
                    i.putExtra("faqListModel", modelList)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this@FaqActivity)
            }
        }

        binding.llPlaylists.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this@FaqActivity)) {
                myBackPress = true
                try {
                    modelList!!.clear()
                    modelList = ArrayList()
                    for (i in faqListModel!!.responseData!!.indices) {
                        if (faqListModel!!.responseData!![i].category!!.contains("Playlist")) {
                            modelList!!.add(faqListModel!!.responseData!![i])
                        }
                    }
                    val i = Intent(this@FaqActivity, AudioFaqActivity::class.java)
                    i.putExtra("Flag", "Playlist")
                    i.putExtra("faqListModel", modelList)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this@FaqActivity)
            }
        }
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().faqLists
            listCall.enqueue(object : Callback<FaqListModel?> {
                override fun onResponse(
                    call: Call<FaqListModel?>,
                    response: Response<FaqListModel?>
                ) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            faqListModel = listModel
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<FaqListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onBackPressed() {
        myBackPress = true
        finish()
    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
            }
            numStarted++
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Restored")
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}