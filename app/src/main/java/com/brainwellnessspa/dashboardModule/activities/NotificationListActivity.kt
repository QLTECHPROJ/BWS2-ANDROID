package com.brainwellnessspa.dashboardModule.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.dashboardModule.models.NotificationlistModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityNotificationListBinding
import com.brainwellnessspa.databinding.NotificationListLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationListBinding
    lateinit var adapter: NotiListAdapter
    lateinit var activity: Activity
    var USERID: String? = null
    var CoUserID: String? = null
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)
        activity = this@NotificationListActivity
        val shared1: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        ctx = this@NotificationListActivity
        binding.llBack.setOnClickListener {
            finish()
        }
        val p = Properties()
        p.putValue("coUserId", CoUserID)
        BWSApplication.addToSegment("Notification List Viewed", p, CONSTANTS.screen)
        binding.llError.visibility = View.GONE
        binding.tvFound.text = "No result found"
        prepareNotiData()
    }

    private fun prepareNotiData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<NotificationlistModel> =
                APINewClient.getClient().getNotificationlist(USERID, CoUserID)
            listCall.enqueue(object : Callback<NotificationlistModel> {
                override fun onResponse(
                    call: Call<NotificationlistModel>,
                    response: Response<NotificationlistModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: NotificationlistModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            binding.rvNotiList.layoutManager =
                                LinearLayoutManager(this@NotificationListActivity)
                            if (listModel.responseData!!.size == 0) {
                                binding.llError.visibility = View.VISIBLE
                                binding.rvNotiList.visibility = View.GONE
                            } else {
                                binding.llError.visibility = View.GONE
                                binding.rvNotiList.visibility = View.VISIBLE
                                adapter = NotiListAdapter(listModel.responseData!!, activity)
                                binding.rvNotiList.adapter = adapter
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<NotificationlistModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class NotiListAdapter(
        listModel: List<NotificationlistModel.ResponseData?>,
        activity: Activity
    ) :
        RecyclerView.Adapter<NotiListAdapter.MyViewHolder>() {
        private val listModel: List<NotificationlistModel.ResponseData?> = listModel
        var activity: Activity = activity

        inner class MyViewHolder(bindingAdapter: NotificationListLayoutBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root) {
            var bindingAdapter: NotificationListLayoutBinding = bindingAdapter
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: NotificationListLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.notification_list_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Glide.with(activity).load(listModel[position]!!.image).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(18))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.bindingAdapter.ivImage)
            holder.bindingAdapter.tvTitle.text = listModel[position]!!.msg
            holder.bindingAdapter.tvDesc.text = listModel[position]!!.desc
            holder.bindingAdapter.tvTime.text = listModel[position]!!.durationTime
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    override fun onBackPressed() {
        finish()
    }
}