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
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.MembershipChangeActivity
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding
import com.brainwellnessspa.referralModule.models.CheckReferCodeModel
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OrderSummaryActivity : AppCompatActivity(), PurchasesUpdatedListener, PurchaseHistoryResponseListener, ConsumeResponseListener, AcknowledgePurchaseResponseListener {
    var binding: ActivityOrderSummaryBinding? = null
    var TrialPeriod: String? = ""
    var comeFrom: String? = ""
    var UserId: String? = ""/* renewPlanFlag, renewPlanId, */

    /* renewPlanFlag, renewPlanId, */
    var CoUserID: String? = ""
    var ComesTrue: String? = ""
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
    //861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
    //TODO : Oauth Client Secret
    //CLIENT_SECRET = "0hQBynI-gzUrHQtSR-ayUFaK"
    // code :- 4%2F0AY0e-g5HwhmC7D1M2ab--RVBhI2HkU5n1qMJPE3UgQlWa3XoB23tDojyKsd0fw6w_VwS5Q
    /*   MD5: 4D:09:22:47:FD:AD:E3:8B:DD:61:4F:65:BA:66:99:37
    SHA1: F5:37:43:D2:FC:73:4E:6C:51:8C:D7:E7:BE:88:D7:3A:4E:BC:37:4F
    SHA-256: 2C:B7:55:77:AC:97:75:10:90:1A:F4:B4:84:33:89:A6:24:56:CF:47:61:F1:D1:46:F7:87:38:71:E4:94:21:23
    code : - 4/eWdxD7b-YSQ5CNNb-c2iI83KQx19.wp6198ti5Zc7dJ3UXOl0T3aRLxQmbwI
*/
    val skuList = listOf("weekly_2_profile", "weekly_3_profile", "monthly_2_profile", "monthly_3_profile", "six_monthly_2_profile", "six_monthly_3_profile", "annual_2_profile", "annual_3_profile")
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        ctx = this@OrderSummaryActivity
        activity = this@OrderSummaryActivity

        setupBillingClient()

        if (intent != null) {
            TrialPeriod = intent.getStringExtra("TrialPeriod") //            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
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
                    sku = listModelList!![position].planInterval!!.replace("-", "_").toLowerCase(Locale.getDefault()) + "_" + listModelList!![position].profileCount!! + "_" + "profile"
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
                    sku = listModelList!![position].planInterval!!.replace("-", "_").toLowerCase(Locale.getDefault()) + "_" + listModelList!![position].profileCount!! + "_" + "profile"
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
        binding!!.btnApply.setOnClickListener { prepareCheckReferCode(binding!!.edtCode.text.toString()) }/*binding!!.btnCheckout.setOnClickListener { view ->
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
                        *//*  Intent i = new Intent(ctx, PaymentActivity.class);
                                   i.putExtra("ComesTrue", ComesTrue);
                                   i.putExtra("comeFrom", "membership");
                                   i.putParcelableArrayListExtra("PlanData", listModelList2);
                                   i.putExtra("TrialPeriod", "");
                                   i.putExtra("position", position);
                                   startActivity(i);
                                   finish();*//*
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
                                                        *//*   Intent i = new Intent(ctx, PaymentActivity.class);
                                                           i.putExtra("ComesTrue", ComesTrue);
                                                           i.putExtra("comeFrom", "membership");
                                                           i.putParcelableArrayListExtra("PlanData", listModelList2);
                                                           i.putExtra("TrialPeriod", "");
                                                           i.putExtra("position", position);
                                                           startActivity(i);
                                                           finish();*//*
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
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) { // The BillingClient is setup
                    Log.e("Setup Billing Done", "") //                    checkPurchases()
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("Failed", "")

            }
        })
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList -> // Process the result.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == sku) binding!!.btnCheckout.setOnClickListener {
                        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails) //                                    .setOldSku(skuList[1],sku)
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
                            if (listModel.responseData!!.codeExist.equals("0", ignoreCase = true)) {
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
                editor.putString(CONSTANTS.PREF_KEY_PurchaseToken, purchase.purchaseToken)
                editor.putString(CONSTANTS.PREF_KEY_PurchaseID, sku)
                editor.commit()
                purchase.originalJson
                Log.e("purchase Original json", gson.toJson(purchase.originalJson))
                acknowledgePurchase(purchase.purchaseToken, purchases)
                Log.e("Purchase Token", purchase.purchaseToken)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) { // Handle an error caused by a user cancelling the purchase flow.

        } else { // Handle any other error codes.
        }
    }

    private fun acknowledgePurchase(purchaseToken: String, purchase: MutableList<Purchase>?) {
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient.acknowledgePurchase(params, this)
        billingClient.acknowledgePurchase(params) { billingResult ->
            checkPurchases()/*   val responseCode = billingResult.responseCode
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
        val client = BillingClient.newBuilder(application).enablePendingPurchases().setListener { billingResult, list -> }.build()
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
                                    TODO("Not yet implemented")
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
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.SUBS).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList -> // Process the result.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == sku) binding!!.btnCheckout.setOnClickListener { // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync()
                        val flowParams = BillingFlowParams.newBuilder().setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldSkuPurchaseToken(token).setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION).build()).setSkuDetails(skuDetails).build()
                        billingClient.launchBillingFlow(activity, flowParams)/*      val billingFlowParams = BillingFlowParams
                                            .newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .setOldSku(oldsku, sku)
                                            .setReplaceSkusProrationMode(IMMEDIATE_WITH_TIME_PRORATION)
                                            .build()*/ //                                    billingClient.launchBillingFlow(this, billingFlowParams)

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
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        Log.e("consumes", consumeParams.purchaseToken)
        billingClient.consumeAsync(consumeParams, this)

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(consumeParams.purchaseToken).build();
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);

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
}/*
   public static String GOOGLE_AUTHORIZATION = "authorization_code";
    public static String GOOGLE_CLIENT_ID = "861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com";
    public static String GOOGLE_CLIENT_SECRET = "0hQBynI-gzUrHQtSR-ayUFaK";
    public static String GOOGLE_CODE = "4/0AY0e-g7LoHIAy2ulpKIfeciuZSkBrFBh6RseKYb372xyxSb7Gmleh-vHyywKqAEGWlTiMg";
//    public static String GOOGLE_CODE = "4/0AY0e-g611fwyaxP6xRR4fEMDBbIxZExfTJ_sB8TKGji28VzLM3XTViiPv6yNuY-BOk_b5Q";
//            "4/0AY0e-g7QBZ_mWlvZnM0WncCwEo9Y6YslTFbyc61ToQwcG4pqJLSKgR99ibIbkEBZaQanxQ";
//    public static String GOOGLE_CODE = "4%2F0AY0e-g5HwhmC7D1M2ab--RVBhI2HkU5n1qMJPE3UgQlWa3XoB23tDojyKsd0fw6w_VwS5Q";
//                                         4%2F0AY0e-g611fwyaxP6xRR4fEMDBbIxZExfTJ_sB8TKGji28VzLM3XTViiPv6yNuY-BOk_b5Q
//                                        4%2F0AY0e-g7QBZ_mWlvZnM0WncCwEo9Y6YslTFbyc61ToQwcG4pqJLSKgR99ibIbkEBZaQanxQ
//                                          4%2F0AY0e-g75th0nAoXj0X5W9BeW7e3NwbqzAN_eNk_MLrkwxIlmAV30tGHCObjHPawlYdNpEQ
    public static String GOOGLE_REDIRECT_URI = "https://brainwellnessspa.com.au/";
    public static String GOOGLE_ACCESS_TYPE = "offline";
    public static String GOOGLE_REDIRECT_URI1 = "https://brainwellnessspa.com.au/?code=4%2F0AY0e-g5HwhmC7D1M2ab--RVBhI2HkU5n1qMJPE3UgQlWa3XoB23tDojyKsd0fw6w_VwS5Q&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fandroidpublisher/";

   public static SubscriptionPurchase getRefreshToken() {
        String refreshToken = "";
        String accessToken = "";
        SubscriptionPurchase subscription = new SubscriptionPurchase();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
//https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&redirect_uri=https://brainwellnessspa.com.au/&client_id=861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
//https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&access_type=offline&redirect_uri=https://brainwellnessspa.com.au/&client_id=861076939494-enq38ui5d9hcbhmt3h972aok62c723ns.apps.googleusercontent.com
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