package com.brainwellnessspa.DassAssSliderTwo.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DassAssSliderTwo.Model.AssessmentQusModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.OptionsDataListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding
import com.brainwellnessspa.databinding.FormFillSubBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    lateinit var firstListAdapter: OptionsFirstListAdapter
    lateinit var secondListAdapter: OptionsSecondListAdapter
    lateinit var ctx: Context
    var passAnsIn: String = ""
    var passAns: String = ""
    var passQus: String = ""
    var assQus = arrayListOf<String>()
    var assAns= arrayListOf<String>()
    lateinit var listModel1: AssessmentQusModel
    lateinit var activity: Activity
    var myPos: Int = 0;
    var gson:Gson = Gson()
    lateinit var editor: SharedPreferences.Editor

    private val dataListModel = ArrayList<OptionsDataListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)
        ctx = this@DassAssSliderActivity
        activity = this@DassAssSliderActivity
        getAssSaveData()
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

        binding.btnNext.setOnClickListener {
            if (myPos < listModel1.responseData!!.questions!!.size - 1) {
                myPos += 2
                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), ctx)
                    binding.rvFirstList.adapter = firstListAdapter
                } else {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), ctx)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            } else {
                val i = Intent(ctx, AssProcessActivity::class.java)
                i.putExtra(CONSTANTS.ASSPROCESS, "1")
                startActivity(i)
            }
        }
        binding.btnPrev.setOnClickListener {
            if (myPos>1) {
                myPos -= 2
                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), ctx)
                    binding.rvFirstList.adapter = firstListAdapter
                } else {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), ctx)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            }
        }
        PrepareData()
    }

    fun getAssSaveData() {
        val shared = ctx.getSharedPreferences(CONSTANTS.DassMain, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.DassQus, gson.toString())
        val json3 = shared.getString(CONSTANTS.DassAns,  gson.toString())
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            assQus = gson.fromJson(json2, type1)
            assAns = gson.fromJson(json3, type1)
        }
    }

    fun PrepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AssessmentQusModel> = APINewClient.getClient().getAssessmentQus()
            listCall.enqueue(object : Callback<AssessmentQusModel> {
                override fun onResponse(call: Call<AssessmentQusModel>, response: Response<AssessmentQusModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AssessmentQusModel = response.body()!!
                        listModel1 = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            if (myPos < listModel.responseData!!.questions!!.size) {
//                                if(myPos ==)
                                firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 2), ctx)
                                binding.rvFirstList.adapter = firstListAdapter
                            }
                            BWSApplication.showToast(listModel.responseMessage, applicationContext)
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AssessmentQusModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }

    }


    class OptionsFirstListAdapter(private val listModel: List<AssessmentQusModel.ResponseData.Questions>?, private val ctx: Context) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillSubBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillSubBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_sub, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (listModel != null) {
                holder.bindingAdapter.tvSecond.text = listModel.get(position).question
                holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
                dass.secondListAdapter = OptionsSecondListAdapter(listModel.get(position), position, ctx)
                holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    class OptionsSecondListAdapter(val listModel: AssessmentQusModel.ResponseData.Questions, val pos: Int, val ctx: Context) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
        var mSelectedItem = -1
        var posItem: Int = -1

        var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.cbChecked.setOnClickListener {
                    setData()
                    mSelectedItem = adapterPosition
                    if(posItem!=-1)
                    notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem
                    if(dass.assQus.size == 0) {
                        dass.assQus.add(listModel.question.toString())
                        dass.assAns.add(0, adapterPosition.toString())

                    }else{
                        if(dass.assQus.contains(listModel.question)){
                            for(i in 0 until dass.assQus.size){
                                if(dass.assQus[i] == listModel.question){
                                    dass.assAns.removeAt(i)
                                    dass.assAns.add(i, adapterPosition.toString())
                                }
                            }
                        }else{
                            dass.assQus.add(pos, listModel.question.toString())
                            dass.assAns.add(pos, adapterPosition.toString())
                        }
                    }
                    Log.e("Qus", dass.assQus.toString())
                    Log.e("Ans", dass.assAns.toString())

                    dass.editor = ctx.getSharedPreferences(CONSTANTS.DassMain, MODE_PRIVATE).edit()
                    dass.editor.putString(CONSTANTS.DassQus, dass.gson.toJson(dass.assQus)) //Friend
                    dass.editor.putString(CONSTANTS.DassAns, dass.gson.toJson(dass.assAns)) //Friend
                    dass.editor.apply()
                    dass.editor.commit()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            if(dass.assQus.contains(listModel.question)){
                for(i in 0 until dass.assQus.size){
                    if(dass.assQus[i] == listModel.question){
                        posItem = Integer.parseInt(dass.assAns.get(i))
                        mSelectedItem = Integer.parseInt(dass.assAns.get(i))
                        break
                    }
                }
            }
            if (position == posItem) {
                holder.bindingAdapter.cbChecked.setChecked(position == posItem)
            } else {
                holder.bindingAdapter.cbChecked.setChecked(false)
            }
            holder.bindingAdapter.cbChecked.text = position.toString()
        }

        private fun setData() {
            val shared = ctx.getSharedPreferences(CONSTANTS.DassMain, MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.DassQus, dass.gson.toString())
            val json3 = shared.getString(CONSTANTS.DassAns,  dass.gson.toString())
            if (!json2.equals(dass.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                dass.assQus = dass.gson.fromJson(json2, type1)
                dass.assAns = dass.gson.fromJson(json3, type1)
            }
        }

        override fun getItemCount(): Int {
            val countx = listModel.answer!!.split("| ").toTypedArray()
            return Integer.parseInt(countx.get(countx.size - 1)) + 1
        }
    }
}