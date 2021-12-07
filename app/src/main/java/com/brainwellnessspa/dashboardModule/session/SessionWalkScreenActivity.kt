package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.StepTypeTwoSaveDataModel
import com.brainwellnessspa.databinding.ActivitySessionWalkScreenBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionWalkScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionWalkScreenBinding
    lateinit var activity: Activity

    var listModel = StepTypeTwoSaveDataModel.ResponseData()
    var sessionId: String? = ""
    var stepId:String? = ""
    var nextForm:String? = ""
    var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_walk_screen)
        activity = this@SessionWalkScreenActivity
        if (intent.extras != null) {
            nextForm = intent.getStringExtra("nextForm").toString()
            sessionId = intent.getStringExtra("SessionId").toString()
            stepId = intent.getStringExtra("StepId").toString()
            val json = intent.getStringExtra("Data").toString()
            val type1 = object : TypeToken<StepTypeTwoSaveDataModel.ResponseData>() {}.type
            listModel = gson.fromJson(json, type1)
        }
        binding.tvSection.text = listModel.sectionTitle
        Glide.with(activity).load(listModel.sectionImage).thumbnail(0.01f)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2)))
            .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.sessionImg)

        binding.tvTitle.text = listModel.sectionSubtitle
        binding.tvSessionDesc.text = listModel.sectionDescription +   listModel.sectionDescription +   listModel.sectionDescription

        binding.rlSectionOne.setOnClickListener {
            if(!nextForm.equals("personal_query")){
                val i = Intent(activity, SessionProgressReportActivity::class.java)
                i.putExtra("Data",gson.toJson(listModel))
                i.putExtra("nextForm",nextForm)
                i.putExtra("SessionId",sessionId)
                i.putExtra("StepId", stepId)
                activity.startActivity(i)
                finish()
            }else if(nextForm.equals("personal_query")){
                val i = Intent(activity, SessionPersonalHistoryActivity::class.java)
                i.putExtra("Data",gson.toJson(listModel))
                i.putExtra("nextForm",nextForm)
                i.putExtra("SessionId",sessionId)
                i.putExtra("StepId", stepId)
                activity.startActivity(i)
                finish()
            }
        }
        val lineCount: Int = binding.tvSessionDesc.lineCount
        if (lineCount >= 2) {
            binding.tvReadMore.visibility = View.VISIBLE
        } else {
            binding.tvReadMore.visibility = View.GONE
        }
        binding.tvReadMore.setOnClickListener {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dass_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvDesc.text = listModel.sectionDescription

            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }

            tvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }
    }
}