package com.brainwellnessspa.resourceModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityResourceDetailsBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.segment.analytics.Properties

class ResourceDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityResourceDetailsBinding
    lateinit var act: Activity
    var id: String? = ""
    var title: String? = ""
    var author: String? = ""
    var linkOne: String? = ""
    var linkTwo: String? = ""
    var image: String? = ""
    var description: String? = ""
    var resourceType: String? = ""
    var userID: String? = ""
    private var coUserID: String? = ""
    var mastercat: String? = ""
    var subcat: String? = ""
    var ctx: Context? = null
    lateinit var p: Properties
    private lateinit var p1: Properties
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource_details)
        ctx = this@ResourceDetailsActivity
        act = this@ResourceDetailsActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        userID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        coUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        if (intent.extras != null) {
            id = intent.getStringExtra("id")
            title = intent.getStringExtra(CONSTANTS.title)
            author = intent.getStringExtra(CONSTANTS.author)
            linkOne = intent.getStringExtra(CONSTANTS.linkOne)
            linkTwo = intent.getStringExtra(CONSTANTS.linkTwo)
            image = intent.getStringExtra(CONSTANTS.image)
            description = intent.getStringExtra(CONSTANTS.description)
            mastercat = intent.getStringExtra(CONSTANTS.mastercat)
            subcat = intent.getStringExtra(CONSTANTS.subcat)
            p = Properties()
            p1 = Properties()
            p.putValue("coUserId", coUserID)
            p.putValue("resourceId", id)
            p.putValue("resourceName", title)
            if (intent.getStringExtra("audio_books") != null) {
                binding.tvScreenName.setText(R.string.Audio_Book)
                binding.btnComplete.visibility = View.VISIBLE
                binding.llPlatfroms.visibility = View.GONE
                resourceType = getString(R.string.Audio_Book)
                p.putValue("author", author)
                p1.putValue("author", author)
            }
            if (intent.getStringExtra("podcasts") != null) {
                binding.tvScreenName.setText(R.string.Podcasts)
                binding.btnComplete.visibility = View.VISIBLE
                binding.llPlatfroms.visibility = View.GONE
                resourceType = getString(R.string.Podcasts)
                p.putValue("author", author)
                p1.putValue("author", author)
            }
            if (intent.getStringExtra("apps") != null) {
                binding.tvScreenName.setText(R.string.Apps)
                binding.btnComplete.visibility = View.GONE
                binding.llPlatfroms.visibility = View.VISIBLE
                binding.tvCreator.visibility = View.GONE
                resourceType = getString(R.string.Apps)
                p.putValue("author", "")
                p1.putValue("author", "")
            }
            if (intent.getStringExtra("website") != null) {
                binding.tvScreenName.setText(R.string.Websites)
                binding.btnComplete.visibility = View.VISIBLE
                binding.llPlatfroms.visibility = View.GONE
                binding.tvCreator.visibility = View.GONE
                resourceType = getString(R.string.Websites)
                p.putValue("author", "")
                p1.putValue("author", "")
            }
            if (intent.getStringExtra("documentaries") != null) {
                binding.tvScreenName.setText(R.string.Documentaries)
                binding.btnComplete.visibility = View.VISIBLE
                binding.llPlatfroms.visibility = View.GONE
                resourceType = getString(R.string.Documentaries)
                p.putValue("author", author)
                p1.putValue("author", author)
            }
            try {
                p.putValue("resourceType", resourceType)
                p.putValue("resourceDesc", description)
                p.putValue("masterCategory", mastercat)
                p.putValue("subCategory", subcat)
                if (linkOne.equals("", ignoreCase = true)) {
                    p.putValue("resourceLink", linkTwo)
                } else if (linkTwo.equals("", ignoreCase = true)) {
                    p.putValue("resourceLink", linkOne)
                }
                BWSApplication.addToSegment("Resource Details Viewed", p, CONSTANTS.screen)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.tvTitle.text = title
            binding.tvCreator.text = author
            binding.tvSubTitle.text = description
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.44f, 0f)
            binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(this).load(image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).apply(RequestOptions.bitmapTransform(RoundedCorners(40))).priority(Priority.HIGH).skipMemoryCache(false).into(binding.ivRestaurantImage)
            binding.btnComplete.setOnClickListener {
                if (linkOne.equals("", ignoreCase = true)) {
                    BWSApplication.showToast("Not Available", act)
                } else {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(linkOne)
                    startActivity(i)
                    p1.putValue("coUserId", coUserID)
                    p1.putValue("resourceId", id)
                    p1.putValue("resourceName", title)
                    p1.putValue("resourceType", resourceType)
                    p1.putValue("resourceDesc", description)
                    p1.putValue("resourceLink", linkOne)
                    p1.putValue("masterCategory", mastercat)
                    p1.putValue("subCategory", subcat)
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track)
                }
            }
            binding.ivAndroid.setOnClickListener {
                if (linkOne.equals("", ignoreCase = true)) {
                    BWSApplication.showToast("Not Available", act)
                } else {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(linkOne)
                    startActivity(i)
                    p1.putValue("coUserId", coUserID)
                    p1.putValue("resourceId", id)
                    p1.putValue("resourceName", title)
                    p1.putValue("resourceType", resourceType)
                    p1.putValue("resourceDesc", description)
                    p1.putValue("resourceLink", linkOne)
                    p1.putValue("masterCategory", mastercat)
                    p1.putValue("subCategory", subcat)
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track)
                }
            }
            binding.ivIos.setOnClickListener {
                if (linkTwo.equals("", ignoreCase = true)) {
                    BWSApplication.showToast("Not Available", act)
                } else {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(linkTwo)
                    startActivity(i)
                    p1.putValue("coUserId", coUserID)
                    p1.putValue("resourceId", id)
                    p1.putValue("resourceName", title)
                    p1.putValue("resourceType", resourceType)
                    p1.putValue("resourceDesc", description)
                    p1.putValue("resourceLink", linkTwo)
                    p1.putValue("masterCategory", mastercat)
                    p1.putValue("subCategory", subcat)
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track)
                }
            }
        }
        binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
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
                notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}