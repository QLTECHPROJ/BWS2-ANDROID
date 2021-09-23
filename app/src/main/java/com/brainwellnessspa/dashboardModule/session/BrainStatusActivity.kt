package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.BrainCatListModel
import com.brainwellnessspa.databinding.ActivityBrainStatusBinding
import com.brainwellnessspa.databinding.BrainFeelingStatusLayoutBinding
import com.brainwellnessspa.utility.APINewClient
import com.google.android.flexbox.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrainStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivityBrainStatusBinding
    lateinit var adapter: BrainFeelingStatusAdapter
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brain_status)
        activity = this@BrainStatusActivity
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvList.layoutManager = layoutManager
        prepareData()
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.braincatLists
            listCall.enqueue(object : Callback<BrainCatListModel?> {
                override fun onResponse(call: Call<BrainCatListModel?>, response: Response<BrainCatListModel?>) {
                    try {
                        val listModel = response.body()
                        val responsedb = listModel?.responseData
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (responsedb != null) {
                                adapter = BrainFeelingStatusAdapter(binding, activity, responsedb.data)
                            }
                            binding.rvList.adapter = adapter
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BrainCatListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class BrainFeelingStatusAdapter(var binding: ActivityBrainStatusBinding, var activity: Activity, var catName: List<BrainCatListModel.ResponseData.Data>?) : RecyclerView.Adapter<BrainFeelingStatusAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: BrainFeelingStatusLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BrainFeelingStatusLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.brain_feeling_status_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val db = catName?.get(position)
            if (db != null) {
                holder.bindingAdapter.tvText.text = db.name

                holder.bindingAdapter.llCategory.setOnClickListener {
                    if (db.catFlag.equals("0", ignoreCase = true)) {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                    } else if (db.catFlag.equals("1", ignoreCase = true)) {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return catName!!.size
        }
    }
}