package com.brainwellnessspa.billingOrderModule.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.addPaymentStripe.AddPaymentActivity
import com.brainwellnessspa.billingOrderModule.models.CardListModel
import com.brainwellnessspa.billingOrderModule.models.CardModel
import com.brainwellnessspa.billingOrderModule.models.SegmentPayment
import com.brainwellnessspa.databinding.CardsListLayoutBinding
import com.brainwellnessspa.databinding.FragmentPaymentBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding
    var adapter: AllCardAdapter? = null
    var userID: String? = null
    lateinit var act : Activity
    var p: Properties? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        val view: View = binding.root
        act = requireActivity()
        val shared = act.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvCardList.layoutManager = mLayoutManager
        binding.rvCardList.itemAnimator = DefaultItemAnimator()
        binding.llAddNewCard.setOnClickListener {
            if (BWSApplication.isNetworkConnected(activity)) {
                val i = Intent(activity, AddPaymentActivity::class.java)
                i.putExtra("ComePayment", "1")
                startActivity(i)
                /*Properties p1 = new Properties();
        p1.putValue("userId", UserID);
        BWSApplication.addToSegment("Payment Card Add Clicked", p1, CONSTANTS.track);*/
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
        return view
    }

    override fun onResume() {
        prepareCardList()
        super.onResume()
    }

    private fun prepareCardList() {
        try {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<CardListModel> = APINewClient.client.getCardLists(userID)
            listCall.enqueue(object : Callback<CardListModel?> {
                override fun onResponse(call: Call<CardListModel?>, response: Response<CardListModel?>) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val cardListModel: CardListModel? = response.body()
                    if (cardListModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                        p = Properties()
                        p!!.putValue("UserID", userID)
                        if (cardListModel.responseData!!.isEmpty()) {
                            binding.rvCardList.adapter = null
                            binding.rvCardList.visibility = View.GONE
                            p!!.putValue("paymentCards", "")
                        } else {
                            binding.rvCardList.visibility = View.VISIBLE
                            adapter = AllCardAdapter(cardListModel.responseData!!, activity!!, userID!!, binding.progressBar, binding.progressBarHolder, binding.rvCardList)
                            binding.rvCardList.adapter = adapter
                            val section1: ArrayList<SegmentPayment> = ArrayList<SegmentPayment>()
                            val e = SegmentPayment()
                            val gson = Gson()
                            for (i in cardListModel.responseData!!.indices) {
                                e.cardId = cardListModel.responseData!![i].customer
                                e.cardNumber = activity!!.getString(R.string.first_card_chars) + " " + cardListModel.responseData!![i].last4
                                e.cardHolderName = ""
                                e.cardExpiry = "Valid: " + cardListModel.responseData!![i].expMonth.toString() + "/" + cardListModel.responseData!![i].expYear
                                section1.add(e)
                            }
                            p!!.putValue("paymentCards", gson.toJson(section1))
                        }
                        BWSApplication.addToSegment("Payment Screen Viewed", p, CONSTANTS.screen)
                    } else {
                        BWSApplication.showToast(cardListModel.responseMessage, activity)
                    }
                }

                override fun onFailure(call: Call<CardListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class AllCardAdapter(private val listModelList: List<CardListModel.ResponseData>, var activity: FragmentActivity, var UserID: String, var ImgV: ProgressBar, var progressBarHolder: FrameLayout, var rvCardList: RecyclerView) : RecyclerView.Adapter<AllCardAdapter.MyViewHolder>() {
        var card_id: String? = null
        var adapter: AllCardAdapter? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: CardsListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.cards_list_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel = listModelList[position]
            holder.binding.tvCardNo.text = activity.getString(R.string.first_card_chars) + " " + listModel.last4
            holder.binding.tvExpiryTime.text = "Valid: " + listModel.expMonth + "/" + listModel.expYear
            Glide.with(activity).load(listModel.image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.ivCardimg)
            if (listModel.isDefault.equals(CONSTANTS.FLAG_ONE, ignoreCase = true)) {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_checked_icon)
                card_id = listModel.customer
                val shared = activity.getSharedPreferences(CONSTANTS.PREF_KEY_CardID, Context.MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREF_KEY_CardID, card_id)
                editor.commit()
            } else {
                holder.binding.ivCheck.setImageResource(R.drawable.ic_unchecked_icon)
            }
            holder.binding.llAddNewCard.setOnClickListener { view ->
                if (BWSApplication.isNetworkConnected(activity)) {
                    BWSApplication.showProgressBar(ImgV, progressBarHolder, activity)
                    val listCall: Call<CardListModel> = APINewClient.client.getChangeCard(UserID, listModel.customer)
                    listCall.enqueue(object : Callback<CardListModel?> {
                        override fun onResponse(call: Call<CardListModel?>, response: Response<CardListModel?>) {
                            try {
                                val cardListModel = response.body()
                                if (cardListModel!!.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                                    if (cardListModel.responseData!!.size == 0) {
                                        rvCardList.adapter = null
                                        rvCardList.visibility = View.GONE
                                    } else {
                                        rvCardList.visibility = View.VISIBLE
                                        adapter = AllCardAdapter(cardListModel.responseData!!, activity, UserID, ImgV, progressBarHolder, rvCardList)
                                        rvCardList.adapter = adapter
                                    }
                                    BWSApplication.showToast(cardListModel.responseMessage, activity)
                                } else {
                                    BWSApplication.showToast(cardListModel.responseMessage, activity)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<CardListModel?>, t: Throwable) {
                            BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                        }
                    })
                } else {
                    BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
                    BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                }
            }
            holder.binding.rlRemoveCard.setOnClickListener { view ->
                val dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.delete_payment_card)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.dark_blue_gray)))
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
                            if (BWSApplication.isNetworkConnected(activity)) {
                                BWSApplication.showProgressBar(ImgV, progressBarHolder, activity)
                                val listCall: Call<CardModel> = APINewClient.client.getRemoveCard(UserID, listModel.customer)
                                listCall.enqueue(object : Callback<CardModel?> {
                                    override fun onResponse(call: Call<CardModel?>, response: Response<CardModel?>) {
                                        try {
                                            BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                                            if (response.isSuccessful) {
                                                val cardModel = response.body()
                                                if (cardModel!!.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                    cardList
                                                    dialog.dismiss()
                                                    BWSApplication.showToast(cardModel.responseMessage, activity)
                                                } else {
                                                    BWSApplication.showToast(cardModel.responseMessage, activity)
                                                }
                                            }
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<CardModel?>, t: Throwable) {
                                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
                                BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
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

        inner class MyViewHolder(binding: CardsListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            var binding: CardsListLayoutBinding

            init {
                this.binding = binding
            }
        }

        private val cardList: Unit
            private get() {
                if (BWSApplication.isNetworkConnected(activity)) {
                    BWSApplication.showProgressBar(ImgV, progressBarHolder, activity)
                    val listCall: Call<CardListModel> = APINewClient.client.getCardLists(UserID)
                    listCall.enqueue(object : Callback<CardListModel?> {
                        override fun onResponse(call: Call<CardListModel?>, response: Response<CardListModel?>) {
                            try {
                                if (response.isSuccessful) {
                                    BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                                    val cardListModel = response.body()
                                    if (cardListModel!!.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                        if (cardListModel.responseData!!.size == 0) {
                                            rvCardList.adapter = null
                                            rvCardList.visibility = View.GONE
                                        } else {
                                            rvCardList.visibility = View.VISIBLE
                                            adapter = AllCardAdapter(cardListModel.responseData!!, activity, UserID, ImgV, progressBarHolder, rvCardList)
                                            rvCardList.adapter = adapter
                                        }
                                    } else {
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<CardListModel?>, t: Throwable) {
                            BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity)
                        }
                    })
                } else {
                    BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
                }
            }

    }

}