package com.brainwellnessspa.manageModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.ReferralModule.Model.CheckReferCodeModel
import com.brainwellnessspa.Utility.APIClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.billingOrderModule.activities.MembershipChangeActivity
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class OrderSummaryActivity: AppCompatActivity(), PurchasesUpdatedListener ,PurchaseHistoryResponseListener,ConsumeResponseListener ,AcknowledgePurchaseResponseListener {
    var binding: ActivityOrderSummaryBinding? = null
    var TrialPeriod: String? = null
    var comeFrom: String? = ""
    var UserId: String? = null/* renewPlanFlag, renewPlanId, */

    /* renewPlanFlag, renewPlanId, */
    var CoUserID: String? = null
    var ComesTrue: String? = null
    var Promocode: String? = ""
    var OldPromocode: String? = ""
    var listModelList: ArrayList<PlanlistInappModel.ResponseData.Plan>? = null
    var listModelList2: ArrayList<PlanListBillingModel.ResponseData.Plan>? = null
    var position = 0
    val mLastClickTime: Long = 0
    lateinit var ctx: Context
    var json = ""
    var sku = ""
    lateinit var billingClient: BillingClient
    val gson = Gson()

    //TODO : Oauth Client ID
    //861076939494-lg98i6qsqreefk9ftjslerikvtj0ot34.apps.googleusercontent.com
//ids
    /*   MD5: 4D:09:22:47:FD:AD:E3:8B:DD:61:4F:65:BA:66:99:37
    SHA1: F5:37:43:D2:FC:73:4E:6C:51:8C:D7:E7:BE:88:D7:3A:4E:BC:37:4F
    SHA-256: 2C:B7:55:77:AC:97:75:10:90:1A:F4:B4:84:33:89:A6:24:56:CF:47:61:F1:D1:46:F7:87:38:71:E4:94:21:23
*/
    val skuList = listOf(
            "weekly_2_profile",
            "weekly_3_profile",
            "monthly_2_profile",
            "monthly_3_profile",
            "six_monthly_2_profile",
            "six_monthly_3_profile",
            "annual_2_profile",
            "annual_3_profile")
    lateinit var activity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary)
        val shared1: SharedPreferences =
                getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        ctx = this@OrderSummaryActivity
        activity = this@OrderSummaryActivity

        setupBillingClient()

        if (intent != null) {
            TrialPeriod = intent.getStringExtra("TrialPeriod")
            //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
//            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = intent.getIntExtra("position", 0)
            if (intent.hasExtra("comeFrom")) {
                comeFrom = intent.getStringExtra("comeFrom")
                listModelList2 = intent.getParcelableArrayListExtra("PlanData")
            } else {
                json = intent.getStringExtra("PlanData")!!
                val type = object : TypeToken<ArrayList<PlanlistInappModel.ResponseData.Plan?>?>() {}.type
                listModelList = gson.fromJson(json, type)
            }
        }
        if (intent != null) {
            ComesTrue = intent.getStringExtra("ComesTrue")
        }
        if (intent.extras != null) {
            OldPromocode = intent.getStringExtra(CONSTANTS.Promocode)
        }
        binding!!.edtCode.addTextChangedListener(promoCodeTextWatcher)
        val p = Properties()
        p.putValue("coUserId", CoUserID)
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
        BWSApplication.addToSegment("Order Summary Viewed", p, CONSTANTS.track)
        if (!OldPromocode.equals("", ignoreCase = true)) {
            binding!!.edtCode.setText(OldPromocode)
        }
        if (!comeFrom.equals("", ignoreCase = true)) {
            binding!!.tvPromoCode.visibility = View.GONE
            binding!!.llPromoCode.visibility = View.GONE
        } else {
            binding!!.tvPromoCode.visibility = View.GONE
            binding!!.llPromoCode.visibility = View.GONE
        }
        try {
            if (!comeFrom.equals("", ignoreCase = true)) {
                binding!!.tvTrialPeriod.visibility = View.GONE
                binding!!.tvPlanInterval.text = listModelList2!![position].planInterval + " Membership"
                binding!!.tvPlanTenure.text = listModelList2!![position].planTenure
                binding!!.tvPlanNextRenewal.text = listModelList2!![position].planNextRenewal
                binding!!.tvSubName.text = listModelList2!![position].subName
                binding!!.tvSubName1.text = listModelList2!![position].subName
                binding!!.tvPlanAmount.text = "$" + listModelList2!![position].planAmount
                binding!!.tvPlanAmount1.text = "$" + listModelList2!![position].planAmount
                binding!!.tvTotalAmount.text = "$" + listModelList2!![position].planAmount
                if (listModelList2!![position].planInterval.equals("Annualy")) {
                    sku = "annual_" + listModelList!![position].profileCount!! + "_" + "profile"
                } else {
                    sku = listModelList!![position].planInterval!!.replace("-", "_").toLowerCase(Locale.getDefault()) +
                            "_" + listModelList!![position].profileCount!! + "_" + "profile"
                }
            } else {
                binding!!.tvTrialPeriod.visibility = View.VISIBLE
                binding!!.tvPlanInterval.text = listModelList!![position].planInterval + " Membership"
                binding!!.tvPlanTenure.text = listModelList!![position].planTenure
                binding!!.tvPlanNextRenewal.text = listModelList!![position].planNextRenewal
                binding!!.tvSubName.text = listModelList!![position].subName
                binding!!.tvSubName1.text = listModelList!![position].subName
                binding!!.tvTrialPeriod.text = listModelList!![position].freeTrial
                binding!!.tvPlanAmount.text = "$" + listModelList!![position].planAmount
                binding!!.tvPlanAmount1.text = "$" + listModelList!![position].planAmount
                binding!!.tvTotalAmount.text = "$" + listModelList!![position].planAmount

                if (listModelList!![position].planInterval.equals("Annualy")) {
                    sku = "annual_" + listModelList!![position].profileCount!! + "_" + "profile"
                } else {
                    sku = listModelList!![position].planInterval!!.replace("-", "_").toLowerCase(Locale.getDefault()) +
                            "_" + listModelList!![position].profileCount!! + "_" + "profile"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.llBack.setOnClickListener { view ->
            if (!comeFrom.equals("", ignoreCase = true)) {
                val i = Intent(ctx, MembershipChangeActivity::class.java)
                i.putExtra("ComeFrom", ComesTrue)
                startActivity(i)
                finish()
            } else {
                finish()
            }
        }
        binding!!.btnApply.setOnClickListener { prepareCheckReferCode(binding!!.edtCode.text.toString()) }
        /*binding!!.btnCheckout.setOnClickListener { view ->
            try {
                  if (binding!!.edtCode.getText().toString().equalsIgnoreCase("")) {
                      Promocode = "";
                      Properties p1 = new Properties();
                      if (!comeFrom.equalsIgnoreCase("")) {
                          if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                              return;
                          }
                          mLastClickTime = SystemClock.elapsedRealtime();
                          Gson gson;
                          GsonBuilder gsonBuilder = new GsonBuilder();
                          gson = gsonBuilder.create();
                          p1.putValue("plan", gson.toJson(listModelList2));
                          p1.putValue("planStartDt ", "");
                          p1.putValue("planExpiryDt", listModelList2.get(position).getPlanNextRenewal());
                          p1.putValue("planRenewalDt", listModelList2.get(position).getPlanNextRenewal());
                          p1.putValue("planAmount", listModelList2.get(position).getPlanAmount());
                        */
        /*  Intent i = new Intent(ctx, PaymentActivity.class);
                                   i.putExtra("ComesTrue", ComesTrue);
                                   i.putExtra("comeFrom", "membership");
                                   i.putParcelableArrayListExtra("PlanData", listModelList2);
                                   i.putExtra("TrialPeriod", "");
                                   i.putExtra("position", position);
                                   startActivity(i);
                                   finish();*/
        /*
                               } else {
                                   if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                       return;
                                   }
                                   mLastClickTime = SystemClock.elapsedRealtime();
                                   GsonBuilder gsonBuilder = new GsonBuilder();
                                   Gson gson;
                                   gson = gsonBuilder.create();
                                   p1.putValue("plan", gson.toJson(listModelList));
                                   p1.putValue("planStartDt ", "");
                                   p1.putValue("planExpiryDt", listModelList.get(position).getPlanNextRenewal());
                                   p1.putValue("planRenewalDt", listModelList.get(position).getPlanNextRenewal());
                                   p1.putValue("planAmount", listModelList.get(position).getPlanAmount());
                                   Intent i = new Intent(ctx, ThankYouMpActivity.class);
                                   i.putExtra("Name", "");
                                   i.putExtra("Code", "");
                                   i.putExtra("MobileNo", "");
                                   i.putExtra("PlanData", gson.toJson(listModelList));
                                   i.putExtra("TrialPeriod", TrialPeriod);
                                   i.putExtra("position", position);
                                   i.putExtra("Promocode", Promocode);
                                   startActivity(i);
                                   finish();
                               }
                               BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track);
                           } else {
                               Promocode = binding!!.edtCode.getText().toString();
                               if (BWSApplication.isNetworkConnected(ctx)) {
                                   BWSApplication.showProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity);
                                   Call<CheckReferCodeModel> listCall = APIClient.getClient().CheckReferCode(Promocode);
                                   listCall.enqueue(new Callback<CheckReferCodeModel>() {
                                       @Override
                                       public void onResponse(Call<CheckReferCodeModel> call, Response<CheckReferCodeModel> response) {
                                               CheckReferCodeModel listModel = response.body();
                                               if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                   BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity);
                                                   if (!listModel.getResponseData().getCodeExist().equalsIgnoreCase("0")){
                                                       BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                                       Properties p1 = new Properties();
                                                       if (!comeFrom.equalsIgnoreCase("")) {
                                                           if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                               return;
                                                           }
                                                           mLastClickTime = SystemClock.elapsedRealtime();
                                                           Gson gson;
                                                           GsonBuilder gsonBuilder = new GsonBuilder();
                                                           gson = gsonBuilder.create();
                                                           p1.putValue("plan", gson.toJson(listModelList2));
                                                           p1.putValue("planStartDt ", "");
                                                           p1.putValue("planExpiryDt", listModelList2.get(position).getPlanNextRenewal());
                                                           p1.putValue("planRenewalDt", listModelList2.get(position).getPlanNextRenewal());
                                                           p1.putValue("planAmount", listModelList2.get(position).getPlanAmount());
                                                        */
        /*   Intent i = new Intent(ctx, PaymentActivity.class);
                                                           i.putExtra("ComesTrue", ComesTrue);
                                                           i.putExtra("comeFrom", "membership");
                                                           i.putParcelableArrayListExtra("PlanData", listModelList2);
                                                           i.putExtra("TrialPeriod", "");
                                                           i.putExtra("position", position);
                                                           startActivity(i);
                                                           finish();*/
        /*
                                                       } else {
                                                           if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                               return;
                                                           }
                                                           mLastClickTime = SystemClock.elapsedRealtime();
                                                           Gson gson;
                                                           GsonBuilder gsonBuilder = new GsonBuilder();
                                                           gson = gsonBuilder.create();
                                                           p1.putValue("plan", gson.toJson(listModelList));
                                                           p1.putValue("planStartDt ", "");
                                                           p1.putValue("planExpiryDt", listModelList.get(position).getPlanNextRenewal());
                                                           p1.putValue("planRenewalDt", listModelList.get(position).getPlanNextRenewal());
                                                           p1.putValue("planAmount", listModelList.get(position).getPlanAmount());
                                                           Intent i = new Intent(ctx, ThankYouMpActivity.class);
                                                           i.putExtra("Name", "");
                                                           i.putExtra("Code", "");
                                                           i.putExtra("MobileNo", "");
                                                           i.putExtra("PlanData", gson.toJson(listModelList));
                                                           i.putExtra("TrialPeriod", TrialPeriod);
                                                           i.putExtra("position", position);
                                                           i.putExtra("Promocode", Promocode);
                                                           startActivity(i);
                                                           finish();
                                                       }
                                                       BWSApplication.addToSegment("Checkout Proceeded", p1, CONSTANTS.track);
                                                   }else {
                                                       BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                                   }
                                               }
                                       }

                                       @Override
                                       public void onFailure(Call<CheckReferCodeModel> call, Throwable t) {
                                           BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity);
                                       }
                                   });
                               } else {
                                   BWSApplication.showToast(getString(R.string.no_server_found), activity);
                               }
                           }

                       } catch (Exception e) {
                           e.printStackTrace();
                       }

        }*/
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is setup
                    Log.e("Setup Billing Done", "")
//                    checkPurchases()
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("Failed", "")

            }
        })
    }
    private fun loadAllSKUs() =
            if (billingClient.isReady) {
                val params = SkuDetailsParams
                        .newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.SUBS)
                        .build()
                billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    // Process the result.

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                        for (skuDetails in skuDetailsList) {
                            if (skuDetails.sku == sku)
                                binding!!.btnCheckout.setOnClickListener {
                                    val billingFlowParams = BillingFlowParams
                                            .newBuilder()
                                            .setSkuDetails(skuDetails)
//                                    .setOldSku(skuList[1],sku)
//                                    .setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION)
                                            .build()
                                    billingClient.launchBillingFlow(this, billingFlowParams)

                                    val p = Properties()
                                    p.putValue("coUserId", CoUserID)
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
                                    BWSApplication.addToSegment("Checkout Proceeded", p, CONSTANTS.track)
                                }
                        }
                    }
                }

            } else {
                println("Billing Client not ready")
            }

    override fun onBackPressed() {
        if (!comeFrom.equals("", ignoreCase = true)) {
            val i = Intent(ctx, MembershipChangeActivity::class.java)
            i.putExtra("ComeFrom", ComesTrue)
            startActivity(i)
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
                binding!!.btnApply.setTextColor(resources.getColor(R.color.gray))
            } else {
                binding!!.btnApply.isEnabled = true
                binding!!.btnApply.setTextColor(resources.getColor(R.color.dark_yellow))
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun prepareCheckReferCode(promoCode: String?) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
            val listCall = APIClient.getClient().CheckReferCode(promoCode)
            listCall.enqueue(object : Callback<CheckReferCodeModel?> {
                override fun onResponse(call: Call<CheckReferCodeModel?>, response: Response<CheckReferCodeModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                            if (listModel.responseData.codeExist.equals("0", ignoreCase = true)) {
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
                    BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                val gson = Gson()
                val shared = ctx.getSharedPreferences(CONSTANTS.InAppPurchase, Context.MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREF_KEY_Purchase, gson.toJson(purchase))
                editor.commit()
                purchase.originalJson
                Log.e("purchase Original json", gson.toJson(purchase.originalJson))
                acknowledgePurchase(purchase.purchaseToken, purchases)
                Log.e("Purchase Token", purchase.purchaseToken)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

        } else {
            // Handle any other error codes.
        }
    }

    private fun acknowledgePurchase(purchaseToken: String, purchase: MutableList<Purchase>?) {
        val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
        billingClient.acknowledgePurchase(params, this)
        billingClient.acknowledgePurchase(params) { billingResult ->
            checkPurchases()
            /*   val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            val i = Intent(ctx, ThankYouMpActivity::class.java)
            i.putExtra("Name", "")
            i.putExtra("Code", "")
            i.putExtra("MobileNo", "")
            i.putExtra("PlanData", gson.toJson(listModelList))
            i.putExtra("TrialPeriod", TrialPeriod)
            i.putExtra("position", position)
            i.putExtra("Promocode", Promocode)
            startActivity(i)
            finish()*/

            val p = Properties()
            p.putValue("coUserId", CoUserID)
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
            BWSApplication.addToSegment("Checkout Completed", p, CONSTANTS.track)
        }
    }

    private fun checkPurchases() {
        val client = BillingClient.newBuilder(application)
                .enablePendingPurchases()
                .setListener { billingResult, list -> }
                .build()
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@NonNull billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    client.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, list ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            loadAllSKUsUpdate(list!![0].skus.toString(),list!![0].purchaseToken)
//                            consumePurchases(list!![0].purchaseToken)
                            val gson = Gson()
                            Log.e("purchase list", gson.toJson(list))
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }
    private fun loadAllSKUsUpdate(oldsku:String,token: String) =
            if (billingClient.isReady) {
                val params = SkuDetailsParams
                        .newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.SUBS)
                        .build()
                billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    // Process the result.

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                        for (skuDetails in skuDetailsList) {
                            if (skuDetails.sku == sku)
                                binding!!.btnCheckout.setOnClickListener {
// Retrieve a value for "skuDetails" by calling querySkuDetailsAsync()
                                    val flowParams = BillingFlowParams.newBuilder()
                                                .setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams
                                                    .newBuilder().setOldSkuPurchaseToken(token)
                                                    .setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION)
                                                        .build())
                                            .setSkuDetails(skuDetails)
                                            .build()
                                    billingClient.launchBillingFlow(activity, flowParams)
                              /*      val billingFlowParams = BillingFlowParams
                                            .newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .setOldSku(oldsku, sku)
                                            .setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION)
                                            .build()*/
//                                    billingClient.launchBillingFlow(this, billingFlowParams)

                                    val p = Properties()
                                    p.putValue("coUserId", CoUserID)
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
                                    BWSApplication.addToSegment("Checkout Proceeded", p, CONSTANTS.track)
                                }
                        }
                    }
                }

            } else {
                println("Billing Client not ready")
            }
    private fun consumePurchases(purchaseToken: String) {
        val consumeParams = ConsumeParams
                .newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
        Log.e("consumes", consumeParams.purchaseToken)
        billingClient.consumeAsync(consumeParams, this)

        val acknowledgePurchaseParams =
        AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(consumeParams.purchaseToken)
                .build();
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);

    }
    override fun onPurchaseHistoryResponse(p0: BillingResult, p1: MutableList<PurchaseHistoryRecord>?) {

    }

    override fun onConsumeResponse(billingResult: BillingResult, p1: String) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e("Consume", p1)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

            Log.e("Consume canceled", p1)
        }
    }

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.e("Consume aaa ", billingResult.debugMessage)
        }
    }
}