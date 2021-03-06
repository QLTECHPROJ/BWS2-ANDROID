package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.models.PlanDetails
import com.brainwellnessspa.databinding.ActivityBillingOrderBinding
import com.brainwellnessspa.databinding.ActivityIapBillingOrderBinding
import com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/* This is the old BWA billing order activity */
class IAPBillingOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityIapBillingOrderBinding
    private var payment = 0
    var userId: String? = ""
    var coUserId: String? = ""
    private var numStarted = 0
    lateinit var listModelGlobal: PlanDetails
    var stackStatus = 0
    lateinit var activity: Activity
    lateinit var ctx: Context

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_iap_billing_order)
        /* This is the get string userId & coUserId */
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        activity = this@IAPBillingOrderActivity
        ctx = this@IAPBillingOrderActivity

        /* This is the screen back button click */
        binding.llBack.setOnClickListener {
            myBackPressbill = true
            AudioDownloadsFragment.comefromDownload = "0"
            finish()
        }
        /* This is the upgrade plan click */
        binding.btnUpgradePlan.setOnClickListener {
            val i = Intent(activity, UpgradePlanActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            i.putExtra("PlanId", listModelGlobal.responseData!!.planId)
            i.putExtra("DeviceType", listModelGlobal.responseData!!.deviceType)
            startActivity(i)
            finish()
        }

        /* This is the cancel plan click */
        binding.tvCancel.setOnClickListener {
            val i = Intent(activity, IAPCancelMembershipActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            i.putExtra("screenView", "1")
            val c: Calendar = Calendar.getInstance()
            c.timeInMillis = listModelGlobal.responseData!!.planPurchaseDate!!.toInt() * 1000L
            val d: Date = c.time
            val sdf = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT_TIME,Locale.ENGLISH)
            binding.tvActive.text = sdf.format(d)

            val c1: Calendar = Calendar.getInstance()
            c1.timeInMillis = listModelGlobal.responseData!!.planExpireDate!!.toInt() * 1000L
            val d1: Date = c1.time
            val sdf1 = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT_TIME,Locale.ENGLISH)
            i.putExtra("planId", listModelGlobal.responseData!!.planId)
            i.putExtra("plan", listModelGlobal.responseData!!.planName)
            i.putExtra("planStatus", listModelGlobal.responseData!!.planStatus)
            i.putExtra("planStartDt ", sdf.format(d))
            i.putExtra("planExpiryDt", sdf1.format(d1))
            i.putExtra("planAmount", listModelGlobal.responseData!!.price)
            startActivity(i)
//            finish()
        }

        /* This condition is check about application in background or foreground */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }

        /* This is the tab layout showing code */
        /*        binding.viewPager.offscreenPageLimit = 2
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Current Plan")) //        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payment"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Billing Address"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL*/
        /* if (BWSApplication.isNetworkConnected(this)) {
             val adapter = TabAdapter(supportFragmentManager, binding.tabLayout.tabCount)
             binding.viewPager.adapter = adapter
             binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
         } else {
             BWSApplication.showToast(getString(R.string.no_server_found), this)
         }*/

        if (intent.hasExtra("payment")) {
            payment = intent.getIntExtra("payment", 0)
        }
        if (payment != 0) {
            binding.viewPager.currentItem = 1
        } else {
            binding.viewPager.currentItem = 0
        }

        getPlanDetails()
        /*   binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

               override fun onTabUnselected(tab: TabLayout.Tab) {}
               override fun onTabReselected(tab: TabLayout.Tab) {}
           })*/
    }

    override fun onResume() {
        Log.e("on resume", "yes billing")
        if (BWSApplication.IsRefreshPlan.equals("1")) {
            getPlanDetails()
        }
        getPlanDetails()
        super.onResume()
    }

   /* fun getCurrencySymbol(currencyCode: String?, priceAmountMicros: String): String? {
        return try {
            val currency = Currency.getInstance(currencyCode)
            val priceAmount: Float = priceAmountMicros.toFloat() / 1000000.0f
            val priceAmount1 = "0"
            val price = "${currency.symbol}$priceAmount$priceAmount1"
            price
        } catch (e: java.lang.Exception) {
            "$"
        }
    }*/

    private fun getPlanDetails() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlanDetails> = APINewClient.client.getPlanDetails(coUserId)
            listCall.enqueue(object : Callback<PlanDetails> {
                override fun onResponse(call: Call<PlanDetails>, response: Response<PlanDetails>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: PlanDetails = response.body()!!
                        listModelGlobal = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_PlanDeviceType, listModel.responseData!!.deviceType)
                            editor.apply()

                            binding.tvTitle.text = listModel.responseData!!.planDescription
                            binding.tvPlan.text = listModel.responseData!!.planName
//                            val SymbolCurrency = getCurrencySymbol("INR", "650000000")
                            //                            binding.tvPrice.text = simbl + /*listModel.responseData!!.price+ */ " " + listModel.responseData!!.intervalTime
                           val price = "$" + listModel.responseData!!.price + " " + listModel.responseData!!.intervalTime
                            binding.tvPrice.text = price
                            val c: Calendar = Calendar.getInstance()
                            c.timeInMillis = listModel.responseData!!.planPurchaseDate!!.toInt() * 1000L
                            val d: Date = c.time
                            val sdf = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT_TIME,Locale.ENGLISH)
                            binding.tvActive.text = sdf.format(d)

                            val c1: Calendar = Calendar.getInstance()
                            c1.timeInMillis = listModel.responseData!!.planExpireDate!!.toInt() * 1000L
                            val d1: Date = c1.time
                            val sdf1 = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT_TIME,Locale.ENGLISH)
                            var statusRenew = ""
                            if (listModel.responseData!!.planStatus.equals("Cancelled", ignoreCase = true) || listModel.responseData!!.planStatus.equals("Inactive", ignoreCase = true)) {
                                statusRenew = "(Expired On " + sdf1.format(d1) + ")"
                            } else if (listModel.responseData!!.planStatus.equals("Active", ignoreCase = true)) {
                                statusRenew = "(Renew On " + sdf1.format(d1) + ")"
                            } else if (listModel.responseData!!.planStatus.equals("Pause", ignoreCase = true)) {
                                statusRenew = "(Resume On " + sdf1.format(d1) + ")"
                            }
                            binding.tvStatusRenew.text = statusRenew

                            binding.tvStatus.text = listModel.responseData!!.planStatus

                            if (listModel.responseData!!.planStatus.equals("Inactive")) {
                                binding.btnUpgradePlan.visibility = View.VISIBLE
                            } else {
                                binding.btnUpgradePlan.visibility = View.GONE
                            }

                            if (listModel.responseData!!.planStatus.equals("Active")) {
                                binding.tvCancel.visibility = View.VISIBLE
                            } else {
                                binding.tvCancel.visibility = View.GONE
                            }
                            val p = Properties()
                            p.putValue("plan", listModel.responseData!!.planName)
                            p.putValue("planStatus", listModel.responseData!!.planStatus)
                            p.putValue("planStartDt ", sdf.format(d))
                            p.putValue("planExpiryDt", sdf1.format(d1))
                            p.putValue("planAmount", listModel.responseData!!.price)
                            BWSApplication.addToSegment("Billing & Order Screen Viewed", p, CONSTANTS.screen)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanDetails>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    /* This is the device back button click */
    override fun onBackPressed() {
        myBackPressbill = true
        AudioDownloadsFragment.comefromDownload = "0"
        finish()
    }

    /* This class is the handling tab layout */
    /*  inner class TabAdapter(fm: FragmentManager?, private var totalTabs: Int) : FragmentStatePagerAdapter(fm!!) {
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
      }*/

    /* This class is check about application in background or foreground */
    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND") //app went to foreground
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
                Log.e("APPLICATION", "App is in BACKGROUND") // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    /* This is object declaration */
    companion object {
        @JvmField
        var myBackPressbill = false
    }
}