package com.brainwellnessspa.membershipModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.models.EEPPlanListModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.webView.TncActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpowerPanListActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener  {
    lateinit var binding: ActivityEmpowerManageBinding
    lateinit var act: Activity
    lateinit var ctx: Context
    var userId: String = ""
    var intentflag: String = ""
    lateinit var i: Intent
    lateinit var adapter: MembershipFaqAdapter
    lateinit var planFeatureAdapter: PlanFeatureAdapter
    lateinit var planListAdapter: PlanListAdapter
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empower_manage)
        act = this@EmpowerPanListActivity
        ctx = this@EmpowerPanListActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        window.addFlags(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW)
        prepareData()

        binding.tvtncs.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            i.putExtra(CONSTANTS.Web, "Tnc")
            startActivity(i)
        }

        binding.tvPrivacyPolicys.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            i.putExtra(CONSTANTS.Web, "PrivacyPolicy")
            startActivity(i)
        }

        binding.rvPlanList.layoutManager = LinearLayoutManager(act)
        i = Intent(ctx, EmpowerOrderSummaryActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION

        binding.btnFreeJoin.setOnClickListener {
            i.putExtra("plan", intentflag)
            startActivity(i)
            finish()
        }

        binding.youtubeView.initialize(API_KEY, this)
        binding.llBack.setOnClickListener{
          finish()
        }
         binding.llSkip.setOnClickListener{
             val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
             val planId = shared.getString(CONSTANTS.PREFE_ACCESS_PlanId, "")
             if(planId == ""){
                 val i = Intent(ctx, StripeEnhanceMembershipActivity::class.java)
                 i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                 i.putExtra("plan", "0")
                 startActivity(i)
                 finish()
             }else {
                 val intent = Intent(act, BottomNavigationActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                 intent.putExtra("IsFirst", "1")
                 startActivity(intent)
                 finish()
             }
        }
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(act)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getEEPPlanList(userId)
            listCall.enqueue(object : Callback<EEPPlanListModel?> {
                override fun onResponse(call: Call<EEPPlanListModel?>, response: Response<EEPPlanListModel?>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        val listModel = response.body()
                        if (listModel?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.nestedScroll.isSmoothScrollingEnabled = true

                            planListAdapter = PlanListAdapter(listModel!!.responseData!!.plan!!, ctx, i, binding)
                            binding.rvPlanList.adapter = planListAdapter
                            binding.tvTitle.text = listModel.responseData!!.title
                            binding.tvDesc.text = listModel.responseData!!.desc

                            binding.tvFreeTrial.text = listModel.responseData!!.plan!![0].freeTrial

                            binding.rvFeatures.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)
                            planFeatureAdapter = PlanFeatureAdapter(listModel.responseData!!.planFeatures!!, ctx)
                            binding.rvFeatures.adapter = planFeatureAdapter

                       /*     binding.rvVideoList.layoutManager = LinearLayoutManager(act, LinearLayoutManager.HORIZONTAL, false)
                            val videoListAdapter = VideoSeriesListAdapter(listModel.responseData!!.testminialVideo!!, ctx)
                            binding.rvVideoList.adapter = videoListAdapter*/

                            binding.rvFaqList.layoutManager = LinearLayoutManager(ctx)
                            adapter = MembershipFaqAdapter(listModel.responseData!!.fAQs!!, ctx, binding.rvFaqList, binding.tvFound)
                            binding.rvFaqList.adapter = adapter

                            // do plan Code
                        } else if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                            BWSApplication.callDelete403(act, listModel.responseMessage)
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<EEPPlanListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }

    override fun onBackPressed() {
       /* if (doubleBackToExitPressedOnce) {
            finishAffinity()
            return
        }
        this.doubleBackToExitPressedOnce = true
        BWSApplication.showToast("Press again to exit", act)

        Handler(Looper.myLooper()!!).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)*/
        finish()
        super.onBackPressed()
    }

    class PlanListAdapter(var listModelList: List<EEPPlanListModel.ResponseData.Plan>, var ctx: Context, var i: Intent, var binding1: ActivityEmpowerManageBinding) : RecyclerView.Adapter<PlanListAdapter.MyViewHolder>()/*, Filterable */ {
        private var rowIndex: Int = -1
        private var pos: Int = 0
        var ip = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanListFilteredLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.plan_list_filtered_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTilte.text = listModelList[position].planInterval
            holder.binding.tvContent.text = listModelList[position].subName

            holder.binding.tvAmount.text = "$" + listModelList[position].planAmount

            if (listModelList[position].recommendedFlag.equals("1")) {
                holder.binding.rlMostPopular.visibility = View.VISIBLE
            } else {
                holder.binding.rlMostPopular.visibility = View.INVISIBLE
            }
            holder.binding.llPlanMain.setOnClickListener {
                rowIndex = position
                pos++
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                changeFunction(holder, listModelList, position)
            } else {
                if (listModelList[position].recommendedFlag.equals("1") && pos == 0) {
                    holder.binding.rlMostPopular.visibility = View.VISIBLE
                    changeFunction(holder, listModelList, position)
                } else {
                    holder.binding.llPlanMain.background = ContextCompat.getDrawable(ctx, R.drawable.light_gray_round_cornors)
                    holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                }
            }

        }

        private fun changeFunction(holder: MyViewHolder, listModelList: List<EEPPlanListModel.ResponseData.Plan>, position: Int) {
            holder.binding.llPlanMain.background = ContextCompat.getDrawable(ctx, R.drawable.light_sky_round_cornors)
            holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            val gson = Gson()
//            binding1.btnFreeJoin.text = "START AT " + holder.binding.tvAmount.text.toString() + " / " + listModelList[position].subName.toString().split("/")[1]
            i.putExtra("PlanData", gson.toJson(listModelList))
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            i.putExtra("Promocode", "")
            i.putExtra("displayPrice", holder.binding.tvAmount.text.toString())
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: PlanListFilteredLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    class PlanFeatureAdapter(private val listModelList: List<EEPPlanListModel.ResponseData.PlanFeature>, var ctx: Context) : RecyclerView.Adapter<PlanFeatureAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanFeatureLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.plan_feature_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvPlanFeatures01.text = listModelList[position].feature
          }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: PlanFeatureLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
    class MembershipFaqAdapter(private val modelList: List<EEPPlanListModel.ResponseData.Faq>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = modelList[position].title
            holder.binding.tvDesc.text = modelList[position].desc

            holder.binding.ivClickRight.setOnClickListener {
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                holder.binding.tvDesc.isFocusable = true
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.visibility = View.VISIBLE
                holder.binding.ivClickRight.visibility = View.GONE
                holder.binding.ivClickDown.visibility = View.VISIBLE
                holder.binding.llBgChange.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_clicked)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_white_arrow_down_icon)
            }

            holder.binding.ivClickDown.setOnClickListener {
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(ctx, R.color.light_black))
                holder.binding.tvDesc.visibility = View.GONE
                holder.binding.ivClickRight.visibility = View.VISIBLE
                holder.binding.ivClickDown.visibility = View.GONE
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_right_gray_arrow_icon)
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

        inner class MyViewHolder(var binding: MembershipFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(CancelMembershipActivity.VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            BWSApplication.showToast(errorMessage, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            youTubePlayerProvider.initialize(API_KEY, this)
        }
    }

    private val youTubePlayerProvider: YouTubePlayer.Provider
        private get() = binding.youtubeView

    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

}