package com.brainwellnessspa.areaOfFocusModule.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.AverageSleepTimeModel
import com.brainwellnessspa.databinding.ActivitySleepTimeBinding
import com.brainwellnessspa.databinding.SleepTimeRawBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SleepTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepTimeBinding
    lateinit var adapter: SleepTimeAdapter
    var sleepTime: String? = ""
    lateinit var activity: Activity
    var coUserId: String? = ""
    var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sleep_time)
        activity = this@SleepTimeActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        if (intent.extras != null) {
            sleepTime = intent.getStringExtra("SleepTime")
        }
        val p = Properties()
        BWSApplication.addToSegment("Sleep Time Screen Viewed", p, CONSTANTS.screen)

        binding.llBack.setOnClickListener {
            finish()
        }
        prepareUserData()
    }

    override fun onBackPressed() {
        finish()
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AverageSleepTimeModel> = APINewClient.client.averageSleepTimeLists
            listCall.enqueue(object : Callback<AverageSleepTimeModel> {
                override fun onResponse(call: Call<AverageSleepTimeModel>, response: Response<AverageSleepTimeModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AverageSleepTimeModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.rvTimeSlot.layoutManager = GridLayoutManager(activity, 2)
                            adapter = SleepTimeAdapter(listModel.responseData!!, activity, activity)
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
            holder.bindingAdapter.tvhours.text = listModel[position].name
            holder.bindingAdapter.llHourSlots.setOnClickListener {
                val shareddd = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                val editordd = shareddd.edit()
                editordd.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel[position].name)
                editordd.apply()
                val preferred = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                val edited = preferred.edit()
                edited.remove(CONSTANTS.selectedCategoriesTitle)
                edited.remove(CONSTANTS.selectedCategoriesName)
                edited.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME)
                edited.clear()
                edited.apply()
                val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel[position].name)
                editor.apply()
                val i = Intent(ctx, AreaOfFocusActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                i.putExtra("SleepTime", listModel[position].name)
                i.putExtra("BackClick", "0")
                ctx.startActivity(i)
                activity.finish()
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}