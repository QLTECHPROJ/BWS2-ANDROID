package com.brainwellnessspa.invoiceModule.activities

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityInvoiceBinding
import com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment
import com.brainwellnessspa.invoiceModule.fragments.AppointmentInvoiceFragment
import com.brainwellnessspa.invoiceModule.fragments.MembershipInvoiceFragment
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel.Appointment
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel.MemberShip
import com.brainwellnessspa.invoiceModule.models.SegmentMembership
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class InvoiceActivity : AppCompatActivity() {
    lateinit var binding: ActivityInvoiceBinding
    lateinit var appointmentList: ArrayList<Appointment>
    lateinit var memberShipList: ArrayList<MemberShip>
    var UserID: String? = ""
    var ComeFrom: String? = ""
    var context: Context? = null
    var activity: Activity? = null
    var p: Properties? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice)
        context = this@InvoiceActivity
        activity = this@InvoiceActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (intent != null) {
            ComeFrom = intent.getStringExtra("ComeFrom")
        }
        val p = Properties()
        p.putValue("userId", UserID)
        BWSApplication.addToSegment("Invoices Screen Viewed", p, CONSTANTS.screen)
        binding.llBack.setOnClickListener { view: View? -> callBack() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        prepareData()
    }

    private fun callBack() {
        myBackPress = true
        if (invoiceToRecepit == 0) {
            invoiceToRecepit = 1
            BWSApplication.tutorial = false
            if (ComeFrom.equals("1", ignoreCase = true)) {
                invoiceToDashboard = 1
                val i = Intent(context, BottomNavigationActivity::class.java)
                startActivity(i)
                finish()
            } else if (ComeFrom.equals("", ignoreCase = true)) {
                invoiceToDashboard = 1
                val i = Intent(context, BottomNavigationActivity::class.java)
                startActivity(i)
            } else {
                AudioDownloadsFragment.comefromDownload = "0"
                finish()
            }
        } else if (invoiceToRecepit == 1) {
            if (ComeFrom.equals("", ignoreCase = true)) {
                invoiceToDashboard = 0
                finish()
            } else {
                AudioDownloadsFragment.comefromDownload = "0"
                invoiceToRecepit = 1
                val i = Intent(context, BottomNavigationActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APIClient.getClient().getInvoicelistPlaylist(UserID, "1")
            listCall!!.enqueue(object : Callback<InvoiceListModel?> {
                override fun onResponse(call: Call<InvoiceListModel?>, response: Response<InvoiceListModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            appointmentList = ArrayList()
                            memberShipList = ArrayList()
                            appointmentList = listModel.responseData.appointment
                            memberShipList = listModel.responseData.memberShip
                            binding.viewPager.offscreenPageLimit = 2
                            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Manage"))
                            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Wellness"))
                            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                            val adapter = TabAdapter(supportFragmentManager, binding.tabLayout.tabCount)
                            binding.viewPager.adapter = adapter
                            binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
                            binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                                override fun onTabSelected(tab: TabLayout.Tab) {
                                    binding!!.viewPager.currentItem = tab.position
                                    p = Properties()
                                    p!!.putValue("userId", UserID)
                                    if (tab.position == 0) {
                                        p!!.putValue("invoiceType", "Memebrship")
                                        val section1 = ArrayList<SegmentMembership>()
                                        val e = SegmentMembership()
                                        val gson = Gson()
                                        for (i in memberShipList.indices) {
                                            e.invoiceId = memberShipList.get(i).invoiceId
                                            e.invoiceAmount = memberShipList.get(i).amount
                                            e.invoiceDate = memberShipList.get(i).date
                                            e.invoiceCurrency = ""
                                            e.plan = ""
                                            e.planStartDt = ""
                                            e.planExpiryDt = ""
                                            section1.add(e)
                                        }
                                        p!!.putValue("membership", gson.toJson(section1))
                                    } else if (tab.position == 1) {
                                        p!!.putValue("invoiceType", "Appointment")
                                    }
                                    BWSApplication.addToSegment("Invoice Screen Viewed", p, CONSTANTS.screen)
                                }

                                override fun onTabUnselected(tab: TabLayout.Tab) {}
                                override fun onTabReselected(tab: TabLayout.Tab) {}
                            })
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<InvoiceListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    override fun onBackPressed() {
        callBack()
    }

    inner class TabAdapter(fm: FragmentManager?, var totalTabs: Int) : FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val bundle = Bundle()
                    val membershipInvoiceFragment = MembershipInvoiceFragment()
                    bundle.putParcelableArrayList("membershipInvoiceFragment", memberShipList)
                    membershipInvoiceFragment.arguments = bundle
                    membershipInvoiceFragment
                }
                1 -> {
                    val bundle = Bundle()
                    val appointmentInvoiceFragment = AppointmentInvoiceFragment()
                    bundle.putParcelableArrayList("appointmentInvoiceFragment", appointmentList)
                    appointmentInvoiceFragment.arguments = bundle
                    appointmentInvoiceFragment
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
                Log.e("APPLICATION", "APP IN FOREGROUND") //app went to foreground
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

    companion object {
        @JvmField var invoiceToDashboard = 0

        @JvmField var invoiceToRecepit = 0
    }
}