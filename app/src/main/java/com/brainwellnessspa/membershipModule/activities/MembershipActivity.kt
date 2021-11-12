package com.brainwellnessspa.membershipModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.callSignActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.ActivityMembershipBinding
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding
import com.brainwellnessspa.databinding.MembershipPlanBinding
import com.brainwellnessspa.databinding.SubscribeBoxLayoutBinding
import com.brainwellnessspa.faqModule.models.FaqListModel
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.utility.APINewClient.client
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.MeasureRatio
import com.brainwellnessspa.webView.TncActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MembershipActivity : AppCompatActivity() {
    lateinit var binding: ActivityMembershipBinding
    var subscriptionAdapter: SubscriptionAdapter? = null
    var membershipPlanAdapter: MembershipPlanAdapter? = null
    lateinit var ctx: Context
    var adapter: MembershipFaqAdapter? = null
    private val mLastClickTime: Long = 0
    lateinit var i: Intent
    private var doubleBackToExitPressedOnce = false
    lateinit var act: Activity
    var UserId :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership)
        ctx = this@MembershipActivity
        act = this@MembershipActivity

        binding.llBack.setOnClickListener { view ->
            callSignActivity(act)
        }
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        UserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvList.layoutManager = mLayoutManager
        binding.rvList.itemAnimator = DefaultItemAnimator()
        val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlanList.layoutManager = mLayoutManager1
        binding.rvPlanList.itemAnimator = DefaultItemAnimator()
        val serachList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvFaqList.layoutManager = serachList
        binding.rvFaqList.itemAnimator = DefaultItemAnimator()
        val p = Properties()
        i = Intent(ctx, OrderSummaryActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        BWSApplication.addToSegment("Plan List Viewed", p, CONSTANTS.screen)
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall: Call<FaqListModel> = client.faqLists
            listCall.enqueue(object : Callback<FaqListModel?> {
                override fun onResponse(call: Call<FaqListModel?>?, response: Response<FaqListModel?>) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            val listModel: FaqListModel? = response.body()
                            binding.tvFaqTitle.text = getString(R.string.f_A_Q)
                            adapter = MembershipFaqAdapter(listModel!!.responseData!!, ctx, binding.rvFaqList, binding.tvFound)
                            binding.rvFaqList.adapter = adapter
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<FaqListModel?>?, t: Throwable?) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    override fun onResume() {
        prepareMembershipData()
        super.onResume()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            return
        }
        this.doubleBackToExitPressedOnce = true
        BWSApplication.showToast("Press again to exit", act)

        Handler(Looper.myLooper()!!).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall: Call<MembershipPlanListModel> = client.getMembershipPlanList()
            listCall.enqueue(object : Callback<MembershipPlanListModel?> {
                override fun onResponse(call: Call<MembershipPlanListModel?>?, response: Response<MembershipPlanListModel?>) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            val membershipPlanListModel: MembershipPlanListModel = response.body()!!
                            if (membershipPlanListModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                binding.btnFreeJoin.visibility = View.VISIBLE
                                binding.tvTitle.text = membershipPlanListModel.responseData!!.title
                                binding.tvDesc.text = membershipPlanListModel.responseData!!.desc
                                binding.tvTag.setText(R.string.membership_title)
                                binding.tvText.text = getString(R.string.privacy_policy_t_n_c)
                                binding.tvtncs.text = getString(R.string.t_n_csm)
                                binding.tvPrivacyPolicys.text = getString(R.string.privacy_policysm)
                                binding.tvAnd.text = getString(R.string.and)
                                binding.tvDisclaimers.setText(R.string.disclaimers)
                                binding.tvtncs.paint.isUnderlineText = true
                                binding.tvPrivacyPolicys.paint.isUnderlineText = true
                                binding.tvDisclaimers.paint.isUnderlineText = true
                                binding.tvtncs.setOnClickListener {
                                    val i = Intent(ctx, TncActivity::class.java)
                                    i.putExtra(CONSTANTS.Web, "Tnc")
                                    startActivity(i)
                                }
                                binding.tvPrivacyPolicys.setOnClickListener {
                                    val i = Intent(ctx, TncActivity::class.java)
                                    i.putExtra(CONSTANTS.Web, "PrivacyPolicy")
                                    startActivity(i)
                                }
                                binding.tvDisclaimers.setOnClickListener {
                                    val dialog = Dialog(ctx)
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog.setContentView(R.layout.full_desc_layout)
                                    dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.dark_blue_gray)))
                                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                                    val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
                                    val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
                                    tvTitle.setText(R.string.Disclaimer)
                                    tvDesc.setText(R.string.Disclaimer_text)
                                    dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss()
                                            return@setOnKeyListener true
                                        }
                                        false
                                    }
                                    tvClose.setOnClickListener { v: View? -> dialog.dismiss() }
                                    dialog.show()
                                    dialog.setCancelable(false)
                                }
                                val measureRatio: MeasureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                                binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner)
                                membershipPlanAdapter = MembershipPlanAdapter(membershipPlanListModel.responseData!!.plan!!, ctx, binding.btnFreeJoin, membershipPlanListModel.responseData!!.trialPeriod!!, act, i)
                                binding.rvPlanList.adapter = membershipPlanAdapter
                                subscriptionAdapter = SubscriptionAdapter(membershipPlanListModel.responseData!!.audioFiles!!, ctx)
                                binding.rvList.adapter = subscriptionAdapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<MembershipPlanListModel?>?, t: Throwable?) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    inner class MembershipFaqAdapter(var modelList: List<FaqListModel.ResponseData>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudioFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audio_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = modelList[position].title
            holder.binding.tvDesc.text = modelList[position].desc
            holder.binding.ivClickRight.setOnClickListener {
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray)
                holder.binding.tvDesc.isFocusable = true
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.visibility = View.VISIBLE
                holder.binding.ivClickRight.visibility = View.GONE
                holder.binding.ivClickDown.visibility = View.VISIBLE
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_arrow_icon)
            }
            holder.binding.ivClickDown.setOnClickListener { view ->
                holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                holder.binding.tvDesc.visibility = View.GONE
                holder.binding.ivClickRight.visibility = View.VISIBLE
                holder.binding.ivClickDown.visibility = View.GONE
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon)
            }
            if (modelList.isEmpty()) {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.GONE
            } else {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: AudioFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }


    class SubscriptionAdapter(private val listModelList: ArrayList<MembershipPlanListModel.AudioFile>, var ctx: Context) : RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SubscribeBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.subscribe_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel = listModelList[position]
            holder.binding.tvTitle.text = listModel.name
            Glide.with(ctx).load(listModel.imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(12))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: SubscribeBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    //import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity;
    class MembershipPlanAdapter(private val listModelList: ArrayList<MembershipPlanListModel.Plan>, var ctx: Context, var btnFreeJoin: Button, var TrialPeriod: String, var activity: Activity, var i: Intent) : RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder>() {
        private var rowIndex = -1
        private var pos = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipPlanBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_plan, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel = listModelList[position] //        holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvPlanFeatures01.text = listModel.planFeatures?.get(0)?.feature
            holder.binding.tvPlanFeatures02.text = listModel.planFeatures?.get(1)?.feature
            holder.binding.tvPlanFeatures03.text = listModel.planFeatures?.get(2)?.feature
            holder.binding.tvPlanFeatures04.text = listModel.planFeatures?.get(3)?.feature
            holder.binding.tvPlanAmount.text = "$" + listModel.planAmount
            holder.binding.tvSubName.text = listModel.subName
            holder.binding.tvPlanInterval.text = listModel.planInterval
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
            holder.binding.llPlanMain.setOnClickListener {
                rowIndex = position
                pos++
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                changeFunction(holder, listModel, position)
            } else {
                if (listModel.recommendedFlag.equals("1", ignoreCase = true) && pos == 0) {
                    holder.binding.tvRecommended.visibility = View.VISIBLE
                    changeFunction(holder, listModel, position)
                } else {
                    holder.binding.llPlanSub.background = ContextCompat.getDrawable(activity, R.drawable.rounded_light_gray)
                    holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.black))
                    holder.binding.llFeatures.visibility = View.GONE
                }
            }
            btnFreeJoin.setOnClickListener {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    activity.startActivity(i)
                    activity.finish()
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity)
                }
            }
        }

        private fun changeFunction(holder: MyViewHolder, listModel: MembershipPlanListModel.Plan, position: Int) {
            holder.binding.llPlanSub.setBackgroundResource(R.drawable.top_round_green_cornor)
            holder.binding.llFeatures.visibility = View.VISIBLE
            holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.white))
            holder.binding.llFeatures.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
            planFlag = listModel.planFlag.toString()
            price = listModel.planAmount.toString()
            planId = listModel.planID.toString()
            val gson = Gson();
            i = Intent(ctx, OrderSummaryActivity::class.java)
            i.putExtra("PlanData", gson.toJson(listModelList))
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            i.putExtra("Promocode", "")
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: MembershipPlanBinding) : RecyclerView.ViewHolder(binding.root)

        companion object {
            @JvmField var planFlag = ""

            @JvmField var planId = ""

            @JvmField var price = ""
        }
    }

}