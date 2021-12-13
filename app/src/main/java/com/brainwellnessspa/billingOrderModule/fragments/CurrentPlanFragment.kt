package com.brainwellnessspa.billingOrderModule.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity
import com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity.Companion.myBackPressbill
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.membershipModule.activities.StripeEnhanceMembershipUpdateActivity
import com.brainwellnessspa.billingOrderModule.models.CurrentPlanVieViewModel
import com.brainwellnessspa.billingOrderModule.models.PayNowDetailsModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.FeaturedLayoutBinding
import com.brainwellnessspa.databinding.FragmentCurrentPlanBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.MeasureRatio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*Active => Cancel button
remaining 10 days =>cancelled status=> pay now button => => Direct payment
after complete plan(10days)=>in active => pay now => plan selection
suspended => paynow => Direct payment*/
class CurrentPlanFragment : Fragment() {
    lateinit var binding: FragmentCurrentPlanBinding
    var UserID: String? = null // coUserIsD
    private var mLastClickTime: Long = 0
    lateinit var act:Activity
    var adpater: FeaturedListAdpater? = null
    var planVieViewModel: CurrentPlanVieViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_plan, container, false)
        val view = binding.root
        act = requireActivity()
        val shared1 = act.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val serachList: RecyclerView.LayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvFeatured.layoutManager = serachList
        binding.rvFeatured.itemAnimator = DefaultItemAnimator()
        //        PrepareData();
        binding.btnCancelSubscrible.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val i = Intent(activity, CancelMembershipActivity::class.java)
            startActivity(i)
        }
        binding.tvChangeCard.setOnClickListener {
            myBackPressbill = true
            val i = Intent(activity, BillingOrderActivity::class.java)
            i.putExtra("payment", 1)
            startActivity(i)
            act.finish()
        }
        return view
    }

    private fun PrepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
        val listCall: Call<CurrentPlanVieViewModel> = APINewClient.client.getCurrentPlanView(UserID)
        listCall.enqueue(object : Callback<CurrentPlanVieViewModel?> {
            override fun onResponse(call: Call<CurrentPlanVieViewModel?>, response: Response<CurrentPlanVieViewModel?>) {
                val listModel: CurrentPlanVieViewModel? = response.body()
                if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess))) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    try {
                        planVieViewModel = listModel
                        binding.tvHeader.text = listModel.responseData!!.plan
                        val measureRatio: MeasureRatio = BWSApplication.measureRatio(activity, 0f, 5f, 3f, 1f, 0f)
                        binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                        binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                        binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                        binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner)
                        if (listModel.responseData!!.activate.equals("")) {
                            binding.tvPlan.text = ""
                            binding.tvPlan.visibility = View.GONE
                        } else {
                            binding.tvPlan.visibility = View.VISIBLE
                            binding.tvPlan.text = "Active Since: " + listModel.responseData!!.activate
                        }
                        //                        val shared = act.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                        //                        val editor = shared.edit()
                        //                        editor.putString(CONSTANTS.PREF_KEY_ExpDate, listModel.responseData.getExpireDate())
                        //                        editor.putString(CONSTANTS.PREF_KEY_IsLock, IsLock)
                        //                        editor.commit()
                        if (listModel.responseData!!.reattempt.equals("")) {
                            binding.tvSubName.text = listModel.responseData!!.subtitle
                        } else {
                            binding.tvSubName.text = listModel.responseData!!.reattempt
                        }
                        val planAmount = "$" + listModel.responseData!!.orderTotal.toString() + " "
                        binding.tvPlanAmount.text = planAmount
                        binding.tvPlanInterval.text = listModel.responseData!!.planStr
                        binding.tvPayUsing.text = listModel.responseData!!.cardDigit
                        invoicePayId = listModel.responseData!!.invoicePayId
                        PlanStatus = listModel.responseData!!.status!!
                        if (listModel.responseData!!.status.equals("1")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.green_background)
                            binding.tvRecommended.text = getString(R.string.Active)
                            binding.btnCancelSubscrible.visibility = View.VISIBLE
                            binding.btnPayNow.visibility = View.GONE
                            binding.tvPayUsing.visibility = View.GONE
                            binding.tvChangeCard.visibility = View.GONE
                        } else if (listModel.responseData!!.status.equals("2")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_brown_background)
                            binding.tvRecommended.text = getString(R.string.InActive)
                            binding.btnCancelSubscrible.visibility = View.GONE
                            binding.btnPayNow.setVisibility(View.VISIBLE)
                            binding.tvPayUsing.visibility = View.GONE
                            binding.tvChangeCard.visibility = View.GONE
                            binding.btnPayNow.setOnClickListener { view1 ->
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return@setOnClickListener
                                }
                                mLastClickTime = SystemClock.elapsedRealtime()
                                val i = Intent(activity, StripeEnhanceMembershipUpdateActivity::class.java)
                                i.putExtra("ComeFrom", "")
                                startActivity(i)
                            }
                        } else if (listModel.responseData!!.status.equals("3")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.yellow_background)
                            binding.tvRecommended.text = getString(R.string.Suspended)
                            binding.btnCancelSubscrible.visibility = View.GONE
                            binding.btnPayNow.visibility = View.VISIBLE
                            binding.tvPayUsing.visibility = View.VISIBLE
                            binding.tvChangeCard.visibility = View.VISIBLE
                            binding.btnPayNow.setOnClickListener { view1 ->
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return@setOnClickListener
                                }
                                mLastClickTime = SystemClock.elapsedRealtime()
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<PayNowDetailsModel> = APINewClient.client.getPayNowDetails(UserID, listModel.responseData!!.cardId, listModel.responseData!!.planId, listModel.responseData!!.planFlag, listModel.responseData!!.invoicePayId, listModel.responseData!!.status)
                                listCall.enqueue(object : Callback<PayNowDetailsModel?> {
                                    override fun onResponse(call: Call<PayNowDetailsModel?>, response: Response<PayNowDetailsModel?>) {
                                        try {
                                            if (response.isSuccessful) {
                                                myBackPressbill = true
                                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                                val listModel1: PayNowDetailsModel? = response.body()
                                                BWSApplication.showToast(listModel1!!.responseMessage, activity)
                                                act.finish()
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<PayNowDetailsModel?>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            }
                        } else if (listModel.responseData!!.status.equals("4")) {
                            binding.tvRecommended.text = getString(R.string.Cancelled)
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_red_background)
                            binding.btnCancelSubscrible.visibility = View.GONE
                            binding.btnPayNow.visibility = View.GONE
                            binding.tvPayUsing.visibility = View.GONE
                            binding.tvChangeCard.visibility = View.GONE
                        }
                        adpater = FeaturedListAdpater(listModel.responseData!!.feature!!)
                        binding.rvFeatured.adapter = adpater
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<CurrentPlanVieViewModel?>, t: Throwable) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            }
        })
    }

    override fun onResume() {
        PrepareData()
        super.onResume()
    }

    inner class FeaturedListAdpater(var modelList: List<CurrentPlanVieViewModel.ResponseData.Feature>) : RecyclerView.Adapter<FeaturedListAdpater.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FeaturedLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.featured_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvFeatured.text = modelList[position].feature
        }

        override fun getItemCount(): Int {
            return modelList.size
        }
        inner class MyViewHolder(var binding: FeaturedLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        var invoicePayId: String? = null
        var PlanStatus = ""
    }
}