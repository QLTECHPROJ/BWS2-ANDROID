package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.ActivityUpgradePlanBinding
import com.brainwellnessspa.databinding.PlanListFilteredLayoutBinding
import com.brainwellnessspa.membershipModule.activities.IAPOrderSummaryActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/* This is the upgrade plan act */
class UpgradePlanActivity : AppCompatActivity(), PurchasesUpdatedListener {
    lateinit var binding: ActivityUpgradePlanBinding
    lateinit var ctx: Context
    lateinit var act: Activity
    var userId: String? = ""
    var coUserId: String? = ""
    lateinit var i: Intent
    lateinit var planListAdapter: PlanListAdapter
    var listModelGlobal: PlanlistInappModel? = null
    var value: Int = 2
    var step = 1
    var min = 1
    var planId: String = ""
    var DeviceType: String = ""
    val skuList = listOf("weekly_2_profile", "weekly_3_profile", "weekly_4_profile", "monthly_2_profile", "monthly_3_profile", "monthly_4_profile", "six_monthly_2_profile", "six_monthly_3_profile", "six_monthly_4_profile", "annual_2_profile", "annual_3_profile", "annual_4_profile")
    lateinit var billingClient: BillingClient
    lateinit var params: SkuDetailsParams
    var intentflag: String = ""
    var skuDetailList = arrayListOf<SkuDetails>()

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upgrade_plan)
        ctx = this@UpgradePlanActivity
        act = this@UpgradePlanActivity
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        i = Intent(ctx, IAPOrderSummaryActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        if (intent != null) {
            planId = intent.getStringExtra("PlanId").toString()
            DeviceType = intent.getStringExtra("DeviceType").toString()
        }

        /* This is screen back click */
        binding.llBack.setOnClickListener {
            val i = Intent(ctx, IAPBillingOrderActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            i.putExtra("PlanId", planId)
            i.putExtra("DeviceType", DeviceType)
            startActivity(i)
            finish()
        }

        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
        binding.btnUpgradePlan.setOnClickListener {
            i.putExtra("plan", intentflag)
            i.putExtra("upgrade", "1")
            startActivity(i)
        }
        /* This is the listing view layout */
        binding.rvPlanList.layoutManager = LinearLayoutManager(act)

        //        planListAdapter = PlanListAdapter(
        //            listModelList, ctx, i
        //        )
        //        binding.rvPlanList.adapter = planListAdapter
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) { // The BillingClient is setup
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("Failed", "")

            }
        })
    }

    override fun onBackPressed() {
        val i = Intent(ctx, IAPBillingOrderActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        i.putExtra("PlanId", planId)
        i.putExtra("DeviceType", DeviceType)
        startActivity(i)
        finish()
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList -> // Process the result.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                skuDetailList = skuDetailsList as java.util.ArrayList<SkuDetails>
                prepareUserData()
            }
        }

    } else {
        println("Billing Client not ready")
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            val listCall: Call<PlanlistInappModel> = APINewClient.client.getUpgradePlanlistInapp(coUserId)
            listCall.enqueue(object : Callback<PlanlistInappModel> {
                override fun onResponse(call: Call<PlanlistInappModel>, response: Response<PlanlistInappModel>) {
                    try {
                        val listModel: PlanlistInappModel = response.body()!!
                        listModelGlobal = response.body()!!
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                            binding.nestedScroll.isSmoothScrollingEnabled = true

                            planListAdapter = PlanListAdapter(listModel.responseData!!.plan!!, ctx, i, skuDetailList, binding)
                            binding.rvPlanList.adapter = planListAdapter
                            binding.tvTitle.text = listModel.responseData!!.title
                            binding.tvDesc.text = listModel.responseData!!.desc

                            val p = Properties()
                            if (intent != null) {
                                planId = intent.getStringExtra("PlanId").toString()
                                DeviceType = intent.getStringExtra("DeviceType").toString()
                            }
                            if (DeviceType == "1") {
                                for (i2 in listModel.responseData!!.plan!!.indices) {
                                    if (planId.equals(listModel.responseData!!.plan!![i2].androidplanId, ignoreCase = true)) {
                                        binding.tvOldPlanTitle.text = listModel.responseData!!.plan!![i2].planInterval
                                        binding.tvOldPlanContent.text = listModel.responseData!!.plan!![i2].subName
                                        p.putValue("planId", planId)
                                        p.putValue("plan", listModel.responseData!!.plan!![i2].subName)
                                        p.putValue("planAmount", listModel.responseData!!.plan!![i2].planAmount)
                                        p.putValue("planInterval", listModel.responseData!!.plan!![i2].planInterval)
                                        p.putValue("totalProfile", listModel.responseData!!.plan!![i2].profileCount)
                                        break
                                    }
                                }
                                for (i1 in 0 until skuDetailList.size) {
                                    if (planId == skuDetailList[i1].sku) {
                                        binding.tvOldPlanAmount.text = skuDetailList[i1].price
                                        p.putValue("planAmount", skuDetailList[i1].price)
                                        break
                                    }
                                }
                            } else if (DeviceType == "0") {
                                for (i2 in listModel.responseData!!.plan!!.indices) {
                                    if (planId.equals(listModel.responseData!!.plan!![i2].iOSplanId, ignoreCase = true)) {
                                        binding.tvOldPlanTitle.text = listModel.responseData!!.plan!![i2].planInterval
                                        binding.tvOldPlanContent.text = listModel.responseData!!.plan!![i2].subName
                                        p.putValue("planId", planId)
                                        p.putValue("plan", listModel.responseData!!.plan!![i2].subName)
                                        p.putValue("planAmount", listModel.responseData!!.plan!![i2].planAmount)
                                        p.putValue("planInterval", listModel.responseData!!.plan!![i2].planInterval)
                                        p.putValue("totalProfile", listModel.responseData!!.plan!![i2].profileCount)
                                        binding.tvOldPlanAmount.text = listModel.responseData!!.plan!![i2].planAmount
                                        break
                                    }
                                }
                            }
                            BWSApplication.addToSegment("Upgrade Plan Screen Viewed", p, CONSTANTS.screen)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanlistInappModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), act)
        }
    }

    override fun onResume() {
        setupBillingClient()
        super.onResume()
    }

    class PlanListAdapter(var listModelList: List<PlanlistInappModel.ResponseData.Plan>, var ctx: Context, var i: Intent, var skuDetailList: java.util.ArrayList<SkuDetails>, var binding1: ActivityUpgradePlanBinding) : RecyclerView.Adapter<PlanListAdapter.MyViewHolder>() {
        private var rowIndex: Int = -1
        private var pos: Int = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanListFilteredLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.plan_list_filtered_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTilte.text = listModelList[position].planInterval
            holder.binding.tvContent.text = listModelList[position].subName
            //            holder.binding.tvAmount.text = "$" + listModelList[position].planAmount
            for (i in 0 until skuDetailList.size) {
                if (listModelList[position].androidplanId!! == skuDetailList[i].sku) {
                    holder.binding.tvAmount.text = skuDetailList[i].price
                    break
                }
            }

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

        private fun changeFunction(holder: MyViewHolder, listModelList: List<PlanlistInappModel.ResponseData.Plan>, position: Int) {
            holder.binding.llPlanMain.background = ContextCompat.getDrawable(ctx, R.drawable.light_sky_round_cornors)
            holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            val gson = Gson()
            binding1.btnUpgradePlan.text = "START AT " + holder.binding.tvAmount.text.toString() + " / " + listModelList[position].subName.toString().split("/")[1]
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

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {}
}