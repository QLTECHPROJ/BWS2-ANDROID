package com.brainwellnessspa.DassAssSliderTwo.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DassAssSliderTwo.Model.AssessmentQusModel
import com.brainwellnessspa.DassAssSliderTwo.Model.PostAssAns
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.AssessmentSaveDataModel
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
    var assQus = arrayListOf<String>()
    var assAns = arrayListOf<String>()
    var assSort = arrayListOf<String>()
    private var postAssAns = arrayListOf<PostAssAns>()
    lateinit var listModel1: AssessmentQusModel
    lateinit var activity: Activity
    var myPos: Int = 0
    var USERID: String? = ""
    var CoUserID: String? = ""
    var EMAIL: String? = ""
    var gson: Gson = Gson()
    lateinit var editor: SharedPreferences.Editor
    private val dataListModel = ArrayList<OptionsDataListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        EMAIL = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        ctx = this@DassAssSliderActivity
        activity = this@DassAssSliderActivity
        getAssSaveData()
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

        binding.btnNext.setOnClickListener {
            getAssSaveData()
            if (myPos < listModel1.responseData!!.questions!!.size - 1) {
                myPos += 2
//                binding.tvNumberOfQus.text = myPos.toString()
                binding.lpIndicator.progress = myPos
                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, ctx, binding, activity)
                    binding.rvFirstList.adapter = firstListAdapter
                } else {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, ctx, binding, activity)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            } else {
                /*    for(i in listModel1.responseData!!.questions!!.indices){
                        val ps = PostAssAns()
                        ps.qus = assQus[i]
                        ps.ans = assAns[i]
                        ps.qusOrder = assSort[i]
                        postAssAns.add(ps)
                    }
                    Log.e("Ass Post Data", gson.toJson(postAssAns))*/

//                binding.tvNumberOfQus.text =listModel1.responseData!!.questions!!.size.toString()
                binding.lpIndicator.progress = listModel1.responseData!!.questions!!.size
                sendAssessmentData()
                Log.e("Ass Post Data", gson.toJson(assAns))
            }
            if (myPos > 2) {
                binding.btnPrev.visibility = View.VISIBLE
            } else {
                binding.btnPrev.visibility = View.GONE
            }
        }
        binding.btnPrev.setOnClickListener {
            callBack()
        }
        prepareData()
    }

    override fun onBackPressed() {
        callBack()
    }

    private fun callBack() {
        if (myPos > 1) {
            myPos -= 2

            binding.lpIndicator.progress = myPos
//            binding.tvNumberOfQus.text = myPos.toString()
            if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, ctx, binding, activity)
                binding.rvFirstList.adapter = firstListAdapter
            } else {
                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, ctx, binding, activity)
                binding.rvFirstList.adapter = firstListAdapter
            }
        }
    }

    private fun getAssSaveData() {
        val shared = ctx.getSharedPreferences(CONSTANTS.AssMain, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.AssQus, gson.toString())
        val json3 = shared.getString(CONSTANTS.AssAns, gson.toString())
        val json4 = shared.getString(CONSTANTS.AssSort, gson.toString())
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            assQus = gson.fromJson(json2, type1)
            assAns = gson.fromJson(json3, type1)
            assSort = gson.fromJson(json4, type1)
        }
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AssessmentQusModel> = APINewClient.getClient().assessmentQus
            listCall.enqueue(object : Callback<AssessmentQusModel> {
                override fun onResponse(call: Call<AssessmentQusModel>, response: Response<AssessmentQusModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AssessmentQusModel = response.body()!!
                        listModel1 = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            var condition: String? = ""
                            for (i in 0 until listModel.responseData!!.content?.size!!) {
                                condition += listModel.responseData!!.content!![i].condition + "\n"
                            }
                            binding.tvText.text = condition
                            binding.lpIndicator.max =listModel.responseData!!.questions!!.size
                            binding.lpIndicator.progress =0
//                            binding.tvNumberOfQus.text = myPos.toString()
//                            binding.tvTotalQus.text = listModel.responseData!!.questions!!.size.toString()
                            if (myPos < listModel.responseData!!.questions!!.size) {
//                                if(myPos ==)
                                firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, ctx, binding, activity)
                                binding.rvFirstList.adapter = firstListAdapter
                            }
                            BWSApplication.showToast(listModel.responseMessage, activity)
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, activity)
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


    class OptionsFirstListAdapter(private val listModel: List<AssessmentQusModel.ResponseData.Questions>?, val myPos: Int, private val ctx: Context, var binding: ActivityDassAssSliderBinding, val activity: Activity) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        private var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillSubBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillSubBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_sub, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (myPos == 0) {
                binding.btnPrev.visibility = View.GONE
            } else {
                binding.btnPrev.visibility = View.VISIBLE
            }
            if (listModel != null) {
                holder.bindingAdapter.tvSecond.text = listModel[position].question
                holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
                if (position == 0) {
                    dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, ctx, binding, activity)
                } else {
                    dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos + 1, ctx, binding, activity)
                }
                holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    class OptionsSecondListAdapter(val listModel: AssessmentQusModel.ResponseData.Questions, val pos: Int, val ctx: Context, var binding: ActivityDassAssSliderBinding, val activity: Activity) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
        var mSelectedItem = -1
        var posItem: Int = -1

        var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.cbChecked.setOnClickListener {
                    setData()
                    mSelectedItem = adapterPosition
                    if (posItem != -1)
                        notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem
                    if (dass.assQus.size == 0) {
                        dass.assQus.add(listModel.question.toString())
                        dass.assAns.add(0, adapterPosition.toString())
                        dass.assSort.add(0, pos.toString())

                    } else {
                        if (dass.assQus.contains(listModel.question)) {
                            for (i in 0 until dass.assQus.size) {
                                if (dass.assQus[i] == listModel.question) {
                                    dass.assAns.removeAt(i)
                                    dass.assSort.removeAt(i)
                                    dass.assAns.add(i, adapterPosition.toString())
                                    dass.assSort.add(i, pos.toString())
                                }
                            }
                        } else {
                            if (pos > dass.assQus.size) {
                                dass.assQus.add(pos - 1, listModel.question.toString())
                                dass.assAns.add(pos - 1, adapterPosition.toString())
                                dass.assSort.add(pos - 1, pos.toString())
                            } else {
                                dass.assQus.add(pos, listModel.question.toString())
                                dass.assAns.add(pos, adapterPosition.toString())
                                dass.assSort.add(pos, pos.toString())
                            }
                        }
                    }
                    Log.e("Qus", dass.assQus.toString())
                    Log.e("Ans", dass.assAns.toString())
                    Log.e("Sort Pos", dass.assSort.toString())
                    visibleGoneNext()
                    dass.editor = ctx.getSharedPreferences(CONSTANTS.AssMain, MODE_PRIVATE).edit()
                    dass.editor.putString(CONSTANTS.AssQus, dass.gson.toJson(dass.assQus)) //Friend
                    dass.editor.putString(CONSTANTS.AssAns, dass.gson.toJson(dass.assAns)) //Friend
                    dass.editor.putString(CONSTANTS.AssSort, dass.gson.toJson(dass.assSort)) //Friend
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
            if (dass.assQus.contains(listModel.question)) {
                for (i in 0 until dass.assQus.size) {
                    if (dass.assQus[i] == listModel.question) {
                        posItem = Integer.parseInt(dass.assAns.get(i))
                        mSelectedItem = posItem
                        break
                    }
                }
            }
            if (position == posItem) {
                holder.bindingAdapter.cbChecked.isChecked = position == posItem
            } else {
                holder.bindingAdapter.cbChecked.isChecked = false
            }
            holder.bindingAdapter.cbChecked.text = position.toString()
        }

        private fun setData() {
            val shared = ctx.getSharedPreferences(CONSTANTS.AssMain, MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.AssQus, dass.gson.toString())
            val json3 = shared.getString(CONSTANTS.AssAns, dass.gson.toString())
            val json4 = shared.getString(CONSTANTS.AssSort, dass.gson.toString())
            if (!json2.equals(dass.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                dass.assQus = dass.gson.fromJson(json2, type1)
                dass.assAns = dass.gson.fromJson(json3, type1)
                dass.assSort = dass.gson.fromJson(json4, type1)
            }
            visibleGoneNext()
        }

        private fun visibleGoneNext() {
            if (dass.assQus.size >= pos + 1) {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            } else {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            }
        }


        override fun getItemCount(): Int {
            val countx = listModel.answer!!.split("| ").toTypedArray()
            return Integer.parseInt(countx[countx.size - 1]) + 1
        }
    }

    private fun sendAssessmentData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AssessmentSaveDataModel> = APINewClient.getClient().getAssessmentSaveData(USERID, CoUserID, gson.toJson(assAns).toString())
            listCall.enqueue(object : Callback<AssessmentSaveDataModel> {
                override fun onResponse(call: Call<AssessmentSaveDataModel>, response: Response<AssessmentSaveDataModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AssessmentSaveDataModel = response.body()!!
                        if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            val i = Intent(activity, AssProcessActivity::class.java)
                            i.putExtra(CONSTANTS.ASSPROCESS, "1")
                            i.putExtra(CONSTANTS.IndexScore, listModel.getResponseData()?.indexScore)
                            startActivity(i)
                            finish()
                        } else {
                            BWSApplication.showToast(listModel.getResponseMessage(), activity)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AssessmentSaveDataModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }
}