package com.brainwellnessspa.membershipModule.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.MembershipPlanBinding
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import java.util.*

//import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity;
class MembershipPlanAdapter(private val listModelList: ArrayList<MembershipPlanListModel.Plan>, var ctx: Context, var btnFreeJoin: Button, var TrialPeriod: String, var activity: Activity, var i: Intent) : RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder>() {
    private var rowIndex = -1
    private var pos = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: MembershipPlanBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_plan, parent, false)
        return MyViewHolder(v)
    }

    @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listModel = listModelList[position] //        holder.binding.tvTitle.setText(listModel.getTitle());
        holder.binding.tvPlanFeatures01.text = listModel.planFeatures?.get(0)?.feature
        holder.binding.tvPlanFeatures02.text = listModel.planFeatures?.get(1)?.feature
        holder.binding.tvPlanFeatures03.text = listModel.planFeatures?.get(2)?.feature
        holder.binding.tvPlanFeatures04.text = listModel.planFeatures?.get(3)?.feature
        holder.binding.tvPlanAmount.text = "$" + listModel.planAmount
        holder.binding.tvSubName.text = listModel.subName
        holder.binding.tvPlanInterval.text = listModel.planInterval
        if (listModel.recommendedFlag.equals("1", ignoreCase = true)) {
            holder.binding.tvRecommended.visibility = View.VISIBLE/*  if (pos == 0) {
                holder.binding.llPlanSub.setBackgroundColor(ctx.getResources().getColor(R.color.blue));
                holder.binding.llFeatures.setVisibility(View.VISIBLE);
                holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.llFeatures.setBackgroundColor(ctx.getResources().getColor(R.color.white));
            }*/
        } else {
            holder.binding.tvRecommended.visibility = View.GONE
        }
        holder.binding.llPlanMain.setOnClickListener {
            rowIndex = position
            pos++
            notifyDataSetChanged()
        }
        if (rowIndex == position) {
            changeFunction(holder, listModel, position)
        } else {
            if (listModel.recommendedFlag.equals("1", ignoreCase = true) && pos == 0) {
                holder.binding.tvRecommended.visibility = View.VISIBLE
                changeFunction(holder, listModel, position)
            } else {
                holder.binding.llPlanSub.background = ContextCompat.getDrawable(activity, R.drawable.rounded_light_gray)
                holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.black))
                holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.black))
                holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.black))
                holder.binding.llFeatures.visibility = View.GONE
            }
        }
        btnFreeJoin.setOnClickListener {
            if (BWSApplication.isNetworkConnected(ctx)) {
                activity.startActivity(i)
                activity.finish()
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity)
            }
        }
    }

    private fun changeFunction(holder: MyViewHolder, listModel: MembershipPlanListModel.Plan, position: Int) {
        holder.binding.llPlanSub.setBackgroundResource(R.drawable.top_round_green_cornor)
        holder.binding.llFeatures.visibility = View.VISIBLE
        holder.binding.tvPlanAmount.setTextColor(ContextCompat.getColor(activity, R.color.white))
        holder.binding.tvSubName.setTextColor(ContextCompat.getColor(activity, R.color.white))
        holder.binding.tvPlanInterval.setTextColor(ContextCompat.getColor(activity, R.color.white))
        holder.binding.llFeatures.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
        planFlag = listModel.planFlag.toString()
        price = listModel.planAmount.toString()
        planId = listModel.planID.toString() //        i = new Intent(ctx, OrderSummaryActivity.class);
        //        i.putParcelableArrayListExtra("PlanData", listModelList);
        //        i.putExtra("TrialPeriod", TrialPeriod);
        //        i.putExtra("position", position);
        //        i.putExtra("Promocode", "");
    }

    override fun getItemCount(): Int {
        return listModelList.size
    }

    inner class MyViewHolder(var binding: MembershipPlanBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        @JvmField var planFlag = ""

        @JvmField var planId = ""

        @JvmField var price = ""
    }
}