package com.brainwellnessspa.dashboardModule.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.NotificationlistModel
import com.brainwellnessspa.databinding.ActivityNotificationListBinding
import com.brainwellnessspa.databinding.NotificationListLayoutBinding
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
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
    var userId: String? = ""
    var coUserId: String? = ""
    var userName: String? = ""
    lateinit var ctx: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        activity = this@NotificationListActivity
        ctx = this@NotificationListActivity
        binding.llBack.setOnClickListener {
            finish()
        }

        val p = Properties()
        addToSegment("Notification List Viewed", p, CONSTANTS.screen)
        binding.llError.visibility = View.GONE

        val unicode = 0x1F917
        val textIcon = String(Character.toChars(unicode))
        binding.tvFound.text = "Welcome $userName! Let's get your wellness journey going! $textIcon"
        prepareNotiData()
    }

    private fun prepareNotiData() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<NotificationlistModel> = APINewClient.client.getNotificationlist(coUserId)
            listCall.enqueue(object : Callback<NotificationlistModel> {
                override fun onResponse(call: Call<NotificationlistModel>, response: Response<NotificationlistModel>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: NotificationlistModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                            binding.rvNotiList.layoutManager = LinearLayoutManager(this@NotificationListActivity)
                            if (listModel.responseData!!.isEmpty()) {
                                binding.llError.visibility = View.VISIBLE
                                binding.rvNotiList.visibility = View.GONE
                            } else {
                                binding.llError.visibility = View.GONE
                                binding.rvNotiList.visibility = View.VISIBLE
                                adapter = NotiListAdapter(listModel.responseData!!, activity)
                                binding.rvNotiList.adapter = adapter
                            }
                        } else if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted))) {
                            deleteCall(activity)
                            showToast(listModel.responseMessage, activity)
                            val i = Intent(activity, SignInActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            i.putExtra("mobileNo", "")
                            i.putExtra("countryCode", "")
                            i.putExtra("name", "")
                            i.putExtra("email", "")
                            i.putExtra("countryShortName", "")
                            startActivity(i)
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<NotificationlistModel>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    class NotiListAdapter(private val listModel: List<NotificationlistModel.ResponseData?>, var activity: Activity) : RecyclerView.Adapter<NotiListAdapter.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: NotificationListLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: NotificationListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Glide.with(activity).load(listModel[position]!!.image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(18))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivImage)
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