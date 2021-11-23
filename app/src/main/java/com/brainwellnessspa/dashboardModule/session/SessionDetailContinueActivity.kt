package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionStepListModel
import com.brainwellnessspa.databinding.ActivitySessionDetailContinueBinding
import com.brainwellnessspa.databinding.SessionDetailLayoutBinding
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

class SessionDetailContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionDetailContinueBinding
    lateinit var activity: Activity
    lateinit var adapter: SessionDetailAdapter
    var userId: String = ""
    var sessionId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_detail_continue)
        activity = this@SessionDetailContinueActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!

        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
        }
        binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        prepareData()
    }

    fun prepareData() {
        if (isNetworkConnected(activity)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getSessionStepList(userId, sessionId)
            listCall.enqueue(object : Callback<SessionStepListModel?> {
                override fun onResponse(call: Call<SessionStepListModel?>, response: Response<SessionStepListModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (response != null) {
                                binding.tvTitle.text = response.sessionTitle
                                binding.tvScreenTitle.text = response.sessionTitle
                                binding.tvshortDesc.text = response.sessionShortDesc
                                binding.tvDesc.text = response.sessionDesc
                                binding.llBack.visibility = View.VISIBLE
                                binding.ivDone.visibility = View.VISIBLE
                                binding.btnContinue.visibility = View.GONE
                                Glide.with(activity).load(response.sessionImg).thumbnail(0.05f)
                                        .apply(RequestOptions.bitmapTransform(RoundedCorners(2)))
                                        .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner)

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
                                adapter = SessionDetailAdapter(binding, response.data, activity)
                                binding.rvList.adapter = adapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionStepListModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(activity.getString(R.string.no_server_found), activity)
        }
    }

    class SessionDetailAdapter(var binding: ActivitySessionDetailContinueBinding, var catName: List<SessionStepListModel.ResponseData.Data>?, val activity: Activity) : RecyclerView.Adapter<SessionDetailAdapter.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: SessionDetailLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionDetailLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_detail_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val db = catName!![position]

            holder.bindingAdapter.tvNumber.text = db.stepId
            holder.bindingAdapter.tvTitle.text = db.desc

            Glide.with(activity).load(db.statusImg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(2)))
                    .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivAction)

            when {
                db.userStepStatus.equals("Completed") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_complete_selected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_dark_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(activity, R.color.session_progress))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE
                }
                db.userStepStatus.equals("Inprogress") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_selected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_gray_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(activity, R.color.lighted_gray))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE /* VISIBLE */
                }
                db.userStepStatus.equals("Lock") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.session_gray_bg)
                    holder.bindingAdapter.viewDown.setBackgroundColor(ContextCompat.getColor(activity, R.color.lighted_gray))
                    holder.bindingAdapter.tvNumber.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
                    holder.bindingAdapter.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
                    holder.bindingAdapter.ivDownload.visibility = View.GONE
                }
            }

            holder.bindingAdapter.llClicked.setOnClickListener {
                when (db.stepId) {
                    "1" -> {
                        val i = Intent(activity, SessionAudiosActivity::class.java)
                        i.putExtra("SessionId", db.sessionId)
                        i.putExtra("StepId", db.stepId)
                        activity.startActivity(i)
                    }
                    "2" -> {
                        /*Progress report */
                    }
                    "3" -> {
                        /*Session Activities not add in current flow */
                    }
                    "4" -> {
                        /*Before Comparison */
                        val i = Intent(activity, BrainStatusActivity::class.java)
                        i.putExtra("SessionId", db.sessionId)
                        i.putExtra("StepId", db.stepId)
                        i.putExtra("Type", "before")
                        activity.startActivity(i)
                    }
                    "5" -> {
                        val i = Intent(activity, SessionAudiosActivity::class.java)
                        i.putExtra("SessionId", db.sessionId)
                        i.putExtra("StepId", db.stepId)
                        activity.startActivity(i)
                    }
                    "6" -> {
                        val i = Intent(activity, BrainStatusActivity::class.java)
                        i.putExtra("SessionId", db.sessionId)
                        i.putExtra("StepId", db.stepId)
                        i.putExtra("Type", "after")
                        activity.startActivity(i)
                    }
                    "7" -> {
                        val i = Intent(activity, SessionAudiosActivity::class.java)
                        i.putExtra("SessionId", db.sessionId)
                        i.putExtra("StepId", db.stepId)
                        activity.startActivity(i)
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

        override fun getItemCount(): Int {
            return catName!!.size
        }
    }
}