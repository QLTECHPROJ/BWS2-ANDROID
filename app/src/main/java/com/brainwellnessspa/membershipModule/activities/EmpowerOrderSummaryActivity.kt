package com.brainwellnessspa.membershipModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.EEPPlanListModel
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import java.util.*

class EmpowerOrderSummaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderSummaryBinding
    var TrialPeriod: String? = null
    var comeFrom: String? = ""
    var UserId: String? = null

    val gson = Gson()

    //    lateinit var params:SkuDetailsParams
    /* renewPlanFlag, renewPlanId, */
    var ComesTrue: String? = null
    var Promocode = ""
    var OldPromocode: String? = ""
    var listModelList: ArrayList<EEPPlanListModel.ResponseData.Plan>? = null
    var position = 0
    private var mLastClickTime: Long = 0
    lateinit var ctx: Context
    lateinit var activity: Activity

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empower_order_summary)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        ctx = this@EmpowerOrderSummaryActivity
        activity = this@EmpowerOrderSummaryActivity
        if (intent != null) {
            TrialPeriod = intent.getStringExtra("TrialPeriod")
            //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
            //            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = intent.getIntExtra("position", 0)

            val json4 = intent.getStringExtra("PlanData")
            val type1 = object : TypeToken<ArrayList<EEPPlanListModel.ResponseData.Plan>?>() {}.type
            listModelList = gson.fromJson(json4, type1)

        }
        if (intent != null) {
            ComesTrue = intent.getStringExtra("ComesTrue")
        }
        if (intent.extras != null) {
            OldPromocode = intent.getStringExtra(CONSTANTS.Promocode)
        }
        val p = Properties()

        val gson: Gson
        val gsonBuilder = GsonBuilder()
        gson = gsonBuilder.create()
        p.putValue("plan", gson.toJson(listModelList))

        BWSApplication.addToSegment("Order Summary Viewed", p, CONSTANTS.screen)
        if (!OldPromocode.equals("", ignoreCase = true)) {
            binding.edtCode.setText(OldPromocode)
        }
        if (!comeFrom.equals("", ignoreCase = true)) {
            binding.tvPromoCode.visibility = View.GONE
            binding.llPromoCode.visibility = View.GONE
        } else {
            binding.tvPromoCode.visibility = View.GONE
            binding.llPromoCode.visibility = View.GONE
        }
        try {

            binding.tvTrialPeriod.visibility = View.VISIBLE
            binding.tvPlanInterval.text = listModelList!![position].planInterval.toString() + " Membership"
            binding.tvPlanTenure.text = "per/" + listModelList!![position].planTenure
            binding.tvPlanNextRenewal.text = listModelList!![position].planNextRenewal
            binding.tvSubName.text = listModelList!![position].subName
            binding.tvTrialPeriod.text = TrialPeriod
            binding.tvSubName1.text = listModelList!![position].subName
            binding.tvPlanAmount1.text = "$" + listModelList!![position].planAmount
            binding.tvPlanAmount.text = "$" + listModelList!![position].planAmount
            binding.tvTotalAmount.text = "$" + listModelList!![position].planAmount

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.llBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnCheckout.setOnClickListener {
            try {
                if (binding.edtCode.text.toString() == "") {
                    Promocode = ""
                    val p1 = Properties()
                    val gsonBuilder = GsonBuilder()
                    val gson: Gson
                    gson = gsonBuilder.create()
                    p1.putValue("plan", gson.toJson(listModelList))
                    p1.putValue("planStartDt ", "")
                    p1.putValue("planExpiryDt", listModelList!![position].planNextRenewal)
                    p1.putValue("planRenewalDt", listModelList!![position].planNextRenewal)
                    p1.putValue("planAmount", listModelList!![position].planAmount)
                    val i = Intent(ctx, StripePaymentCheckoutActivity::class.java)
                    i.putExtra("PlanData", gson.toJson(listModelList))
                    i.putExtra("TrialPeriod", TrialPeriod)
                    i.putExtra("position", position)
                    i.putExtra("Promocode", Promocode)
                    startActivity(i)
                    finish()

                    BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        if (!comeFrom.equals("", ignoreCase = true)) {
            val i = Intent(ctx, StripeEnhanceMembershipUpdateActivity::class.java)
            i.putExtra("ComeFrom", ComesTrue)
            startActivity(i)
            finish()
        } else {
            val i = Intent(ctx, StripeEnhanceMembershipActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}