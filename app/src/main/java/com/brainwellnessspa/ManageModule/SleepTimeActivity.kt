package com.brainwellnessspa.ManageModule

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.AverageSleepTimeModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPasswordModel
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.databinding.ActivitySleepTimeBinding
import com.brainwellnessspa.databinding.SleepTimeRawBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SleepTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepTimeBinding
    lateinit var adapter: SleepTimeAdapter
    lateinit var ctx: Context
    var SleepTime: String? = null
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sleep_time)
        ctx = this@SleepTimeActivity
        activity = this@SleepTimeActivity
        if (intent.extras != null) {
            SleepTime = intent.getStringExtra("SleepTime")
        }
        prepareUserData()
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AverageSleepTimeModel> = APINewClient.getClient().getAverageSleepTimeLists()
            listCall.enqueue(object : Callback<AverageSleepTimeModel> {
                override fun onResponse(call: Call<AverageSleepTimeModel>, response: Response<AverageSleepTimeModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AverageSleepTimeModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.rvTimeSlot.layoutManager = GridLayoutManager(ctx, 2)
                            adapter = SleepTimeAdapter(listModel.responseData!!, ctx, activity)
                            binding.rvTimeSlot.adapter = adapter
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AverageSleepTimeModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class SleepTimeAdapter(private val listModel: List<AverageSleepTimeModel.ResponseData>, var ctx: Context, var activity: Activity) : RecyclerView.Adapter<SleepTimeAdapter.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: SleepTimeRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SleepTimeRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.sleep_time_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvhours.text = listModel.get(position).name
            holder.bindingAdapter.llHourSlots.setOnClickListener {
                val i = Intent(ctx, RecommendedCategoryActivity::class.java)
                i.putExtra("SleepTime", listModel.get(position).name)
                i.putExtra("BackClick","0")
                ctx.startActivity(i)
                activity.finish()
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}