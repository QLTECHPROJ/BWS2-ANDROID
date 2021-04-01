package com.brainwellnessspa.DassAssSliderTwo.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DassAssSliderTwo.Model.AssessmentQusModel
import com.brainwellnessspa.DassAssSliderTwo.Model.ResponseData
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.OptionsDataListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding
import com.brainwellnessspa.databinding.FormFillSubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    lateinit var firstListAdapter: OptionsFirstListAdapter
    lateinit var secondListAdapter: OptionsSecondListAdapter
    lateinit var ctx: Context
    private val dataListModel = ArrayList<OptionsDataListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)
        ctx = this@DassAssSliderActivity
        binding.btnDone.setOnClickListener {
            val i = Intent(this@DassAssSliderActivity, AssProcessActivity::class.java)
            i.putExtra(CONSTANTS.ASSPROCESS, "1")
            startActivity(i)
        }
        PrepareData()
    }

    fun PrepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@DassAssSliderActivity)
            val listCall: Call<AssessmentQusModel> = APINewClient.getClient().getAssessmentQus()
            listCall.enqueue(object : Callback<AssessmentQusModel> {
                override fun onResponse(call: Call<AssessmentQusModel>, response: Response<AssessmentQusModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@DassAssSliderActivity)
                        val listModel: AssessmentQusModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.rvFirstList.layoutManager = LinearLayoutManager(this@DassAssSliderActivity)
                            firstListAdapter = OptionsFirstListAdapter(listModel.responseData,ctx)
                            binding.rvFirstList.adapter = firstListAdapter
                            BWSApplication.showToast(listModel.responseMessage, applicationContext)
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AssessmentQusModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@DassAssSliderActivity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }

    }


    class OptionsFirstListAdapter(private val listModel: List<ResponseData>?, private val ctx: Context) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        lateinit var secondListAdapter: OptionsSecondListAdapter
        inner class MyViewHolder(var bindingAdapter: FormFillSubBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillSubBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_sub, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvSecond.text = listModel!![position].question
            holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
            secondListAdapter = OptionsSecondListAdapter(listModel[position])
            holder.bindingAdapter.rvSecondList.adapter = secondListAdapter
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    class OptionsSecondListAdapter(private val listModel: ResponseData) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.cbChecked.text = position.toString()
        }

        override fun getItemCount(): Int {
            val countx = listModel.answer!!.split("| ").toTypedArray()
            return Integer.parseInt(countx.get(countx.size-1))
        }
    }
}