package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.BeforeAfterComparisionQuestionListModel
import com.brainwellnessspa.dashboardModule.models.SessionStepOneModel
import com.brainwellnessspa.databinding.ActivitySessionComparisonStatusBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionComparisonStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionComparisonStatusBinding
    lateinit var activity: Activity
    var sessionId: String? = ""
    var stepId: String? = ""
    var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_comparison_status)
        activity = this@SessionComparisonStatusActivity

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
            stepId = intent.getStringExtra("StepId")
        }

        prepareData()
    }

    fun prepareData() {
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getBeforeAfterQuestionListing(stepId, sessionId)
            listCall.enqueue(object : Callback<BeforeAfterComparisionQuestionListModel?> {
                override fun onResponse(call: Call<BeforeAfterComparisionQuestionListModel?>, response: Response<BeforeAfterComparisionQuestionListModel?>) {
                    try {
                        val listModel1 = response.body()
                        val response = listModel1?.responseData
                        if (listModel1?.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (response != null) {

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BeforeAfterComparisionQuestionListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
        }
    }
}