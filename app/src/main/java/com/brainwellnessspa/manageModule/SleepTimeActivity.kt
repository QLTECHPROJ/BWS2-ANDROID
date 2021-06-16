package com.brainwellnessspa.manageModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.dashboardModule.models.AverageSleepTimeModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySleepTimeBinding
import com.brainwellnessspa.databinding.SleepTimeRawBinding
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SleepTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepTimeBinding
    lateinit var adapter: SleepTimeAdapter
    lateinit var ctx: Context
    var SleepTime: String? = null
    lateinit var activity: Activity
    var CoUserID: String? = ""
    var USERID: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sleep_time)
        ctx = this@SleepTimeActivity
        activity = this@SleepTimeActivity
        val shared = ctx.getSharedPreferences(
            CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
            AppCompatActivity.MODE_PRIVATE
        )
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        if (intent.extras != null) {
            SleepTime = intent.getStringExtra("SleepTime")
        }
        val p = Properties()
        p.putValue("coUserId", CoUserID)
        BWSApplication.addToSegment("Sleep Time Screen Viewed", p, CONSTANTS.screen)
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
                val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.get(position).name)
                editor.commit()
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