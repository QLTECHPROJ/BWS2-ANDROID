package com.brainwellnessspa.billingOrderModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.notificationId
import com.brainwellnessspa.billingOrderModule.models.CardListModel
import com.brainwellnessspa.billingOrderModule.models.CardModel
import com.brainwellnessspa.billingOrderModule.models.PayNowDetailsModel
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.billingOrderModule.models.SegmentPayment
import com.brainwellnessspa.R
import com.brainwellnessspa.addPaymentStripeModule.AddPaymentActivity
import com.brainwellnessspa.membershipModule.activities.StripeEnhanceMembershipUpdateActivity.Companion.renewPlanFlag
import com.brainwellnessspa.membershipModule.activities.StripeEnhanceMembershipUpdateActivity.Companion.renewPlanId
import com.brainwellnessspa.billingOrderModule.fragments.CurrentPlanFragment.Companion.PlanStatus
import com.brainwellnessspa.billingOrderModule.fragments.CurrentPlanFragment.Companion.invoicePayId
import com.brainwellnessspa.databinding.ActivityPaymentBinding
import com.brainwellnessspa.databinding.CardsListLayoutBinding
import com.brainwellnessspa.membershipModule.activities.StripeEnhanceOrderSummaryActivity
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentBinding
    var adapter: AllCardsAdapter? = null
    lateinit var context: Context
    var cardId: String? = ""
    var userId: String? = ""
    var trialPeriod: String? = ""
    var comeFrom: String? = ""
    var comesTrue: String? = ""
    val gson = Gson()
    var activity: Activity? = null
    var position = 0
    var listModelList2: ArrayList<PlanListBillingModel.ResponseData.Plan>? = null
    private var listModelList: ArrayList<MembershipPlanListModel.Plan>? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        context = this@PaymentActivity
        activity = this@PaymentActivity
        val shared = context.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.rvCardList.layoutManager = mLayoutManager
        binding.rvCardList.itemAnimator = DefaultItemAnimator()
        if (intent != null) {
            if (intent.hasExtra("TrialPeriod")) {
                trialPeriod = intent.getStringExtra("TrialPeriod")
            }
            position = intent.getIntExtra("position", 0)
            if (intent.hasExtra("comeFrom")) {
                comeFrom = intent.getStringExtra("comeFrom")
                val json4 =  intent.getStringExtra("PlanData")
                val type1 = object : TypeToken<ArrayList<PlanListBillingModel.ResponseData.Plan>?>() {}.type
                listModelList2 = gson.fromJson(json4, type1)
            } else {
                val json4 =  intent.getStringExtra("PlanData")
                val type1 = object : TypeToken<ArrayList<MembershipPlanListModel.Plan>?>() {}.type
                listModelList = gson.fromJson(json4, type1)            }
        }
        if (intent != null) {
            comesTrue = intent.getStringExtra("ComesTrue")
        }
        binding.llBack.setOnClickListener {
            myBackPress = true
            val i = Intent(context, StripeEnhanceOrderSummaryActivity::class.java)
            if (intent.hasExtra("comeFrom")) {
                i.putExtra("PlanData", gson.toJson(listModelList2))
            }else{
                i.putExtra("PlanData", gson.toJson(listModelList))
            }
            i.putExtra("comeFrom", "membership")
            i.putExtra("ComesTrue", comesTrue)
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            i.putExtra("Promocode", "")
            startActivity(i)
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        binding.llAddNewCard.setOnClickListener {
            myBackPress = true
            if (BWSApplication.isNetworkConnected(context)) {
                val i = Intent(context, AddPaymentActivity::class.java)
                i.putExtra("ComePayment", "2")
                i.putExtra("ComesTrue", comesTrue)
                i.putExtra("comeFrom", "membership")
                i.putExtra("PlanData", gson.toJson(listModelList2))
                i.putExtra("TrialPeriod", "")
                i.putExtra("position", position)
                startActivity(i)
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
        //        prepareCardList();
    }

    public override fun onResume() {
        prepareCardList()
        super.onResume()
    }

    private fun prepareCardList() {
        try {
            if (BWSApplication.isNetworkConnected(context)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall: Call<CardListModel> = APINewClient.client.getCardLists(userId)
                listCall.enqueue(object : Callback<CardListModel?> {
                    override fun onResponse(call: Call<CardListModel?>, response: Response<CardListModel?>) {
                        try {
                            val cardListModel: CardListModel? = response.body()
                            if (cardListModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val p = Properties()
                                if (cardListModel.responseData!!.isEmpty()) {
                                    binding.rvCardList.adapter = null
                                    binding.rvCardList.visibility = View.GONE
                                    p.putValue("paymentCards", "")
                                } else {
                                    binding.rvCardList.visibility = View.VISIBLE
                                    adapter = AllCardsAdapter(cardListModel.responseData!!, binding.progressBar, binding.progressBarHolder, binding.rvCardList)
                                    binding.rvCardList.setAdapter(adapter)
                                    val section1: ArrayList<SegmentPayment> = ArrayList<SegmentPayment>()
                                    val e = SegmentPayment()
                                    val gson = Gson()
                                    for (i in cardListModel.responseData!!.indices) {
                                        e.cardId = (cardListModel.responseData!![i].customer)
                                        e.cardNumber = (getString(R.string.first_card_chars) + " " + cardListModel.responseData!![i].last4)
                                        e.cardHolderName = ("")
                                        e.cardExpiry = ("Valid: " + cardListModel.responseData!![i].expMonth.toString() + "/" + cardListModel.responseData!![i].expYear)
                                        section1.add(e)
                                    }
                                    p.putValue("paymentCards", gson.toJson(section1))
                                }
                                BWSApplication.addToSegment("Payment Screen Viewed", p, CONSTANTS.screen)
                                binding.btnCheckout.setOnClickListener {
                                    if (cardListModel.responseData!!.size == 0) {
                                        BWSApplication.showToast("Please enter card details", activity)
                                    } else {
                                        if (BWSApplication.isNetworkConnected(context)) {
                                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                            val listCall: Call<PayNowDetailsModel> = APINewClient.client.getPayNowDetails(userId, cardId, renewPlanId, renewPlanFlag, invoicePayId, PlanStatus)
                                            listCall.enqueue(object : Callback<PayNowDetailsModel?> {
                                                override fun onResponse(call: Call<PayNowDetailsModel?>, response: Response<PayNowDetailsModel?>) {
                                                    try {
                                                        if (response.isSuccessful) {
                                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                                            val listModel1: PayNowDetailsModel? = response.body()
                                                            BWSApplication.showToast(listModel1!!.responseMessage, activity)
                                                            val i = Intent(context, BillingOrderActivity::class.java)
                                                            startActivity(i)
                                                            finish()
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }

                                                override fun onFailure(call: Call<PayNowDetailsModel?>, t: Throwable) {
                                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                                }
                                            })
                                        } else {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                            BWSApplication.showToast(getString(R.string.no_server_found), activity)
                                        }
                                    }
                                }
                            } else {
                                BWSApplication.showToast(cardListModel.responseMessage, activity)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<CardListModel?>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class AllCardsAdapter(var listModelList: List<CardListModel.ResponseData>,var ImgV: ProgressBar,var progressBarHolder: FrameLayout,var rvCardList: RecyclerView) : RecyclerView.Adapter<AllCardsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: CardsListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.cards_list_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel: CardListModel.ResponseData = listModelList[position]
            val cardNo = getString(R.string.first_card_chars) + " " + listModel.last4
            holder.binding.tvCardNo.text = cardNo
            val expTime = "Valid: " + listModel.expMonth.toString() + "/" + listModel.expYear
            holder.binding.tvExpiryTime.text = expTime
            Glide.with(context).load(listModel.image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.ivCardimg)
            if (listModel.isDefault.equals(CONSTANTS.FLAG_ONE)) {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_checked_icon)
                cardId = listModel.customer
                val shared = getSharedPreferences(CONSTANTS.PREF_KEY_CardID, MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREF_KEY_CardID, cardId)
                editor.commit()
            } else {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_unchecked_icon)
            }
            holder.binding.llAddNewCard.setOnClickListener {
                if (BWSApplication.isNetworkConnected(context)) {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val listCall: Call<CardListModel> = APINewClient.client.getChangeCard(userId, listModel.customer)
                    listCall.enqueue(object : Callback<CardListModel?> {
                        override fun onResponse(call: Call<CardListModel?>, response: Response<CardListModel?>) {
                            try {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val cardListModel: CardListModel? = response.body()
                                if (cardListModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                    if (cardListModel.responseData!!.isEmpty()) {
                                        rvCardList.adapter = null
                                        rvCardList.visibility = View.GONE
                                    } else {
                                        val p = Properties()
                                        p.putValue("cardId",listModel.customer)
                                        BWSApplication.addToSegment("Payment Card Selected", p, CONSTANTS.track)
                                        rvCardList.visibility = View.VISIBLE
                                        adapter = AllCardsAdapter(cardListModel.responseData!!, ImgV, progressBarHolder, rvCardList)
                                        rvCardList.adapter = adapter
                                    }
                                    BWSApplication.showToast(cardListModel.responseMessage, activity)
                                } else {
                                    BWSApplication.showToast(cardListModel.responseMessage, activity)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<CardListModel?>, t: Throwable) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        }
                    })
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), activity)
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            }
            holder.binding.rlRemoveCard.setOnClickListener {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.delete_payment_card)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context,R.color.dark_blue_gray)))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                val Btn = dialog.findViewById<Button>(R.id.Btn)
                Btn.text = "DELETE"
                dialog.setOnKeyListener { v: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                Btn.setOnTouchListener { view1: View, event: MotionEvent ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val views = view1 as Button
                            views.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                            view1.invalidate()
                        }
                        MotionEvent.ACTION_UP -> {
                            if (BWSApplication.isNetworkConnected(context)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<CardModel> = APINewClient.client.getRemoveCard(userId, listModel.customer)
                                listCall.enqueue(object : Callback<CardModel?> {
                                    override fun onResponse(call: Call<CardModel?>, response: Response<CardModel?>) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                        try {
                                            if (response.isSuccessful) {
                                                val cardModel: CardModel? = response.body()
                                                if (cardModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                                    val p = Properties()
                                                    p.putValue("cardId",listModel.customer)
                                                    BWSApplication.addToSegment("Payment Card Removed", p, CONSTANTS.track)
                                                    prepareCardList()
                                                    dialog.dismiss()
                                                    BWSApplication.showToast(cardModel.responseMessage, activity)
                                                } else {
                                                    BWSApplication.showToast(cardModel.responseMessage, activity)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<CardModel?>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            }
                            run {
                                val views = view1 as Button
                                views.background.clearColorFilter()
                                views.invalidate()
                            }
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            val views = view1 as Button
                            views.background.clearColorFilter()
                            views.invalidate()
                        }
                    }
                    true
                }
                tvGoBack.setOnClickListener { v: View? -> dialog.dismiss() }
                dialog.show()
                dialog.setCancelable(false)
            }
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: CardsListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    override fun onBackPressed() {
        myBackPress = true
        val i = Intent(context, StripeEnhanceOrderSummaryActivity::class.java)
        i.putExtra("comeFrom", "membership")
        i.putExtra("ComesTrue", comesTrue)
        if (intent.hasExtra("comeFrom")) {
            i.putExtra("PlanData", gson.toJson(listModelList2))
        }else{
            i.putExtra("PlanData", gson.toJson(listModelList))
        }
        i.putExtra("TrialPeriod", "")
        i.putExtra("position", position)
        i.putExtra("Promocode", "")
        startActivity(i)
        finish()
    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
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
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}