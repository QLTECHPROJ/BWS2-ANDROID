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
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.dashboardModule.session.SessionFreeIntroScreenActivity
import com.brainwellnessspa.databinding.ActivityEmpowerOrderSummaryBinding
import com.brainwellnessspa.userModule.models.AuthOtpModel
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

class EmpowerOrderSummaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmpowerOrderSummaryBinding
    var TrialPeriod: String? = null
    var comeFrom: String? = ""
    var UserId: String? = null
    var CardId: String? = null

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
                val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                CardId = shared.getString(CONSTANTS.PREFE_ACCESS_CardId, "").toString()
                if (CardId != "") {
                    callMembershipPurchaseAPi()
                } else {
                    val i = Intent(ctx, EmpowerPaymentCheckoutActivity::class.java)
                    i.putExtra("PlanData", gson.toJson(listModelList))
                    i.putExtra("TrialPeriod", TrialPeriod)
                    i.putExtra("position", position)
                    i.putExtra("Promocode", Promocode)
                    startActivity(i)
                    finish()
                }
                BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callMembershipPurchaseAPi() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SucessModel> = APINewClient.client.getEepStripeExistingCusPayment(UserId, listModelList!![position].stripePlanId, listModelList!![position].planFlag,CardId)
            listCall.enqueue(object : Callback<SucessModel> {
                override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val listModel: SucessModel = response.body()!!
                    if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                        callGetCoUserDetails()
                    }
                }

                override fun onFailure(call: Call<SucessModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
        }
    }

    fun callGetCoUserDetails() {
        val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(UserId)
        listCall.enqueue(object : Callback<AuthOtpModel> {
            override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                try {
                    val listModel: AuthOtpModel = response.body()!!
                    if (listModel.ResponseCode == getString(R.string.ResponseCodesuccess)) {
                        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                        editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                        editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                        editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                        editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                        editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, listModel.ResponseData.CountryCode)
                        editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                        editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                        editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                        editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                        editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                        editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, listModel.ResponseData.directLogin)
                        editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, listModel.ResponseData.isPinSet)
                        editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, listModel.ResponseData.isEmailVerified)
                        editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, listModel.ResponseData.isMainAccount)
                        editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, listModel.ResponseData.CoUserCount)
                        editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, listModel.ResponseData.IsInCouser)
                        editor.putString(CONSTANTS.PREFE_ACCESS_paymentType, listModel.ResponseData.paymentType)
                        if (listModel.ResponseData.paymentType == "0") {
                            // Stripe
                            try {
                                if (listModel.ResponseData.oldPaymentDetails.isNotEmpty()) {
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.oldPaymentDetails[0].PlanId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.oldPaymentDetails[0].purchaseDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.oldPaymentDetails[0].expireDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "")
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "")
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStr, listModel.ResponseData.oldPaymentDetails[0].PlanStr)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_OrderTotal, listModel.ResponseData.oldPaymentDetails[0].OrderTotal)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.oldPaymentDetails[0].PlanStatus)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_CardId, listModel.ResponseData.oldPaymentDetails[0].CardId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.oldPaymentDetails[0].PlanContent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (listModel.ResponseData.paymentType == "1") {
                            // IAP
                            try {
                                if (listModel.ResponseData.planDetails.isNotEmpty()) {
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.planDetails[0].PlanId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.planDetails[0].PlanExpireDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, listModel.ResponseData.planDetails[0].TransactionId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, listModel.ResponseData.planDetails[0].TrialPeriodStart)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, listModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.planDetails[0].PlanStatus)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.planDetails[0].PlanContent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        editor.apply()
                        val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                        val edited = shred.edit()
                        edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                        val selectedCategoriesTitle = arrayListOf<String>()
                        val selectedCategoriesName = arrayListOf<String>()
                        val gson = Gson()
                        for (i in listModel.ResponseData.AreaOfFocus) {
                            selectedCategoriesTitle.add(i.MainCat)
                            selectedCategoriesName.add(i.RecommendedCat)
                        }
                        edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                        edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                        edited.apply()

                        var p = Properties()
                        p.putValue("planId", listModelList!![position].planID)
                        p.putValue("plan", listModelList!![position].subName)
                        p.putValue("planAmount", listModelList!![position].planAmount)
                        p.putValue("planInterval", listModelList!![position].planInterval)
                        p.putValue("totalProfile", "2")
                        BWSApplication.addToSegment("Checkout Completed", p, CONSTANTS.track)
                        val i = Intent(ctx, SessionFreeIntroScreenActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(i)
                        finish()

                    } else if (listModel.ResponseCode == getString(R.string.ResponseCodeDeleted)) {
                        BWSApplication.callDelete403(activity, listModel.ResponseMessage)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
            }
        })
    }

    override fun onBackPressed() {
//        if (!comeFrom.equals("", ignoreCase = true)) {
//            val i = Intent(ctx, StripeEnhanceMembershipUpdateActivity::class.java)
//            i.putExtra("ComeFrom", ComesTrue)
//            startActivity(i)
//            finish()
//        } else {
            val i = Intent(ctx, EmpowerPanListActivity::class.java)
            startActivity(i)
            finish()
//        }
    }
}