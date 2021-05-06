package com.brainwellnessspa.ManageModule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.PlanlistInappModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityManageBinding
import com.brainwellnessspa.databinding.MembershipFaqLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageBinding
    lateinit var adapter: MembershipFaqAdapter
    lateinit var activity: Activity
    var USERID: String? = null
    var CoUserID: String? = null
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage)
        activity = this@ManageActivity
        val shared1: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        ctx = this@ManageActivity
        binding.llBack.setOnClickListener { _ ->
            finish()
        }


//        SubscriptionAdapter TODO Audio List Adapter
        prepareUserData();
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlanlistInappModel> = APINewClient.getClient().getPlanlistInapp(CoUserID)
            listCall.enqueue(object : Callback<PlanlistInappModel> {
                override fun onResponse(
                    call: Call<PlanlistInappModel>,
                    response: Response<PlanlistInappModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: PlanlistInappModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                            binding.ivRestaurantImage.layoutParams.height =
                                (measureRatio.height * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.layoutParams.width =
                                (measureRatio.widthImg * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                            Glide.with(ctx).load(listModel.responseData!!.image).thumbnail(0.05f)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
                                .priority(Priority.HIGH)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                                .into(binding.ivRestaurantImage)

                            binding.tvTitle.text = listModel.responseData!!.title
                            binding.tvDesc.text = listModel.responseData!!.desc
                            binding.tvPlanFeatures01.text = listModel.responseData!!.planFeatures!![0].feature
                            binding.tvPlanFeatures02.text = listModel.responseData!!.planFeatures!![1].feature
                            binding.tvPlanFeatures03.text = listModel.responseData!!.planFeatures!![2].feature
                            binding.tvPlanFeatures04.text = listModel.responseData!!.planFeatures!![3].feature

                            binding.rvFaqList.layoutManager = LinearLayoutManager(this@ManageActivity)
                            adapter = MembershipFaqAdapter(listModel.responseData!!.fAQs!!, ctx, binding.rvFaqList, binding.tvFound)
                            binding.rvFaqList.adapter = adapter
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanlistInappModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }


    class MembershipFaqAdapter(
        private val modelList: List<PlanlistInappModel.ResponseData.Faq>,
        var ctx: Context,
        var rvFaqList: RecyclerView,
        var tvFound: TextView
    ) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipFaqLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.membership_faq_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(modelList[position].title)
            holder.binding.tvDesc.setText(modelList[position].desc)
            holder.binding.ivClickRight.setOnClickListener { _ ->
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray)
                holder.binding.tvDesc.setFocusable(true)
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.setVisibility(View.VISIBLE)
                holder.binding.ivClickRight.setVisibility(View.GONE)
                holder.binding.ivClickDown.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon)
            }
            holder.binding.ivClickDown.setOnClickListener { _ ->
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.membership_faq_not_clicked)
                holder.binding.tvDesc.setVisibility(View.GONE)
                holder.binding.ivClickRight.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setVisibility(View.GONE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon)
            }
            if (modelList.size == 0) {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.GONE
            } else {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(binding: MembershipFaqLayoutBinding) :
            RecyclerView.ViewHolder(binding.getRoot()) {
            var binding: MembershipFaqLayoutBinding

            init {
                this.binding = binding
            }
        }
    }
}