package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.fragments.CurrentPlanFragment
import com.brainwellnessspa.billingOrderModule.fragments.PaymentFragment
import com.brainwellnessspa.databinding.ActivityBillingOrderBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.segment.analytics.Properties

class BillingOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityBillingOrderBinding
    var payment = 0
    var UserID: String? = null
    private var numStarted = 0
    var stackStatus = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing_order)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.llBack.setOnClickListener {
            myBackPressbill = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val p = Properties()
        BWSApplication.addToSegment("Billing & Order Screen Viewed", p, CONSTANTS.screen)
        binding.viewPager.offscreenPageLimit = 3
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current Plan"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payment"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        if (BWSApplication.isNetworkConnected(this)) {
            val adapter = TabAdapter(supportFragmentManager, this, binding.tabLayout.tabCount)
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
        finish()
    }

    inner class TabAdapter(fm: FragmentManager?, var myContext: Context, var totalTabs: Int) : FragmentStatePagerAdapter(fm!!) {
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
                    val paymentFragment = PaymentFragment()
                    bundle = Bundle()
                    paymentFragment.arguments = bundle
                    paymentFragment
                }/*
                2 -> {
                    val billingAddressFragment = BillingAddressFragment()
                    bundle = Bundle()
                    billingAddressFragment.arguments = bundle
                    billingAddressFragment
                }*/
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
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        var myBackPressbill = false
    }
}