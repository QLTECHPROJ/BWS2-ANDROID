package com.brainwellnessspa.FaqModule.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardModule.Account.AccountFragment
import com.brainwellnessspa.FaqModule.Models.FaqListModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAudioFaqBinding
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import java.util.*

class AudioFaqActivity : AppCompatActivity() {
    lateinit var binding: ActivityAudioFaqBinding
    lateinit var ctx: Context
    var adapter: AudioFaqAdapter? = null
    var faqListModel: ArrayList<FaqListModel.ResponseData>? = null
    var Flag: String? = null
    var UserID: String? = null
    var section: ArrayList<String>? = null
    var gsonBuilder: GsonBuilder? = null
    var gson: Gson? = null
    var p: Properties? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_faq)
        ctx = this@AudioFaqActivity
        faqListModel = ArrayList()
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (intent != null) {
            faqListModel = intent.getParcelableArrayListExtra("faqListModel")
            Flag = intent.getStringExtra("Flag")
        }
        binding.llBack.setOnClickListener { view: View? ->
            myBackPress = true
            AccountFragment.ComeScreenAccount = 1
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        section = ArrayList()
        gsonBuilder = GsonBuilder()
        gson = gsonBuilder!!.create()
        if (Flag.equals("Audio", ignoreCase = true)) {
            binding.tvTitle.setText(R.string.Audio)
        } else if (Flag.equals("General", ignoreCase = true)) {
            binding.tvTitle.text = "General"
        } else if (Flag.equals("Playlist", ignoreCase = true)) {
            binding.tvTitle.setText(R.string.Playlist)
        }
        val serachList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvFaqList.layoutManager = serachList
        binding.rvFaqList.itemAnimator = DefaultItemAnimator()
        if (faqListModel!!.size == 0) {
            binding.tvFound.visibility = View.VISIBLE
            binding.rvFaqList.visibility = View.GONE
        } else {
            binding.tvFound.visibility = View.GONE
            binding.rvFaqList.visibility = View.VISIBLE
            adapter = AudioFaqAdapter(faqListModel, ctx, binding.rvFaqList, binding.tvFound)
            binding.rvFaqList.adapter = adapter
        }
    }

    override fun onBackPressed() {
        myBackPress = true
        AccountFragment.ComeScreenAccount = 1
        finish()
    }

    inner class AudioFaqAdapter(private val modelList: List<FaqListModel.ResponseData>?, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<AudioFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudioFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audio_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            p = Properties()
            p!!.putValue("userId", UserID)
            if (Flag.equals("Audio", ignoreCase = true)) {
                p!!.putValue("faqCategory", "Audio")
            } else if (Flag.equals("General", ignoreCase = true)) {
                p!!.putValue("faqCategory", "General")
            } else if (Flag.equals("Playlist", ignoreCase = true)) {
                p!!.putValue("faqCategory", "Playlist")
            }
            for (i in modelList!!.indices) {
                section!!.add(modelList[position].title.toString())
                section!!.add(modelList[position].title.toString())
            }
            p!!.putValue("faqDescription", modelList[position].desc)
            BWSApplication.addToSegment("FAQ Clicked", p, CONSTANTS.screen)
            holder.binding.tvTitle.text = modelList[position].title
            holder.binding.tvDesc.text = modelList[position].desc
            holder.binding.ivClickRight.setOnClickListener { view: View? ->
                myBackPress = true
                holder.binding.tvTitle.setTextColor(resources.getColor(R.color.white))
                holder.binding.tvDesc.isFocusable = true
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.visibility = View.VISIBLE
                holder.binding.ivClickRight.visibility = View.GONE
                holder.binding.ivClickDown.visibility = View.VISIBLE
                holder.binding.llBgChange.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_clicked)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_white_arrow_down_icon)
            }
            holder.binding.ivClickDown.setOnClickListener { view: View? ->
                myBackPress = true
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.tvTitle.setTextColor(resources.getColor(R.color.light_black))
                holder.binding.tvDesc.visibility = View.GONE
                holder.binding.ivClickRight.visibility = View.VISIBLE
                holder.binding.ivClickDown.visibility = View.GONE
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_right_gray_arrow_icon)
            }
            if (modelList.size == 0) {
                tvFound.visibility = View.VISIBLE
                rvFaqList.visibility = View.GONE
            } else {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return modelList!!.size
        }

        inner class MyViewHolder(var binding: AudioFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)
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
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}