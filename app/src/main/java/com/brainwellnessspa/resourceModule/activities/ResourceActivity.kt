package com.brainwellnessspa.resourceModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment
import com.brainwellnessspa.R
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityResourceBinding
import com.brainwellnessspa.databinding.FilterListLayoutBinding
import com.brainwellnessspa.resourceModule.Fragments.*
import com.brainwellnessspa.resourceModule.models.ResourceFilterModel
import com.brainwellnessspa.resourceModule.models.ResourceListModel
import com.brainwellnessspa.resourceModule.models.SegmentResource
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ResourceActivity : AppCompatActivity() {
    lateinit var binding: ActivityResourceBinding
    var userId: String? = ""
    var category: String? = ""
    var tabFlag = "1"
    var coUserId: String? = ""
    lateinit var activity: Activity
    var currentTab = 0
    private var dialogBox: Dialog? = null
    private var mLastClickTime: Long = 0
    lateinit var rvFilterList: RecyclerView
    var ivFilter: ImageView? = null
    lateinit var tvAll: TextView
    private lateinit var li: LayoutInflater
    private lateinit var promptsView: View
    var p: Properties? = null
    var p4: Properties? = null
    var section: ArrayList<String?>? = null
    var gsonBuilder: GsonBuilder? = null
    var gson: Gson? = null
    var ctx: Context? = null
    var resourceListModel: ResourceListModel? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource)
        activity = this@ResourceActivity
        ctx = this@ResourceActivity
        binding.llBack.setOnClickListener {
            myBackPress = true
            AudioDownloadsFragment.comefromDownload = "0"
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        p4 = Properties()
        p4!!.putValue("coUserId", coUserId)
        section = ArrayList()
        gsonBuilder = GsonBuilder()
        gson = gsonBuilder!!.create()
        binding.viewPager.offscreenPageLimit = 5
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio Books"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Podcasts"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Apps"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Websites"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documentaries"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                currentTab = tab.position
                p = Properties()
                p!!.putValue("coUserId", coUserId)
                when (tab.position) {
                    0 -> {
                        tabFlag = CONSTANTS.FLAG_ONE
                        p!!.putValue("resourceType", "Audio Books")
                        p4!!.putValue("resourceType", "Audio Books")
                    }
                    1 -> {
                        tabFlag = CONSTANTS.FLAG_TWO
                        p!!.putValue("resourceType", "Podcasts")
                        p4!!.putValue("resourceType", "Podcasts")
                    }
                    2 -> {
                        tabFlag = CONSTANTS.FLAG_THREE
                        p!!.putValue("resourceType", "Apps")
                        p4!!.putValue("resourceType", "Apps")
                    }
                    3 -> {
                        tabFlag = CONSTANTS.FLAG_FOUR
                        p!!.putValue("resourceType", "Websites")
                        p4!!.putValue("resourceType", "Websites")
                    }
                    4 -> {
                        tabFlag = CONSTANTS.FLAG_FIVE
                        p!!.putValue("resourceType", "Documentaries")
                        p4!!.putValue("resourceType", "Documentaries")
                    }
                }
                val listCalls =
                    APINewClient.getClient().getResourceList(coUserId, tabFlag, category)
                listCalls.enqueue(object : Callback<ResourceListModel?> {
                    override fun onResponse(
                        call: Call<ResourceListModel?>,
                        response: Response<ResourceListModel?>
                    ) {
                        try {
                            val listModel = response.body()!!
                            if (listModel.responseCode.equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                resourceListModel = listModel
                                val allResourceType = ArrayList<String>()
                                val gson = Gson()
                                allResourceType.add("Audio Books")
                                allResourceType.add("Podcasts")
                                allResourceType.add("Apps")
                                allResourceType.add("Websites")
                                allResourceType.add("Documentaries")
                                p!!.putValue("allResourceType", gson.toJson(allResourceType))
                                val section1 = ArrayList<SegmentResource>()
                                val e = SegmentResource()
                                val gsons = Gson()
                                for (i in resourceListModel!!.responseData!!.indices) {
                                    e.resourceId = resourceListModel!!.responseData!![i].iD
                                    e.resourceName = resourceListModel!!.responseData!![i].title
                                    e.author = resourceListModel!!.responseData!![i].author
                                    e.masterCategory =
                                        resourceListModel!!.responseData!![i].masterCategory
                                    section1.add(e)
                                }
                                p!!.putValue("resources", gsons.toJson(section1))
                                BWSApplication.addToSegment(
                                    "Resources Screen Viewed",
                                    p,
                                    CONSTANTS.screen
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResourceListModel?>, t: Throwable) {}
                })
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setAdapter()
        li = LayoutInflater.from(activity)
        promptsView = li.inflate(R.layout.resource_filter_menu, null)
        dialogBox = Dialog(activity, R.style.AppCompatAlertDialogStyle)
        val window = dialogBox!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        dialogBox!!.setContentView(promptsView)
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM or Gravity.END
        dialogBox!!.window!!.decorView.bottom = 100
        dialogBox!!.window!!.decorView.right = 100
        dialogBox!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        wlp.y = 190
        wlp.x = 33
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        dialogBox!!.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish()
            }
            false
        }
        rvFilterList = promptsView.findViewById(R.id.rvFilterList)
        ivFilter = promptsView.findViewById(R.id.ivFilter)
        tvAll = promptsView.findViewById(R.id.tvAll)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rvFilterList.layoutManager = mLayoutManager
        rvFilterList.itemAnimator = DefaultItemAnimator()
        binding.ivFilter.setOnClickListener {
            if (BWSApplication.isNetworkConnected(activity)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                prepareData(rvFilterList, dialogBox, tvAll, ivFilter)
                tvAll.setOnClickListener {
                    category = ""
                    setAdapter()
                    dialogBox!!.dismiss()
                }
                dialogBox!!.show()
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
    }

    override fun onResume() {
        prepareData(rvFilterList, dialogBox, tvAll, ivFilter)
        super.onResume()
    }

    override fun onBackPressed() {
        myBackPress = true
        AudioDownloadsFragment.comefromDownload = "0"
        finish()
    }

    private fun setAdapter() {
        if (BWSApplication.isNetworkConnected(activity)) {
            val adapter = TabAdapter(
                supportFragmentManager, binding.tabLayout.tabCount
            )
            binding.viewPager.adapter = adapter
            binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
            binding.viewPager.currentItem = currentTab
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    fun prepareData(
        rvFilterList: RecyclerView?,
        dialogBox: Dialog?,
        tvAll: TextView?,
        ivFilter: ImageView?
    ) {
        try {
            if (BWSApplication.isNetworkConnected(ctx)) {
                val listCall = APINewClient.getClient().getResourceCatList(coUserId)
                listCall.enqueue(object : Callback<ResourceFilterModel?> {
                    override fun onResponse(
                        call: Call<ResourceFilterModel?>,
                        response: Response<ResourceFilterModel?>
                    ) {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            val adapter = ResourceFilterAdapter(
                                listModel.responseData,
                                dialogBox,
                                tvAll,
                                ivFilter
                            )
                            rvFilterList!!.adapter = adapter
                            for (i in listModel.responseData!!.indices) {
                                section!!.add(listModel.responseData!![i].categoryName)
                            }
                            p4!!.putValue("allMasterCategory", gson!!.toJson(section))
                        }
                    }

                    override fun onFailure(call: Call<ResourceFilterModel?>, t: Throwable) {}
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ResourceFilterAdapter(
        private val listModel: List<ResourceFilterModel.ResponseData>?,
        private var dialogBox: Dialog?,
        private val tvAll: TextView?,
        var ivFilter: ImageView?
    ) : RecyclerView.Adapter<ResourceFilterAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FilterListLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.filter_list_layout, parent, false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            ivFilter!!.visibility = View.INVISIBLE
            holder.binding.tvTitle.text = listModel!![position].categoryName
            holder.binding.llMainLayout.setOnClickListener {
                holder.binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.app_theme_color
                    )
                )
                holder.binding.ivFiltered.visibility = View.VISIBLE
                category = listModel[position].categoryName
                setAdapter()
                dialogBox!!.dismiss()
                p4!!.putValue("masterCategory", listModel[position].categoryName)
                BWSApplication.addToSegment("Resources Filter Clicked", p4, CONSTANTS.screen)
            }
            if (listModel[position].categoryName.equals(category, ignoreCase = true)) {
                ivFilter!!.visibility = View.INVISIBLE
                tvAll!!.setTextColor(
                    ContextCompat.getColor(
                        activity, R.color.black
                    )
                )
                holder.binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        activity, R.color.app_theme_color
                    )
                )
                holder.binding.ivFiltered.visibility = View.VISIBLE
            } else if (category.equals("", ignoreCase = true)) {
                tvAll!!.setTextColor(
                    ContextCompat.getColor(
                        activity, R.color.app_theme_color
                    )
                )
                ivFilter!!.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }

        inner class MyViewHolder(var binding: FilterListLayoutBinding) : RecyclerView.ViewHolder(
            binding.root
        )
    }

    class TabAdapter(fm: FragmentManager, var totalTabs: Int) : FragmentPagerAdapter(fm) {
        var act = ResourceActivity()
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val audioBooksFragment = AudioBooksFragment()
                    val bundle = Bundle()
                    bundle.putString("audio_books", "audio_books")
                    bundle.putString("Category", act.category)
                    audioBooksFragment.arguments = bundle
                    audioBooksFragment
                }
                1 -> {
                    val podcastsFragment = PodcastsFragment()
                    val bundle = Bundle()
                    bundle.putString("podcasts", "podcasts")
                    bundle.putString("Category", act.category)
                    podcastsFragment.arguments = bundle
                    podcastsFragment
                }
                2 -> {
                    val appsFragment = AppsFragment()
                    val bundle = Bundle()
                    bundle.putString("apps", "apps")
                    bundle.putString("Category", act.category)
                    appsFragment.arguments = bundle
                    appsFragment
                }
                3 -> {
                    val websiteFragment = WebsiteFragment()
                    val bundle = Bundle()
                    bundle.putString("website", "website")
                    bundle.putString("Category", act.category)
                    websiteFragment.arguments = bundle
                    websiteFragment
                }
                4 -> {
                    val documentariesFragment = DocumentariesFragment()
                    val bundle = Bundle()
                    bundle.putString("documentaries", "documentaries")
                    bundle.putString("Category", act.category)
                    documentariesFragment.arguments = bundle
                    documentariesFragment
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
}