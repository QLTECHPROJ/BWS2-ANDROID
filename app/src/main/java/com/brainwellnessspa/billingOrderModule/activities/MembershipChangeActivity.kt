package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.notificationId
import com.brainwellnessspa.BWSApplication.player
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.databinding.ActivityMembershipChangeBinding
import com.brainwellnessspa.databinding.MembershipPlanBinding
import com.brainwellnessspa.membershipModule.activities.OrderSummaryActivity
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.MeasureRatio
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MembershipChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityMembershipChangeBinding
    lateinit var ctx: Context 
    var UserID: String? = null
    var ComeFrom: String? = null
    var activity: Activity? = null
    var membershipPlanAdapter: MembershipPlanAdapter? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    var notificationStatus = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership_change)
        ctx = this@MembershipChangeActivity
        activity = this@MembershipChangeActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.llBack.setOnClickListener { view -> callback() }
        notificationStatus = false
        if (intent != null) {
            ComeFrom = intent.getStringExtra("ComeFrom")
        }
        val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlanList.layoutManager = mLayoutManager1
        binding.rvPlanList.itemAnimator = DefaultItemAnimator()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
    }

    override fun onResume() {
        prepareMembershipData()
        super.onResume()
    }

    override fun onBackPressed() {
        callback()
    }

    private fun callback() {
        myBackPress = true
        if (ComeFrom.equals("Plan", ignoreCase = true)) {
            finish()
        } else if (ComeFrom.equals("", ignoreCase = true)) {
            val i = Intent(ctx, BillingOrderActivity::class.java)
            startActivity(i)
            finish()
        } else {
            val i = Intent(ctx, BillingOrderActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlanListBillingModel> = APINewClient.client.getPlanListBilling(UserID)
            listCall.enqueue(object : Callback<PlanListBillingModel?> {
                override fun onResponse(call: Call<PlanListBillingModel?>, response: Response<PlanListBillingModel?>) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val membershipPlanListModel: PlanListBillingModel? = response.body()
                            if (membershipPlanListModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                binding.tvTitle.text = membershipPlanListModel.responseData!!.title
                                binding.tvDesc.text = membershipPlanListModel.responseData!!.desc
                                val measureRatio: MeasureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                                binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner)
                                membershipPlanAdapter = MembershipPlanAdapter(membershipPlanListModel.responseData!!.plan!!, ctx, binding.btnFreeJoin)
                                binding.rvPlanList.adapter = membershipPlanAdapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanListBillingModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    inner class MembershipPlanAdapter(var listModelList: ArrayList<PlanListBillingModel.ResponseData.Plan>,var ctx: Context?,var btnFreeJoin: Button) : RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder>() {
        private var row_index = -1
        private var pos = 0
        var i: Intent? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipPlanBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_plan, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel: PlanListBillingModel.ResponseData.Plan = listModelList[position]
            //        holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvPlanFeatures01.text = listModel.planFeatures!![0].feature
            holder.binding.tvPlanFeatures02.text = listModel.planFeatures!![1].feature
            holder.binding.tvPlanFeatures03.text = listModel.planFeatures!![2].feature
            holder.binding.tvPlanFeatures04.text = listModel.planFeatures!![3].feature
            holder.binding.tvPlanAmount.text = "$" + listModel.planAmount
            holder.binding.tvSubName.text = listModel.subName
            holder.binding.tvPlanInterval.text = listModel.planInterval
            if (listModel.recommendedFlag.equals("1")) {
                holder.binding.tvRecommended.visibility = View.VISIBLE
                /*  if (pos == 0) {
                holder.binding.llPlanSub.setBackgroundColor(ctx.getResources().getColor(R.color.blue));
                holder.binding.llFeatures.setVisibility(View.VISIBLE);
                holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.llFeatures.setBackgroundColor(ctx.getResources().getColor(R.color.white));
            }*/
            } else {
                holder.binding.tvRecommended.visibility = View.GONE
            }
            holder.binding.llPlanMain.setOnClickListener { view ->
                row_index = position
                pos++
                notifyDataSetChanged()
            }
            if (row_index == position) {
                ChangeFunction(holder, listModel, position)
            } else {
                if (listModel.recommendedFlag.equals("1") && pos == 0) {
                    holder.binding.tvRecommended.visibility = View.VISIBLE
                    ChangeFunction(holder, listModel, position)
                } else {
                    holder.binding.llPlanSub.background = ContextCompat.getDrawable(ctx!!,R.drawable.rounded_light_gray)
                    holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(ctx!!,R.color.black))
                    holder.binding.tvSubName.setTextColor(ContextCompat.getColor(ctx!!,R.color.black))
                    holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(ctx!!,R.color.black))
                    holder.binding.llFeatures.visibility = View.GONE
                }
            }
            btnFreeJoin.setOnClickListener {
                myBackPress = true
                ctx!!.startActivity(i)
                finish()
            }
        }

        private fun ChangeFunction(holder: MyViewHolder, listModel: PlanListBillingModel.ResponseData.Plan, position: Int) {
            holder.binding.llPlanSub.setBackgroundResource(R.drawable.top_round_green_cornor)
            holder.binding.llFeatures.visibility = View.VISIBLE
            holder.binding.tvPlanAmount.setTextColor(ctx!!.resources.getColor(R.color.white))
            holder.binding.tvSubName.setTextColor(ctx!!.resources.getColor(R.color.white))
            holder.binding.tvPlanInterval.setTextColor(ctx!!.resources.getColor(R.color.white))
            holder.binding.llFeatures.setBackgroundColor(ctx!!.resources.getColor(R.color.white))
            renewPlanFlag = listModel.planFlag
            renewPlanId = listModel.planID
            notificationStatus = true
            val gson = Gson()
            i = Intent(ctx, OrderSummaryActivity::class.java)
            i!!.putExtra("comeFrom", "membership")
            i!!.putExtra("ComesTrue", ComeFrom)
            i!!.putExtra("PlanData", gson.toJson(listModelList))
            i!!.putExtra("TrialPeriod", "")
            i!!.putExtra("position", position)
            i!!.putExtra("Promocode", "")
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: MembershipPlanBinding) : RecyclerView.ViewHolder(binding.root)
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
                    notificationStatus = false
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
                if (!notificationStatus) {
                    if (player != null) {
                        Log.e("Destroy", "Activity Destoryed")
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(notificationId)
                        relesePlayer(applicationContext)
                    }
                }
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        var renewPlanFlag: String? = null
        var renewPlanId: String? = null
    }
}