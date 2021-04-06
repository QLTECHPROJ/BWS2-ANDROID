package com.brainwellnessspa.DassAssSliderTwo.Activity

import android.app.Activity
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
    var passAnsIn: String = ""
    var passAns: String = ""
    var passQus: String = ""
    lateinit var listModel1: AssessmentQusModel
    lateinit var activity: Activity
    var myPos: Int = 0;
    private val dataListModel = ArrayList<OptionsDataListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)
        ctx = this@DassAssSliderActivity
        activity = this@DassAssSliderActivity
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)
        binding.btnNext.setOnClickListener {
//            val i = Intent(ctx, AssProcessActivity::class.java)
//            i.putExtra(CONSTANTS.ASSPROCESS, "1")
//            startActivity(i)
            if (myPos < listModel1.responseData!!.questions!!.size - 1) {
//                passQus=passQus+","+pos.toString()
//                passAns=passAns+","+pos.toString()
//                Log.e("qus ", passQus)
//                Log.e("qus ", passAns)
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
        PrepareData()
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
        var posItem: Int = 0

        var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.cbChecked.setOnClickListener {
                    mSelectedItem = adapterPosition
                    notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem

                    BWSApplication.showToast("position :-$mSelectedItem", ctx)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (position == posItem) {
                holder.bindingAdapter.cbChecked.setChecked(position == posItem)
            } else {
                holder.bindingAdapter.cbChecked.setChecked(false)
            }
            holder.bindingAdapter.cbChecked.text = position.toString()

            /*  holder.bindingAdapter.cbChecked.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
                  BWSApplication.showToast("position :-$position", ctx)
                  val previousItem = mSelectedItem
                  mSelectedItem = position
                  notifyItemChanged(previousItem)
                  notifyItemChanged(position)
              }*/
            /* holder.bindingAdapter.cbChecked.setOnClickListener { view ->
                 BWSApplication.showToast("position :-$position", ctx)
                 val previousItem = mSelectedItem
                 mSelectedItem = position
                 notifyItemChanged(previousItem)
                 notifyItemChanged(position)
             }*/
            /* if (mSelectedItem == position) {
                 holder.bindingAdapter.cbChecked.isSelected = true
                 dass.passAnsIn = position.toString()
                 Log.e("ans ", position.toString())
             }else{
                 holder.bindingAdapter.cbChecked.isSelected = false
             }*/
        }

        override fun getItemCount(): Int {
            val countx = listModel.answer!!.split("| ").toTypedArray()
            return Integer.parseInt(countx.get(countx.size - 1)) + 1
        }
    }
}