package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionStepListModel
import com.brainwellnessspa.dashboardModule.models.SessionStepOneModel
import com.brainwellnessspa.databinding.ActivitySessionAudiosBinding
import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.CONSTANTS.position
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionAudiosActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionAudiosBinding
    lateinit var activity: Activity
    var sessionId: String? = ""
    var stepId: String? = ""
    var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_audios)
        activity = this@SessionAudiosActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
            stepId = intent.getStringExtra("StepId")
        }

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            val i = Intent(activity, WalkScreenActivity::class.java)
            i.putExtra(CONSTANTS.ScreenView,"5")
            activity.startActivity(i)
        }
        prepareData()
    }

    fun prepareData() {
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getEEPStepTypeOneData(userId, stepId, sessionId)
            listCall.enqueue(object : Callback<SessionStepOneModel?> {
                override fun onResponse(call: Call<SessionStepOneModel?>, response: Response<SessionStepOneModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (response != null) {
                                binding.tvTitle.text = response.sessionTitle
                                binding.tvSessionTitle.text = Html.fromHtml(response.stepShortDescription)
                                binding.tvDesc.text = Html.fromHtml(response.stepLongDescription)
                                binding.tvAudioName.text = response.stepAudio?.name
                                binding.tvAudioTime.text = response.stepAudio?.audioDuration
                                Glide.with(activity).load(response.stepAudio?.imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivAudioImage)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionStepOneModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}