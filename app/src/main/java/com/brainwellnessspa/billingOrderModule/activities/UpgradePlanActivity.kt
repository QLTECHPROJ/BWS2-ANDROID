package com.brainwellnessspa.billingOrderModule.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityUpgradePlanBinding

class UpgradePlanActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpgradePlanBinding
    lateinit var ctx: Context
    lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upgrade_plan)
        ctx = this@UpgradePlanActivity
        act = this@UpgradePlanActivity

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.rvPlanList.layoutManager = LinearLayoutManager(act)

//        planListAdapter = PlanListAdapter(
//            listModelList, ctx, i
//        )
//        binding.rvPlanList.adapter = planListAdapter
    }

    /* class PlanListAdapter(
         var listModelList: List<PlanlistInappModel.ResponseData.Plan>,
         var ctx: Context,
         var i: Intent
     ) :
         RecyclerView.Adapter<PlanListAdapter.MyViewHolder>()*//*, Filterable *//* {
        private var rowIndex: Int = -1
        private var pos: Int = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanListFilteredLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.plan_list_filtered_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        *//*{"PlanPosition":"1","ProfileCount":"3","PlanID":"5","PlanAmount":"14.99",
        "PlanCurrency":"Aus","PlanInterval":"Weekly","PlanImage":"",
        "PlanTenure":"1 Week","PlanNextRenewal":"17 May, 2021",
        "FreeTrial":"TRY 14 DAYS FOR FREE","SubName":"Week \/ Per 3 User","RecommendedFlag":"0","PlanFlag":"1"}*//*
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTilte.text = listModelList[position].planInterval
            holder.binding.tvContent.text = listModelList[position].subName
            holder.binding.tvAmount.text = "$" + listModelList[position].planAmount

            if (listModelList[position].recommendedFlag.equals("1", ignoreCase = true)) {
                holder.binding.rlMostPopular.visibility = View.VISIBLE
            } else {
                holder.binding.rlMostPopular.visibility = View.INVISIBLE
            }
            holder.binding.llPlanMain.setOnClickListener {
                rowIndex = position
                pos++
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                changeFunction(holder, listModelList, position)
            } else {
                if (listModelList[position].recommendedFlag.equals(
                        "1",
                        ignoreCase = true
                    ) && pos == 0
                ) {
                    holder.binding.rlMostPopular.visibility = View.VISIBLE
                    changeFunction(holder, listModelList, position)
                } else {
                    holder.binding.llPlanMain.background =
                        ContextCompat.getDrawable(ctx, R.drawable.light_gray_round_cornors)
                    holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    holder.binding.tvContent.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.black
                        )
                    )
                    holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                }
            }

        }

        private fun changeFunction(
            holder: PlanListAdapter.MyViewHolder,
            listModelList: List<PlanlistInappModel.ResponseData.Plan>,
            position: Int
        ) {
            holder.binding.llPlanMain.background =
                ContextCompat.getDrawable(ctx, R.drawable.light_sky_round_cornors)
            holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            val gson = Gson()
            i.putExtra("PlanData", gson.toJson(listModelList))
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            i.putExtra("Promocode", "")
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        *//*     override fun getFilter(): Filter {
                 return object : Filter() {
                     override fun performFiltering(charSequence: CharSequence): FilterResults {
                         val filterResults = FilterResults()
                         val charString = charSequence.toString()
                         if (charString.isEmpty()) {
                             listFilterData = listData
                         } else {
                             val filteredList: MutableList<PlanlistInappModel.ResponseData.Plan> =
                                 ArrayList()
                             for (row in listData) {
                                 if (row.profileCount!!.toLowerCase(Locale.getDefault())
                                         .contains(charString.toLowerCase(Locale.getDefault()))
                                 ) {
                                     filteredList.add(row)
                                 }
                             }
                             listFilterData = filteredList
                         }
                         filterResults.values = listFilterData
                         return filterResults
                     }

                     override fun publishResults(
                             charSequence: CharSequence,
                             filterResults: FilterResults
                     ) {
                         if (listFilterData.size == 0) {
     //                        binding.tvFound.setVisibility(View.VISIBLE)
     //                        binding.tvFound.setText("Couldn't find $searchFilter. Try searching again")
     //                        binding.rvCountryList.setVisibility(View.GONE)
                         } else {
     //                        binding.tvFound.setVisibility(View.GONE)
     //                        binding.rvCountryList.setVisibility(View.VISIBLE)
                             listFilterData =
                                 filterResults.values as List<PlanlistInappModel.ResponseData.Plan>
                             notifyDataSetChanged()
                         }
                     }
                 }
             }*//*

        inner class MyViewHolder(var binding: PlanListFilteredLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)
    }*/
}