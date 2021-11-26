package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import android.util.Log
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.dashboardModule.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Callback
import retrofit2.Response

class SessionComparisonStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionComparisonStatusBinding
    lateinit var ctx: Context
    lateinit var act: Activity
    var sessionId: String? = ""
    var stepId: String? = ""
    var userId: String? = ""
    lateinit var listModel: BeforeAfterComparisionQuestionListModel
    lateinit var firstListAdapter: OptionsFirstListAdapter
    var myPos = 0
    val gson = Gson()
    var assAns = arrayListOf<String>()
    var assQus = arrayListOf<String>()
    var assQusString = arrayListOf<String>()
    lateinit var editor: SharedPreferences.Editor
    val sendAnsArray = arrayListOf<sendQusData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_comparison_status)
        act = this@SessionComparisonStatusActivity
        ctx = this@SessionComparisonStatusActivity

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
            stepId = intent.getStringExtra("StepId")
        }
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)
        getAssSaveData()
        binding.btnNext.setOnClickListener {
            getAssSaveData()
            if (myPos < listModel.responseData!!.questions!!.size - 1) {
                myPos += 1 //                binding.tvNumberOfQus.text = myPos.toString()
                binding.lpIndicator.progress = myPos
                binding.tvSection.text = (myPos+1).toString()
                if (myPos == listModel.responseData!!.questions!!.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnDone.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnDone.visibility = View.GONE
                }
                firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act)
                binding.rvFirstList.adapter = firstListAdapter
            }
            if (myPos > 1) {
                binding.btnPrev.visibility = View.VISIBLE
            } else {
                binding.btnPrev.visibility = View.GONE
            }
            Log.e("qus",gson.toJson(assQus))
            Log.e("ans",gson.toJson(assAns))
        }
        binding.btnPrev.setOnClickListener {
            callBack()
        }
        binding.btnDone.setOnClickListener{
            for (i in 0 until assQus.size) {
                val sendR = sendQusData()
                sendR.question_id = (assQus[i])
                sendR.answer = (assAns[i])
                sendAnsArray.add(sendR)
            }
            sendCategoryData(gson.toJson(sendAnsArray))
        }
        prepareData()
    }

    private fun sendCategoryData(sendAnsArray: String?) {
        if (isNetworkConnected(act)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getBeforeAndAfterAnswerSave(userId, sessionId, stepId, sendAnsArray)
            listCall.enqueue(object : Callback<BeforeAfterComparisionSaveStatusModel?> {
                override fun onResponse(call: Call<BeforeAfterComparisionSaveStatusModel?>, response: Response<BeforeAfterComparisionSaveStatusModel?>) {
                    try {
                        val listModel1 = response.body()
                        val response = listModel1?.responseData
                        if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                    val listCall = APINewClient.client.getSessionStepStatusList(userId,sessionId, stepId)
                                    listCall.enqueue(object : Callback<SessionStepStatusListModel?> {
                                        override fun onResponse(call: Call<SessionStepStatusListModel?>, response: Response<SessionStepStatusListModel?>) {
                                            try {
                                                val listModel = response.body()
                                                val response = listModel?.responseData
                                                if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                    act.finish()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }

                                        override fun onFailure(call: Call<SessionStepStatusListModel?>, t: Throwable) {
                                        }
                                    })

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BeforeAfterComparisionSaveStatusModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            showToast(act.getString(R.string.no_server_found), act)
        }
    }

    private fun getAssSaveData() {
        val shared = ctx.getSharedPreferences(CONSTANTS.sessionMain, Context.MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.sessionQus, gson.toString())
        val json3 = shared.getString(CONSTANTS.sessionAns, gson.toString())
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            assQus = gson.fromJson(json2, type1)
            assAns = gson.fromJson(json3, type1)
        }
    }
    private fun callBack() {
        if (myPos > 0) {
            myPos -= 1
            binding.lpIndicator.progress = myPos
            binding.tvSection.text = (myPos+1).toString()
            binding.btnNext.visibility = View.GONE
            binding.btnDone.visibility = View.VISIBLE
            if (myPos == listModel.responseData!!.questions!!.size - 1) {
                binding.btnNext.visibility = View.GONE
                binding.btnDone.visibility = View.VISIBLE
            } else {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnDone.visibility = View.GONE
            }
            firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act)
            binding.rvFirstList.adapter = firstListAdapter
        } else {
            //go to previous activity
        }
    }


    fun prepareData() {
        if (BWSApplication.isNetworkConnected(act)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getBeforeAfterQuestionListing(stepId, sessionId)
            listCall.enqueue(object : Callback<BeforeAfterComparisionQuestionListModel?> {
                override fun onResponse(call: Call<BeforeAfterComparisionQuestionListModel?>, response: Response<BeforeAfterComparisionQuestionListModel?>) {
                    try {
                        val listModel1 = response.body()
                        listModel = response.body()!!
                        val response = listModel1?.responseData
                        if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                binding.tvQus.text = response.questions!![0].sessionTitle
//                                if(assQus.size == 0){
//                                    for(i in 0..10){
//
//                                    }
//                                }
                                if (assQus.size != 0) {
                                    myPos = assQus.size - 1
                                    binding.lpIndicator.progress = myPos
                                }
                                binding.lpIndicator.progress = 0
                                binding.tvSection.text = (myPos+1).toString()
                                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                                    binding.btnNext.visibility = View.GONE
                                    binding.btnDone.visibility = View.VISIBLE
                                }
                                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act)
                                binding.rvFirstList.adapter = firstListAdapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BeforeAfterComparisionQuestionListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }

    class OptionsFirstListAdapter(private val listModel: List<BeforeAfterComparisionQuestionListModel.ResponseData.Question>?, private val myPos: Int, private val mypos2: Int, private val ctx: Context, var binding: ActivitySessionComparisonStatusBinding, val act: Activity) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        private var scsa = SessionComparisonStatusActivity()

        inner class MyViewHolder(var bindingAdapter: SessionComparisonQusLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        /* This is the first options box input layout create */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionComparisonQusLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_comparison_qus_layout, parent, false)
            return MyViewHolder(v)
        }

        /* This is the first options box set input layout */
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (myPos == 0) {
                binding.btnPrev.visibility = View.GONE
            } else {
                binding.btnPrev.visibility = View.VISIBLE
            }
            val shared = ctx.getSharedPreferences(CONSTANTS.sessionMain, Context.MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.sessionQus, scsa.gson.toString())
            val json3 = shared.getString(CONSTANTS.sessionAns, scsa.gson.toString())
            if (!json2.equals(scsa.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                scsa.assQus = scsa.gson.fromJson(json2, type1)
                scsa.assAns = scsa.gson.fromJson(json3, type1)
            }
            if (scsa.assQus.size != 0) {
                if (scsa.assQus.contains(listModel!![position].question)) {
                    for (i in 0 until scsa.assQus.size) {
                        if (scsa.assQus[i] == listModel[position].question) {
                            if (scsa.assAns[i].equals("Yes", ignoreCase = true)){
                                holder.bindingAdapter.cbYes1.isSelected = true
                                holder.bindingAdapter.cbNo1.isSelected = false
                                holder.bindingAdapter.cbYes1.isChecked = true
                                holder.bindingAdapter.cbNo1.isChecked = false
                            } else if (scsa.assAns[i].equals("No", ignoreCase = true)) {
                                holder.bindingAdapter.cbYes1.isSelected = false
                                holder.bindingAdapter.cbNo1.isSelected = true
                                holder.bindingAdapter.cbYes1.isChecked = false
                                holder.bindingAdapter.cbNo1.isChecked = true
                            }
                            break
                        }
                    }
                }else{
                    holder.bindingAdapter.cbYes1.isSelected = false
                    holder.bindingAdapter.cbNo1.isSelected = true
                    holder.bindingAdapter.cbYes1.isChecked = false
                    holder.bindingAdapter.cbNo1.isChecked = true
                }
            }else{
                holder.bindingAdapter.cbYes1.isSelected = false
                holder.bindingAdapter.cbNo1.isSelected = true
                holder.bindingAdapter.cbYes1.isChecked = false
                holder.bindingAdapter.cbNo1.isChecked = true
            }
            holder.bindingAdapter.tvQus.text = listModel!![position].question
            holder.bindingAdapter.tvSubDec.text = listModel[position].stepShortDescription
            holder.bindingAdapter.cbYes1.text = listModel[position].questionOptions!![0]
            holder.bindingAdapter.cbNo1.text = listModel[position].questionOptions!![1]

            holder.bindingAdapter.cbYes1.setOnClickListener {
                scsa.assQus.removeAt(position)
                scsa.assAns.removeAt(position)
                scsa.assQus.add(position, (position + 1).toString())
                scsa.assAns.add(position, "Yes")
                holder.bindingAdapter.cbYes1.isSelected = true
                holder.bindingAdapter.cbNo1.isSelected = false
                holder.bindingAdapter.cbYes1.isChecked = true
                holder.bindingAdapter.cbNo1.isChecked = false
                savedata()
            }
            holder.bindingAdapter.cbNo1.setOnClickListener {
                scsa.assQus.removeAt(position)
                scsa.assAns.removeAt(position)
                scsa.assQus.add(position, (position + 1).toString())
                scsa.assAns.add(position, "No")
                holder.bindingAdapter.cbYes1.isSelected = false
                holder.bindingAdapter.cbNo1.isSelected = true
                holder.bindingAdapter.cbYes1.isChecked = false
                holder.bindingAdapter.cbNo1.isChecked = true
                savedata()
            }

            if (holder.bindingAdapter.cbYes1.isChecked || holder.bindingAdapter.cbYes1.isSelected) {
                if (scsa.assQus.size == position) {
                    scsa.assQus.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "Yes")
                }else{
                    scsa.assQus.removeAt(position)
                    scsa.assAns.removeAt(position)
                    scsa.assQus.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "Yes")
                }
            } else if (holder.bindingAdapter.cbNo1.isChecked || holder.bindingAdapter.cbNo1.isSelected) {
                if (scsa.assQus.size == position) {
                    scsa.assAns.add(position, "No")
                }else{
                    scsa.assQus.removeAt(position)
                    scsa.assAns.removeAt(position)
                    scsa.assQus.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "No")
                }
                savedata()
            }
        }

        private fun savedata() {
            scsa.editor = ctx.getSharedPreferences(CONSTANTS.sessionMain, Context.MODE_PRIVATE).edit()
            scsa.editor.putString(CONSTANTS.sessionQus, scsa.gson.toJson(scsa.assQus)) //Friend
            scsa.editor.putString(CONSTANTS.sessionAns, scsa.gson.toJson(scsa.assAns)) //Friend
            scsa.editor.apply()
            scsa.editor.commit()
            val shared = ctx.getSharedPreferences(CONSTANTS.sessionMain, Context.MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.sessionQus, scsa.gson.toString())
            val json3 = shared.getString(CONSTANTS.sessionAns, scsa.gson.toString())
            if (!json2.equals(scsa.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                scsa.assQus = scsa.gson.fromJson(json2, type1)
                scsa.assAns = scsa.gson.fromJson(json3, type1)
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }
}