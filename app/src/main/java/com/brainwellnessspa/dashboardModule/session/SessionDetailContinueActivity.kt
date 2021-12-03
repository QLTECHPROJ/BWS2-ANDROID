package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.ActivitySessionDetailContinueBinding
import com.brainwellnessspa.databinding.SessionDetailLayoutBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionDetailContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionDetailContinueBinding
    lateinit var act: Activity
    lateinit var adapter: SessionDetailAdapter
    var userId: String = ""
    var sessionId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_detail_continue)
        act = this@SessionDetailContinueActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!

        binding.llBack.setOnClickListener {
            finish()
        }
        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
        }
        binding.rvList.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)
        prepareData()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun prepareData() {
        if (isNetworkConnected(act)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getSessionStepList(userId, sessionId)
            listCall.enqueue(object : Callback<SessionStepListModel?> {
                override fun onResponse(call: Call<SessionStepListModel?>, response: Response<SessionStepListModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                binding.tvTitle.text = response.sessionTitle
                                binding.tvScreenTitle.text = response.sessionTitle
                                binding.tvshortDesc.text = response.sessionShortDesc
                                binding.tvDesc.text = response.sessionDesc
                                binding.llBack.visibility = View.VISIBLE
                                binding.ivDone.visibility = View.VISIBLE
                                binding.btnContinue.visibility = View.GONE
                                Glide.with(act).load(response.sessionImg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(2))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner)

                                when {
                                    response.sessionProgress.equals("Great") -> {
                                        binding.llGreatProgress.visibility = View.VISIBLE
                                        binding.llSlowProgress.visibility = View.GONE
                                    }
                                    response.sessionProgress.equals("Slow") -> {
                                        binding.llGreatProgress.visibility = View.GONE
                                        binding.llSlowProgress.visibility = View.VISIBLE
                                    }
                                    else -> {
                                        binding.llGreatProgress.visibility = View.GONE
                                        binding.llSlowProgress.visibility = View.GONE
                                    }
                                }
                                adapter = SessionDetailAdapter(binding, response.data, act, userId, sessionId)
                                binding.rvList.adapter = adapter
                            }
                        } else if (listModel.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                            callDelete403(act, listModel.responseMessage)
                        } else {
                            showToast(listModel.responseMessage, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionStepListModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            showToast(act.getString(R.string.no_server_found), act)
        }
    }

    fun callCheckProgressReport(act: Activity, sessionId: String?, stepId: String?, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        if (isNetworkConnected(act)) {
            showProgressBar(progressBar, progressBarHolder, act)
            val shared1 = act.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
            userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
            val listCall = APINewClient.client.getCheckProgressReportStatus(userId, sessionId, stepId)
            listCall.enqueue(object : Callback<CheckProgressReportStatusModel?> {
                override fun onResponse(call: Call<CheckProgressReportStatusModel?>, response: Response<CheckProgressReportStatusModel?>) {
                    try {
                        val listModel1 = response.body()
                        val response = listModel1?.responseData
                        if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            if (response != null) {
                                if (response.nextForm != "") {
                                    val listCall = APINewClient.client.getSessionProgressReport(sessionId, stepId, response.nextForm)
                                    listCall.enqueue(object : Callback<StepTypeTwoSaveDataModel?> {
                                        override fun onResponse(call: Call<StepTypeTwoSaveDataModel?>, response2: Response<StepTypeTwoSaveDataModel?>) {
                                            try {
                                                val listModel2 = response2.body()
                                                val response1 = listModel2?.responseData
                                                if (listModel2?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                    hideProgressBar(progressBar, progressBarHolder, act)
                                                    if (response1 != null) {
                                                        val gson = Gson()
                                                        /* TODO Progress report
                                                        *  TODO Progress report
                                                        *   nextForm key use open next screen
                                                        *   key                        act name
                                                        *   wellness_assessment        SessionWellnessAssessmentActivity
                                                        *   perception                 SessionPerceptionsActivity
                                                        *   mental_health              SessionMentalHealthActivity
                                                        *   dass21                     SessionAssessmentActivity
                                                        *   penn_state_worry           SessionPennStateWorryActivity
                                                        *   self_esteem                SessionSelfScaleActivity
                                                        *   aggression_query           AggressionQuestionsActivity
                                                        *   personal_query             SessionPersonalHistoryActivity*/
                                                        val i = Intent(act, SessionWalkScreenActivity::class.java)
                                                        i.putExtra("Data", gson.toJson(response1))
                                                        i.putExtra("nextForm", response.nextForm)
                                                        i.putExtra("SessionId", sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                        act.finish()
                                                        /*else if(response.nextForm.equals("wellness_assessment")){
                                                        val i = Intent(act, SessionWellnessAssessmentActivity::class.java)
                                                        i.putExtra("Data",gson.toJson(response1))
                                                        i.putExtra("nextForm",gson.toJson(response.nextForm))
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("perception")){
                                                        val i = Intent(act, SessionPerceptionsActivity::class.java)
                                                        i.putExtra("SessionId", sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("mental_health")){
                                                        val i = Intent(act, SessionMentalHealthActivity::class.java)
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("dass21")){
                                                        val i = Intent(act, SessionAssessmentActivity::class.java)
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("penn_state_worry")){
                                                        val i = Intent(act, SessionPennStateWorryActivity::class.java)
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("self_esteem")){
                                                        val i = Intent(act, SessionSelfScaleActivity::class.java)
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }else if(response.nextForm.equals("aggression_query")){
                                                        val i = Intent(act, AggressionQuestionsActivity::class.java)
                                                        i.putExtra("SessionId",sessionId)
                                                        i.putExtra("StepId", stepId)
                                                        act.startActivity(i)
                                                    }*/
                                                    }
                                                } else if (listModel2!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                                                    callDelete403(act, listModel2.responseMessage)
                                                } else {
                                                    showToast(listModel2.responseMessage, act)
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }

                                        override fun onFailure(call: Call<StepTypeTwoSaveDataModel?>, t: Throwable) {
                                            hideProgressBar(progressBar, progressBarHolder, act)
                                        }
                                    })
                                } else {
                                    if (isNetworkConnected(act)) {
                                        val listCall = APINewClient.client.getSessionStepStatusList(userId, sessionId, stepId)
                                        listCall.enqueue(object : Callback<SessionStepStatusListModel?> {
                                            override fun onResponse(call: Call<SessionStepStatusListModel?>, response: Response<SessionStepStatusListModel?>) {
                                                try {
                                                    val listModel = response.body()
                                                    val response = listModel?.responseData
                                                    if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                        act.finish()
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            override fun onFailure(call: Call<SessionStepStatusListModel?>, t: Throwable) {
                                            }
                                        })
                                    } else {
                                        showToast(act.getString(R.string.no_server_found), act)
                                    }
                                    finish()
                                }
                            }
                        } else if (listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                            callDelete403(act, listModel1.responseMessage)
                        } else {
                            showToast(listModel1.responseMessage, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CheckProgressReportStatusModel?>, t: Throwable) {
                    hideProgressBar(progressBar, progressBarHolder, act)
                }
            })
        } else {
            showToast(act.getString(R.string.no_server_found), act)
        }
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    class SessionDetailAdapter(var binding: ActivitySessionDetailContinueBinding, var catName: List<SessionStepListModel.ResponseData.Data>?, val act: Activity, var userId: String?, var sessionId: String?) : RecyclerView.Adapter<SessionDetailAdapter.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: SessionDetailLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionDetailLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_detail_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val db = catName!![position]

            holder.bindingAdapter.tvNumber.text = (position + 1).toString()
            holder.bindingAdapter.tvTitle.text = db.desc

            Glide.with(act).load(db.statusImg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(2))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivAction)

            when {
                db.userStepStatus.equals("Completed") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_complete_selected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_dark_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(act, R.color.session_progress))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(act, R.color.white))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(act, R.color.white))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE
                }
                db.userStepStatus.equals("Inprogress") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_selected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_gray_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(act, R.color.lighted_gray))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(act, R.color.light_black))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(act, R.color.light_black))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE /* VISIBLE */
                }
                db.userStepStatus.equals("Lock") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_gray_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(act, R.color.lighted_gray))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(act, R.color.light_black))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(act, R.color.light_black))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE
                }
            }

            holder.bindingAdapter.llBorder.setOnClickListener {
                when (db.stepId) {
                    "1" -> {
                        /* TODO Welcome & Session Description */
                        callAudioActivity(db.sessionId, db.stepId)
                    }
                    "2" -> {
                        /* TODO IMPORTANT FOR FORAM
                        *  TODO Progress report
                        *   nextForm key use open next screen */
                        val sd = SessionDetailContinueActivity()
                        sd.callCheckProgressReport(act, db.sessionId, db.stepId, binding.progressBar, binding.progressBarHolder)
                    }
                    "3" -> {
                        /* TODO Session Activities not add in current flow */
                    }
                    "4" -> {
                        BeforeAfterQuestionnaires(db.sessionId, db.stepId, db.userStepStatus)
                    }
                    "5" -> {
                        /* TODO Actual Session */
                        callAudioActivity(db.sessionId, db.stepId)
                    }
                    "6" -> {
                        BeforeAfterQuestionnaires(db.sessionId, db.stepId, db.userStepStatus)
                    }
                    "7" -> {
                        /* TODO Pre Session Audio for session 2 */
                        callAudioActivity(db.sessionId, db.stepId)
                    }
                }
            }

            if (catName!!.size == 1) {
                holder.bindingAdapter.viewDown.visibility = View.GONE
            } else {
                holder.bindingAdapter.viewDown.visibility = View.VISIBLE
            }

            if (position == (catName!!.size - 1)) {
                holder.bindingAdapter.viewDown.visibility = View.GONE
            }
        }

        private fun callAudioActivity(sessionId: String?, stepId: String?) {

            val i = Intent(act, SessionAudiosActivity::class.java)
            i.putExtra("SessionId", sessionId)
            i.putExtra("StepId", stepId)
            act.startActivity(i)

        }

        private fun BeforeAfterQuestionnaires(sessionId: String?, stepId: String?, userStepStatus: String?) {
            /* TODO IMPORTANT FOR FORAM
            *   Before Comparison
            *   API call  getCheckBeforeAfterFeelingStatus
            *   key use purpose feeling_status = "0"(BrainStatusActivity) "1"(SessionAudiosActivity(Actual Session open))
            *   key use purpose question_status = "0"(SessionComparisonStatusActivity) "1"(SessionAudiosActivity(Actual Session open))
            *   key use purpose question_status = "1"() feeling_status = "1"()  (SessionAudiosActivity(Actual Session open)) */
            /* TODO IMPORTANT FOR FORAM
            *   After Comparison
            *   API call  getCheckBeforeAfterFeelingStatus
            *   key use purpose feeling_status = "0"(BrainStatusActivity) "1"(SessionAudiosActivity(Actual Session open))
            *   key use purpose question_status = "0"(SessionComparisonStatusActivity)  "1"(SessionAudiosActivity(Actual Session open))
            *   key use purpose question_status = "1" feeling_status = "1"  (SessionAudiosActivity(Actual Session open)) */

            if (isNetworkConnected(act)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, act)
                val listCall = APINewClient.client.getCheckBeforeAfterFeelingStatus(userId, sessionId, stepId)
                listCall.enqueue(object : Callback<BeforeAfterComparisionFetchStatusModel?> {
                    override fun onResponse(call: Call<BeforeAfterComparisionFetchStatusModel?>, response: Response<BeforeAfterComparisionFetchStatusModel?>) {
                        try {
                            val listModel1 = response.body()
                            val response = listModel1?.responseData
                            if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                                if (response != null) {
                                    if (response.questionStatus.equals("0")) {
                                        val i = Intent(act, SessionComparisonStatusActivity::class.java)
                                        i.putExtra("SessionId", sessionId)
                                        i.putExtra("StepId", stepId)
                                        act.startActivity(i)
                                    } else if (response.feelingStatus.equals("0")) {
                                        val i = Intent(act, BrainStatusActivity::class.java)
                                        i.putExtra("SessionId", sessionId)
                                        i.putExtra("StepId", stepId)
                                        i.putExtra("Type", "before")
                                        act.startActivity(i)
                                    } else if (userStepStatus.equals("Inprogress")) {
                                        if (isNetworkConnected(act)) {
                                            val listCall = APINewClient.client.getSessionStepStatusList(userId, sessionId, stepId)
                                            listCall.enqueue(object : Callback<SessionStepStatusListModel?> {
                                                override fun onResponse(call: Call<SessionStepStatusListModel?>, response: Response<SessionStepStatusListModel?>) {
                                                    try {
                                                        val listModel = response.body()
                                                        val response = listModel?.responseData
                                                        if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                            act.finish()
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }

                                                override fun onFailure(call: Call<SessionStepStatusListModel?>, t: Throwable) {
                                                }
                                            })
                                        } else {
                                            showToast(act.getString(R.string.no_server_found), act)
                                        }
                                    }
                                }
                            } else if (listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                                callDelete403(act, listModel1.responseMessage)
                            } else {
                                showToast(listModel1.responseMessage, act)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<BeforeAfterComparisionFetchStatusModel?>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                    }
                })
            } else {
                showToast(act.getString(R.string.no_server_found), act)
            }

        }

        override fun getItemCount(): Int {
            return catName!!.size
        }
    }
}