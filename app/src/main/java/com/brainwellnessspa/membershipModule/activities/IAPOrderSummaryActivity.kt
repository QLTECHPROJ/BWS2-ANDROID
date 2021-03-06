package com.brainwellnessspa.membershipModule.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.IAPBillingOrderActivity
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding
import com.brainwellnessspa.membershipModule.models.UpdatePlanPurchase
import com.brainwellnessspa.userModule.models.AuthOtpModel

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class IAPOrderSummaryActivity : AppCompatActivity(), PurchasesUpdatedListener, PurchaseHistoryResponseListener, ConsumeResponseListener, AcknowledgePurchaseResponseListener {
    var binding: ActivityOrderSummaryBinding? = null
    var trialPeriod: String? = ""
    var comeFrom: String? = ""
    var mainAccountId: String? = ""/* renewPlanFlag, renewPlanId, */

    //    lateinit var params:SkuDetailsParams
    /* renewPlanFlag, renewPlanId, */
    var userId: String? = ""
    var displayPrice: String? = ""
    var comesTrue: String? = ""
    var promocode: String? = ""
    var oldPromocode: String? = ""
    var listModelList: ArrayList<PlanlistInappModel.ResponseData.Plan>? = null
    var position = 0
    lateinit var ctx: Context
    var json = ""
    var sku = ""
    var p = Properties()
    var upgrade = ""
    var intentflag: String = ""
    lateinit var billingClient: BillingClient
    lateinit var params: SkuDetailsParams
    val gson = Gson()

    //TODO : Oauth Client ID
    //861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
    //TODO : Oauth Client Secret
    //CLIENT_SECRET = "0hQBynI-gzUrHQtSR-ayUFaK"
    // code :- 4%2F0AY0e-g5HwhmC7D1M2ab--RVBhI2HkU5n1qMJPE3UgQlWa3XoB23tDojyKsd0fw6w_VwS5Q
    /*   MD5: 4D:09:22:47:FD:AD:E3:8B:DD:61:4F:65:BA:66:99:37
    SHA1: F5:37:43:D2:FC:73:4E:6C:51:8C:D7:E7:BE:88:D7:3A:4E:BC:37:4F
    SHA-256: 2C:B7:55:77:AC:97:75:10:90:1A:F4:B4:84:33:89:A6:24:56:CF:47:61:F1:D1:46:F7:87:38:71:E4:94:21:23
    code : - 4/eWdxD7b-YSQ5CNNb-c2iI83KQx19.wp6198ti5Zc7dJ3UXOl0T3aRLxQmbwI
*/

    val skuList = listOf("weekly_2_profile", "weekly_3_profile", "weekly_4_profile", "monthly_2_profile", "monthly_3_profile", "monthly_4_profile", "six_monthly_2_profile", "six_monthly_3_profile", "six_monthly_4_profile", "annual_2_profile", "annual_3_profile", "annual_4_profile")
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        ctx = this@IAPOrderSummaryActivity
        activity = this@IAPOrderSummaryActivity

        if (intent != null) {
            trialPeriod = intent.getStringExtra("TrialPeriod") //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
            displayPrice = intent.getStringExtra("displayPrice") //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
            //            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = intent.getIntExtra("position", 0)
            json = intent.getStringExtra("PlanData")!!
            val type = object : TypeToken<ArrayList<PlanlistInappModel.ResponseData.Plan?>?>() {}.type
            listModelList = gson.fromJson(json, type)
            intentflag = intent.getStringExtra("plan").toString()
            if (intent.hasExtra("upgrade")) {
                upgrade = intent.getStringExtra("upgrade")!!
            } else {
                upgrade = ""
            }
        }
        if (intent != null) {
            comesTrue = intent.getStringExtra("ComesTrue")
        }
        if (intent.extras != null) {
            oldPromocode = intent.getStringExtra(CONSTANTS.Promocode)
        }
        binding!!.edtCode.addTextChangedListener(promoCodeTextWatcher)

        setupBillingClient()
        p = Properties()
        p.putValue("planId", listModelList!![position].planID)
        p.putValue("plan", listModelList!![position].subName)
        p.putValue("planAmount", displayPrice)
        p.putValue("planInterval", listModelList!![position].planInterval)
        p.putValue("totalProfile", listModelList!![position].profileCount)
        addToSegment("Order Summary Viewed", p, CONSTANTS.screen)
        if (!oldPromocode.equals("")) {
            binding!!.edtCode.setText(oldPromocode)
        }

        binding!!.tvPromoCode.visibility = View.GONE
        binding!!.llPromoCode.visibility = View.GONE

        try {
            binding!!.tvTrialPeriod.visibility = View.VISIBLE
            binding!!.tvPlanInterval.text = listModelList!![position].planInterval + " Membership"
            binding!!.tvPlanTenure.text = listModelList!![position].planTenure
            binding!!.tvPlanNextRenewal.text = listModelList!![position].planNextRenewal
            binding!!.tvSubName.text = listModelList!![position].subName
            binding!!.tvSubName1.text = listModelList!![position].subName
            binding!!.tvTrialPeriod.text = listModelList!![position].freeTrial
            /*binding!!.tvPlanAmount.text = "$" + listModelList!![position].planAmount
                binding!!.tvPlanAmount1.text = "$" + listModelList!![position].planAmount
                binding!!.tvTotalAmount.text = "$" + listModelList!![position].planAmount*/
            binding!!.tvPlanAmount.text = displayPrice
            binding!!.tvPlanAmount1.text = displayPrice
            binding!!.tvTotalAmount.text = displayPrice
            //                if (listModelList!![position].planInterval.equals("Annualy")) {
            //                    sku = "annual_" + listModelList!![position].profileCount!! + "_" + "profile"
            //                } else {
            sku = listModelList!![position].androidplanId!!
            Log.e("sku", sku)
            //                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.llBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) { // The BillingClient is setup
                    Log.e("Setup Billing Done", "") //                    checkPurchases()
                    val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                    val token = shared1.getString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                    val oldsku = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanId, "")
                    if (intentflag == "1") loadAllSKUsUpdate(oldsku!!, token!!)
                    else loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("Failed", "")

            }
        })
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList -> // Process the result.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == sku) binding!!.btnCheckout.setOnClickListener {
                        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails) //                                    .setOldSku(skuList[1],sku)
                                //                                    .setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION)
                                .build()
                        billingClient.launchBillingFlow(this, billingFlowParams)
                    }
                }
            }
        }

    } else {
        println("Billing Client not ready")
    }

    override fun onBackPressed() {
        if (isEnhanceBack.equals("1")) {
            val intent = Intent(applicationContext, EnhanceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }

    private val promoCodeTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val number = binding!!.edtCode.text.toString().trim()
            if (number.isEmpty()) {
                binding!!.btnApply.isEnabled = false
                binding!!.btnApply.setTextColor(ContextCompat.getColor(ctx, R.color.gray))
            } else {
                binding!!.btnApply.isEnabled = true
                binding!!.btnApply.setTextColor(ContextCompat.getColor(ctx, R.color.dark_yellow))
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            val shared = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
            val editor = shared.edit()
            editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, sku)
            editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, purchases[0].purchaseToken)
            editor.apply()
            Log.e("Purchase Token", purchases[0].purchaseToken)
            val p = Properties()
            p.putValue("planId", listModelList!![position].planID)
            p.putValue("plan", listModelList!![position].planInterval)
            p.putValue("planAmount", displayPrice)
            p.putValue("planInterval", listModelList!![position].planInterval)
            p.putValue("totalProfile", listModelList!![position].profileCount)
            addToSegment("Checkout Proceeded", p, CONSTANTS.track)
            isEnhanceBack = ""
            callIAPApi(purchases[0].purchaseToken)
            for (purchase in purchases) {
                acknowledgePurchase(purchase.purchaseToken, purchases)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) { // Handle an error caused by a user cancelling the purchase flow.

        } else { // Handle any other error codes.
        }
    }

    private fun callIAPApi(purchaseToken: String) {
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
            val listCall: Call<UpdatePlanPurchase> = APINewClient.client.getUpdatePlanPurchase(userId, mainAccountId, purchaseToken, sku, "1")
            listCall.enqueue(object : Callback<UpdatePlanPurchase> {
                override fun onResponse(call: Call<UpdatePlanPurchase>, response: Response<UpdatePlanPurchase>) {
                    try {
                        hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                        val listModel: UpdatePlanPurchase = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                            if (isNetworkConnected(ctx)) {
                                val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(userId)
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
                                            } else if (listModel.ResponseCode == getString(R.string.ResponseCodeDeleted)) {
                                                callDelete403(activity, listModel.ResponseMessage)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                                    }
                                })
                            }
                            if (upgrade == "1") {
                                IsRefreshPlan = "1"
                                addToSegment("User Plan Upgraded", p, CONSTANTS.track)
                                val i = Intent(ctx, IAPBillingOrderActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                startActivity(i)
                                finish()
                            } else if (upgrade == "") {
                                addToSegment("Checkout Completed", p, CONSTANTS.track)
                                val i = Intent(ctx, EnhanceDoneActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NO_HISTORY
                                i.putExtra("Name", "")
                                i.putExtra("Code", "")
                                i.putExtra("MobileNo", "")
                                i.putExtra("PlanData", gson.toJson(listModelList))
                                i.putExtra("TrialPeriod", trialPeriod)
                                i.putExtra("position", position)
                                i.putExtra("Promocode", promocode)
                                startActivity(i)
                                finish()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<UpdatePlanPurchase>, t: Throwable) {
                    hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun acknowledgePurchase(purchaseToken: String, purchase: MutableList<Purchase>?) {
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient.acknowledgePurchase(params, this)
        billingClient.acknowledgePurchase(params) {
            //            checkPurchases()

        }
    }

    private fun checkPurchases() {
        val client = BillingClient.newBuilder(application).enablePendingPurchases().setListener { _, _ -> }.build()
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@NonNull
                                                billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    client.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, list ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) { //                            loadAllSKUsUpdate(list!![0].skus.toString(), list!![0].purchaseToken)
                            //                            consumePurchases(list!![0].purchaseToken)
                            val gson = Gson()
                            val dateString: String = DateFormat.format("MM/dd/yyyy", Date(list!![0].purchaseTime)).toString()
                            Log.e("purchase list", dateString)
                            val params = SkuDetailsParams.newBuilder()
                            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
                            billingClient.querySkuDetailsAsync(params.build(), object : SkuDetailsResponseListener {
                                fun onSkuDetailsResponse(responseCode: Int, skuDetailsList: List<SkuDetails>?) {
                                    if (skuDetailsList == null) {
                                        return
                                    }
                                    for (skuDetail in skuDetailsList) {
                                        if (skuDetail.sku == list[0].skus[0]) {
                                            val period = skuDetail.subscriptionPeriod // boolean expired = purchaseTime + period < now
                                            Log.e("purchase list", period)

                                        }
                                    }
                                }

                                override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
//                                    TODO("Not yet implemented")
                                }
                            })
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun loadAllSKUsUpdate(oldsku: String, token: String) = if (billingClient.isReady) {
        val params1 = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build()
        billingClient.querySkuDetailsAsync(params1) { billingResult, skuDetailsList -> // Process the result.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == sku) binding!!.btnCheckout.setOnClickListener { // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync()
                        val flowParams = BillingFlowParams.newBuilder().setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldSkuPurchaseToken(token).setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION).build()).setSkuDetails(skuDetails).build()

                        billingClient.launchBillingFlow(this, flowParams)
                    }
                }
            }
        }

    } else {
        println("Billing Client not ready")
    }

    private fun consumePurchases(purchaseToken: String) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        Log.e("consumes", consumeParams.purchaseToken)
        billingClient.consumeAsync(consumeParams, this)

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(consumeParams.purchaseToken).build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, this)

    }

    override fun onPurchaseHistoryResponse(p0: BillingResult, p1: MutableList<PurchaseHistoryRecord>?) {

    }

    override fun onConsumeResponse(billingResult: BillingResult, p1: String) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e("Consume", p1)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) { // Handle an error caused by a user cancelling the purchase flow.

            Log.e("Consume canceled", p1)
        }
    }

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e("Consume aaa ", billingResult.debugMessage)
        }
    }
}
/*
   public static String GOOGLE_AUTHORIZATION = "authorization_code";
    public static String GOOGLE_CLIENT_ID = "861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com";
    public static String GOOGLE_CLIENT_SECRET = "0hQBynI-gzUrHQtSR-ayUFaK";
    public static String GOOGLE_CODE = "4/0AY0e-g7LoHIAy2ulpKIfeciuZSkBrFBh6RseKYb372xyxSb7Gmleh-vHyywKqAEGWlTiMg";
    public static String GOOGLE_REDIRECT_URI = "https://brainwellnessspa.com.au/";
    public static String GOOGLE_ACCESS_TYPE = "offline";
    public static String GOOGLE_REDIRECT_URI1 = "https://brainwellnessapp.com.au?code=4%2F0AY0e-g5HwhmC7D1M2ab--RVBhI2HkU5n1qMJPE3UgQlWa3XoB23tDojyKsd0fw6w_VwS5Q&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fandroidpublisher/";

   public static SubscriptionPurchase getRefreshToken() {
        String refreshToken = "";
        String accessToken = "";
        SubscriptionPurchase subscription = new SubscriptionPurchase();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
//https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&redirect_uri=https://brainwellnessapp.com.au&client_id=861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
//https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&access_type=offline&redirect_uri=https://brainwellnessapp.com.au&client_id=861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
//https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&access_type=offline&prompt=consent&redirect_uri=https://brainwellnessapp.com.au&client_id=861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("grant_type",GOOGLE_AUTHORIZATION));
            nameValuePairs.add(new BasicNameValuePair("client_id",GOOGLE_CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret",GOOGLE_CLIENT_SECRET));
            nameValuePairs.add(new BasicNameValuePair("code",GOOGLE_CODE));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri",GOOGLE_REDIRECT_URI));
//            nameValuePairs.add(new BasicNameValuePair("access_type",GOOGLE_ACCESS_TYPE));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }
            JSONObject json = new JSONObject(buffer.toString());
            refreshToken = json.getString("refresh_token");
//            refreshToken = json.getString("access_token");
            Log.e("purchase refreshToken", refreshToken);
            subscription = getAccessToken(refreshToken);
          /*  SharedPreferences shared1 = getContext().getSharedPreferences(CONSTANTS.InAppPurchase, Context.MODE_PRIVATE);
            String purchaseToken = shared1.getString(CONSTANTS.PREF_KEY_PurchaseToken, "");
            String purchaseID = shared1.getString(CONSTANTS.PREF_KEY_PurchaseID, "");
            subscription = getSubscriptionExpire(accessToken, refreshToken, purchaseID, purchaseToken);*/
            return subscription;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscription;
    }
    public static SubscriptionPurchase getAccessToken(String refreshToken) {
        String accessToken = "";
        SubscriptionPurchase subscription = new SubscriptionPurchase();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
            nameValuePairs.add(new BasicNameValuePair("client_id", GOOGLE_CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
//            nameValuePairs.add(new BasicNameValuePair("access_type",GOOGLE_ACCESS_TYPE));
            nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }
            SharedPreferences shared1 = getContext().getSharedPreferences(CONSTANTS.InAppPurchase, Context.MODE_PRIVATE);
            String purchaseToken = shared1.getString(CONSTANTS.PREF_KEY_PurchaseToken, "");
            String purchaseID = shared1.getString(CONSTANTS.PREF_KEY_PurchaseID, "");
            JSONObject json = new JSONObject(buffer.toString());
            accessToken = json.getString("access_token");

            Log.e("purchase accessToken", accessToken);
            subscription = getSubscriptionExpire(accessToken, refreshToken, purchaseID, purchaseToken);
            return subscription;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return subscription;
    }
    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = new JacksonFactory();

    public static SubscriptionPurchase getSubscriptionExpire(String accessToken, String refreshToken, String subscriptionId, String purchaseToken){
        SubscriptionPurchase subscription = new SubscriptionPurchase();
        try{
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(accessToken);
//            tokenResponse.setRefreshToken(refreshToken);
            tokenResponse.setExpiresInSeconds(3600L);
            tokenResponse.setScope("https://www.googleapis.com/auth/androidpublisher");
            tokenResponse.setTokenType("Bearer");
            HttpRequestInitializer credential =  new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
                    .build()
                    .setFromTokenResponse(tokenResponse);

            AndroidPublisher publisher = new AndroidPublisher.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).
                    setApplicationName(getContext().getString(R.string.app_name)).
                    build();
            AndroidPublisher.Purchases purchases = publisher.purchases();
            AndroidPublisher.Purchases.Subscriptions.Get get = purchases.subscriptions().get(getContext().getPackageName(), subscriptionId, purchaseToken);
            subscription = get.execute();
            Log.e("purchase exp time ", subscription.toString());
            return subscription;
        }
        catch (IOException e) { e.printStackTrace(); }
        return subscription;
    }*/