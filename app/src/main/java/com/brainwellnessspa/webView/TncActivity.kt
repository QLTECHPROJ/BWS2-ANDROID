package com.brainwellnessspa.webView

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityTncBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.AppUtils

class TncActivity : AppCompatActivity() {
    lateinit var binding: ActivityTncBinding
    private var web: String? = null
    var activity: Activity? = null
    private var tnc = AppUtils.tncs_url
    var privacyPolicy = AppUtils.privacy_policy_url!!
    private var howReferWorks = AppUtils.how_refer_works_url!!
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tnc)
        activity = this@TncActivity
        if (intent != null) {
            web = intent.getStringExtra("Web")
        }
        binding.webView.clearHistory()
        binding.webView.clearCache(true)
        binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                binding.progressBar.progress = progress
                if (progress == 100) {
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = CustomWebViewClient()
        when {
            web.equals("Tnc", ignoreCase = true) -> {
                binding.tvTitle.setText(R.string.t_n_csf)
                binding.webView.loadUrl(tnc)
            }
            web.equals("PrivacyPolicy", ignoreCase = true) -> {
                binding.tvTitle.setText(R.string.privacy_policy)
                binding.webView.loadUrl(privacyPolicy)
            }
            web.equals("HowReferWorks", ignoreCase = true) -> {
                binding.tvTitle.setText(R.string.how_refer_works)
                binding.webView.loadUrl(howReferWorks)
            }
        } /*else {
            binding.tvTitle.setText(Web);
            binding.webView.loadUrl(HowReferWorks);
        }*/
    }

    private inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
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
                Log.e("Destroy", "Activity Destroyed")
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