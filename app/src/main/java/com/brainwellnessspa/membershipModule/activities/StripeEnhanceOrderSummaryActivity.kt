package com.brainwellnessspa.membershipModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.PaymentActivity
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StripeEnhanceOrderSummaryActivity : AppCompatActivity() {
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
    var listModelList: ArrayList<MembershipPlanListModel.Plan>? = null
    var listModelList2: ArrayList<PlanListBillingModel.ResponseData.Plan>? = null
    var position = 0
    private var mLastClickTime: Long = 0
    lateinit var ctx: Context
    lateinit var activity: Activity
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        ctx = this@StripeEnhanceOrderSummaryActivity
        activity = this@StripeEnhanceOrderSummaryActivity
        if (intent != null) {
            TrialPeriod = intent.getStringExtra("TrialPeriod")
            //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
            //            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = intent.getIntExtra("position", 0)
            if (intent.hasExtra("comeFrom")) {
                comeFrom = intent.getStringExtra("comeFrom")
                val json4 = intent.getStringExtra("PlanData")
                val type1 = object : TypeToken<ArrayList<PlanListBillingModel.ResponseData.Plan>?>() {}.type
                listModelList2 = gson.fromJson(json4, type1)
            } else {
                val json4 = intent.getStringExtra("PlanData")
                val type1 = object : TypeToken<ArrayList<MembershipPlanListModel.Plan>?>() {}.type
                listModelList = gson.fromJson(json4, type1)
            }
        }
        if (intent != null) {
            ComesTrue = intent.getStringExtra("ComesTrue")
        }
        if (intent.extras != null) {
            OldPromocode = intent.getStringExtra(CONSTANTS.Promocode)
        }
        binding.edtCode.addTextChangedListener(promoCodeTextWatcher)
        val p = Properties()
        if (!comeFrom.equals("", ignoreCase = true)) {
            val gson: Gson
            val gsonBuilder = GsonBuilder()
            gson = gsonBuilder.create()
            p.putValue("plan", gson.toJson(listModelList2))
        } else {
            val gson: Gson
            val gsonBuilder = GsonBuilder()
            gson = gsonBuilder.create()
            p.putValue("plan", gson.toJson(listModelList))
        }
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
            if (!comeFrom.equals("", ignoreCase = true)) {
                binding.tvTrialPeriod.visibility = View.GONE
                binding.tvPlanInterval.text = listModelList2!![position].planInterval + " Membership"
                binding.tvPlanTenure.text = "per/" + listModelList2!![position].planTenure
                binding.tvPlanNextRenewal.text = listModelList2!![position].planNextRenewal
                binding.tvSubName.text = listModelList2!![position].subName
                binding.tvPlanAmount.text = "$" + listModelList2!![position].planAmount
                binding.tvSubName1.text = listModelList2!![position].subName
                binding.tvPlanAmount1.text = "$" + listModelList2!![position].planAmount
                binding.tvTotalAmount.text = "$" + listModelList2!![position].planAmount
            } else {
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.llBack.setOnClickListener {
           onBackPressed()
        }

        binding.btnApply.setOnClickListener { prepareCheckReferCode(binding.edtCode.text.toString()) }
        binding.btnCheckout.setOnClickListener {
            try {
                if (binding.edtCode.text.toString() == "") {
                    Promocode = ""
                    val p1 = Properties()
                    val gson: Gson
                    val gsonBuilder = GsonBuilder()
                    if (!comeFrom.equals("", ignoreCase = true)) {
                        gson = gsonBuilder.create()
                        p1.putValue("plan", gson.toJson(listModelList2))
                        p1.putValue("planStartDt ", "")
                        p1.putValue("planExpiryDt", listModelList2!![position].planNextRenewal)
                        p1.putValue("planRenewalDt", listModelList2!![position].planNextRenewal)
                        p1.putValue("planAmount", listModelList2!![position].planAmount)
                        val i = Intent(ctx, PaymentActivity::class.java)
                        i.putExtra("ComesTrue", ComesTrue)
                        i.putExtra("comeFrom", "membership")
                        if (!comeFrom.equals("", ignoreCase = true)) {
                            i.putExtra("PlanData", gson.toJson(listModelList2))
                        } else {
                            i.putExtra("PlanData", gson.toJson(listModelList))
                        }
                        i.putExtra("TrialPeriod", "")
                        i.putExtra("position", position)
                        startActivity(i)
                        finish()
                    } else {
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
                    }
                    BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track)
                } else {
                    Promocode = binding.edtCode.text.toString()
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listCall: Call<CheckReferCodeModel> = APINewClient.client.CheckReferCode(Promocode)
                        listCall.enqueue(object : Callback<CheckReferCodeModel?> {
                            override fun onResponse(call: Call<CheckReferCodeModel?>, response: Response<CheckReferCodeModel?>) {
                                val listModel: CheckReferCodeModel? = response.body()
                                if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    if (!listModel.responseData!!.codeExist.equals("0")) {
                                        BWSApplication.showToast(listModel.responseMessage, activity)
                                        val p1 = Properties()
                                        if (!comeFrom.equals("", ignoreCase = true)) {
                                            val gson: Gson
                                            val gsonBuilder = GsonBuilder()
                                            gson = gsonBuilder.create()
                                            p1.putValue("plan", gson.toJson(listModelList2))
                                            p1.putValue("planStartDt ", "")
                                            p1.putValue("planExpiryDt", listModelList2!![position].planNextRenewal)
                                            p1.putValue("planRenewalDt", listModelList2!![position].planNextRenewal)
                                            p1.putValue("planAmount", listModelList2!![position].planAmount)
                                            val i = Intent(ctx, PaymentActivity::class.java)
                                            i.putExtra("ComesTrue", ComesTrue)
                                            i.putExtra("comeFrom", "membership")
                                            if (!comeFrom.equals("", ignoreCase = true)) {
                                                i.putExtra("PlanData", gson.toJson(listModelList2))
                                            } else {
                                                i.putExtra("PlanData", gson.toJson(listModelList))
                                            }
                                            i.putExtra("TrialPeriod", "")
                                            i.putExtra("position", position)
                                            startActivity(i)
                                            finish()
                                        } else {
                                            val gson: Gson
                                            val gsonBuilder = GsonBuilder()
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
                                        }
                                        BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track)
                                    } else {
                                        BWSApplication.showToast(listModel.responseMessage, activity)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<CheckReferCodeModel?>, t: Throwable) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            }
                        })
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), activity)
                    }
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

    private val promoCodeTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val number = binding.edtCode.text.toString().trim()
            if (number.isEmpty()) {
                binding.btnApply.isEnabled = false
                binding.btnApply.setTextColor(resources.getColor(R.color.gray))
            } else {
                binding.btnApply.isEnabled = true
                binding.btnApply.setTextColor(resources.getColor(R.color.dark_yellow))
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun prepareCheckReferCode(promoCode: String?) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<CheckReferCodeModel> = APINewClient.client.CheckReferCode(promoCode)
            listCall.enqueue(object : Callback<CheckReferCodeModel?> {
                override fun onResponse(call: Call<CheckReferCodeModel?>, response: Response<CheckReferCodeModel?>) {
                    try {
                        val listModel: CheckReferCodeModel? = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (listModel.responseData!!.codeExist.equals("0")) {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CheckReferCodeModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }
}