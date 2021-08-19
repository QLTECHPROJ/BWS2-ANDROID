package com.brainwellnessspa.billingOrderModule.activities

import android.annotation.SuppressLint
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
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.databinding.ActivityMembershipChangeBinding
import com.brainwellnessspa.databinding.MembershipPlanBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

//import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity;

/* This is the old BWA renew plan activity */
class MembershipChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityMembershipChangeBinding
    lateinit var ctx: Context
    var userId: String? = null
    var comeFrom: String? = null
    lateinit var activity: Activity
    var membershipPlanAdapter: MembershipPlanAdapter? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    var notificationStatus = false

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership_change)
        ctx = this@MembershipChangeActivity
        activity = this@MembershipChangeActivity

        /* This is the get string userId */
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        binding.llBack.setOnClickListener { callback() }
        notificationStatus = false

        /* This condition is get string access */
        if (intent != null) {
            comeFrom = intent.getStringExtra("ComeFrom")
        }
        val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlanList.layoutManager = mLayoutManager1
        binding.rvPlanList.itemAnimator = DefaultItemAnimator()

        /* This condition is check about application in background or foreground */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
    }

    /* This function is called when acitvity is in background to open */
    override fun onResume() {
        prepareMembershipData()
        super.onResume()
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        callback()
    }

    /* This function is the device back click event */
    private fun callback() {
        myBackPress = true
        when {
            comeFrom.equals("Plan", ignoreCase = true) -> {
                finish()
            }
            comeFrom.equals("", ignoreCase = true) -> {
                val i = Intent(ctx, BillingOrderActivity::class.java)
                startActivity(i)
                finish()
            }
            else -> {
                val i = Intent(ctx, BillingOrderActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    /* This function is preparing to membership data */
    private fun prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APIClient.client.getPlanListBilling(userId)
            listCall?.enqueue(object : Callback<PlanListBillingModel?> {
                override fun onResponse(call: Call<PlanListBillingModel?>, response: Response<PlanListBillingModel?>) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val membershipPlanListModel = response.body()
                            if (membershipPlanListModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                binding.tvTitle.text = membershipPlanListModel.responseData?.title
                                binding.tvDesc.text = membershipPlanListModel.responseData?.desc
                                val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                                binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner)
                                membershipPlanAdapter = MembershipPlanAdapter(membershipPlanListModel.responseData?.plan, ctx, binding.btnFreeJoin)
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
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    /* This class is preparing to membership plan data */
    inner class MembershipPlanAdapter(private val listModelList: ArrayList<PlanListBillingModel.ResponseData.Plan?>?, var ctx: Context, var btnFreeJoin: Button) : RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder>() {
        private var rowIndex = -1
        private var pos = 0
        var i: Intent? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipPlanBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_plan, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel = listModelList?.get(position) //        holder.binding.tvTitle.setText(listModel.getTitle());
            if (listModel != null) {
                holder.binding.tvSubName.text = listModel.subName
                holder.binding.tvPlanInterval.text = listModel.planInterval
                holder.binding.tvPlanFeatures01.text = listModel.planFeatures?.get(0)?.feature
                holder.binding.tvPlanFeatures02.text = listModel.planFeatures?.get(1)?.feature
                holder.binding.tvPlanFeatures03.text = listModel.planFeatures?.get(2)?.feature
                holder.binding.tvPlanFeatures04.text = listModel.planFeatures?.get(3)?.feature
                holder.binding.tvPlanAmount.text = "$" + listModel.planAmount
            }
            if (listModel != null) {
                if (listModel.recommendedFlag.equals("1", ignoreCase = true)) {
                    holder.binding.tvRecommended.visibility = View.VISIBLE/*  if (pos == 0) {
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
            }
            holder.binding.llPlanMain.setOnClickListener {
                rowIndex = position
                pos++
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                if (listModel != null) {
                    changeFunction(holder, listModel)
                }
            } else {
                if (listModel?.recommendedFlag.equals("1", ignoreCase = true) && pos == 0) {
                    holder.binding.tvRecommended.visibility = View.VISIBLE
                    if (listModel != null) {
                        changeFunction(holder, listModel)
                    }
                } else {
                    holder.binding.llPlanSub.background = ContextCompat.getDrawable(activity, R.drawable.rounded_light_gray)
                    holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.llFeatures.visibility = View.GONE
                }
            }
            btnFreeJoin.setOnClickListener {
                myBackPress = true
                ctx.startActivity(i)
                finish()
            }
        }

        /* This function is which plan selection */
        private fun changeFunction(holder: MyViewHolder, listModel: PlanListBillingModel.ResponseData.Plan) {
            holder.binding.llPlanSub.setBackgroundResource(R.drawable.top_round_blue_cornor)
            holder.binding.llFeatures.visibility = View.VISIBLE
            holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.llFeatures.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
            renewPlanFlag = listModel.planFlag
            renewPlanId = listModel.planID
            notificationStatus = true/*      i = new Intent(ctx, OrderSummaryActivity.class);
            i.putExtra("comeFrom", "membership");
            i.putExtra("ComesTrue", comeFrom);
            i.putParcelableArrayListExtra("PlanData", listModelList);
            i.putExtra("TrialPeriod", "");
            i.putExtra("position", position);
            i.putExtra("Promocode", "");*/
        }

        override fun getItemCount(): Int {
            return listModelList?.size!!
        }

        inner class MyViewHolder(var binding: MembershipPlanBinding) : RecyclerView.ViewHolder(binding.root)

    }

    /* This class is check about application in background or foreground */
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
                    notificationStatus = false
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
                if (!notificationStatus) {
                    if (BWSApplication.player != null) {
                        Log.e("Destroy", "Activity Destroyed")
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(BWSApplication.notificationId)
                        GlobalInitExoPlayer.relesePlayer(applicationContext)
                    }
                }
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    /* This is object declaration */
    companion object {
        var renewPlanFlag: String? = null
        var renewPlanId: String? = null
    }
}