package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment
import com.brainwellnessspa.R
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.billingOrderModule.fragments.BillingAddressFragment
import com.brainwellnessspa.billingOrderModule.fragments.CurrentPlanFragment
import com.brainwellnessspa.databinding.ActivityBillingOrderBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.segment.analytics.Properties

class BillingOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityBillingOrderBinding
    var payment = 0
    var userId: String? = ""
    var coUserId: String? = ""
    private var numStarted = 0
    var stackStatus = 0
    var activity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing_order)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        activity = this@BillingOrderActivity
        binding.llBack.setOnClickListener {
            myBackPressbill = true
            AudioDownloadsFragment.comefromDownload = "0"
            finish()
        }
        binding.btnUpgradePlan.setOnClickListener {
            val i = Intent(activity, UpgradePlanActivity::class.java)
            startActivity(i)
        }
        binding.tvCancel.setOnClickListener {
            val i = Intent(activity, CancelMembershipActivity::class.java)
            startActivity(i)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val p = Properties()
        p.putValue("userId", userId)
        p.putValue("plan", "")
        p.putValue("planStatus", "")
        p.putValue("planStartDt", "")
        p.putValue("planExpiryDt", "")
        p.putValue("planAmount", "")
        BWSApplication.addToSegment("Billing & Order Screen Viewed", p, CONSTANTS.screen)
        binding.viewPager.offscreenPageLimit = 2
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current Plan"))
        //        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payment"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Billing Address"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        if (BWSApplication.isNetworkConnected(this)) {
            val adapter = TabAdapter(
                supportFragmentManager, this, binding.tabLayout.tabCount
            )
            binding.viewPager.adapter = adapter
            binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
        if (intent.hasExtra("payment")) {
            payment = intent.getIntExtra("payment", 0)
        }
        if (payment != 0) {
            binding.viewPager.currentItem = 1
        } else {
            binding.viewPager.currentItem = 0
        }
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onBackPressed() {
        myBackPressbill = true
        AudioDownloadsFragment.comefromDownload = "0"
        finish()
    }

    inner class TabAdapter(fm: FragmentManager?, var myContext: Context, var totalTabs: Int) :
        FragmentStatePagerAdapter(
            fm!!
        ) {
        override fun getItem(position: Int): Fragment {
            val bundle: Bundle
            return when (position) {
                0 -> {
                    val currentPlanFragment = CurrentPlanFragment()
                    bundle = Bundle()
                    currentPlanFragment.arguments = bundle
                    currentPlanFragment
                }
                1 -> {
                    val billingAddressFragment = BillingAddressFragment()
                    bundle = Bundle()
                    billingAddressFragment.arguments = bundle
                    billingAddressFragment
                }
                else -> getItem(position)
            }
        }

        override fun getCount(): Int {
            return totalTabs
        }
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
                if (!myBackPressbill) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPressbill = true
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
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        @JvmField
        var myBackPressbill = false
    }
}