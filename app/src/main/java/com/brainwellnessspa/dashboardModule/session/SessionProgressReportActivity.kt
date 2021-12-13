package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.ActivitySessionProgressReportBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding
import com.brainwellnessspa.databinding.FormFillSubBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/* This act is assessment form act */
class  SessionProgressReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionProgressReportBinding
    lateinit var firstListAdapter: OptionsFirstListAdapter
    lateinit var secondListAdapter: OptionsSecondListAdapter
    lateinit var ctx: Context
    var navigation: String = ""
    var progressReportQus = arrayListOf<String>()
    var progressReportAns = arrayListOf<String>()
    lateinit var act: Activity
    var myPos: Int = 0
    private var doubleBackToExitPressedOnce = false
    var mainAccountID: String? = ""
    var userId: String? = ""
    var email: String? = ""
    var listModel = StepTypeTwoSaveDataModel.ResponseData()
    var sessionId: String? = ""
    var stepId:String? = ""
    var nextForm:String? = ""
    var gson: Gson = Gson()
    var mod = 0
    val sendAnsArray = arrayListOf<sendQusData>()
    lateinit var editor: SharedPreferences.Editor

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_progress_report)

        /* This is the get string mainAccountID, UserId & email */
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        email = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        ctx = this@SessionProgressReportActivity
        act = this@SessionProgressReportActivity
        getAssSaveData()
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

        binding.llBack.setOnClickListener {
            finish()
        }

        if (intent.extras != null) {
            nextForm = intent.getStringExtra("nextForm").toString()
            sessionId = intent.getStringExtra("SessionId").toString()
            stepId = intent.getStringExtra("StepId").toString()
            val json = intent.getStringExtra("Data").toString()
            val type1 = object : TypeToken<StepTypeTwoSaveDataModel.ResponseData>() {}.type
            listModel = gson.fromJson(json, type1)
        }
        mod = listModel.questions!!.size % 2

        /* This is the next button click */
        binding.btnNext.setOnClickListener {
            getAssSaveData()
            if (myPos < listModel.questions!!.size - 1) {
                myPos += Integer.parseInt(listModel.chunkSize!!)

                binding.lpIndicator.progress = myPos
              /*  if (myPos == listModel.questions!!.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                }*/ /*6 000 akta
                5 000 pote rakhya
                15 masi ne aapya */
                var s = myPos + Integer.parseInt(listModel.chunkSize!!)
                if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                } else if (s > listModel.questions!!.size) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                }else if(myPos < listModel.questions!!.size) {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                }
                /*if (mod == 0) {
                    if (myPos < listModel.questions!!.size) {
                        firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + 2, ctx, binding, act, listModel)
                        binding.rvFirstList.adapter = firstListAdapter
                    }
                } else {
                    if (myPos == listModel.questions!!.size - 1) {
                        firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act, listModel)
                        binding.rvFirstList.adapter = firstListAdapter
                    } else {
                        firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + 2, ctx, binding, act, listModel)
                        binding.rvFirstList.adapter = firstListAdapter
                    }
                }*/
            }
            if (myPos > Integer.parseInt(listModel.chunkSize!!)) {
                binding.btnPrev.visibility = View.VISIBLE
            } else {
                binding.btnPrev.visibility = View.GONE
            }
            Log.e("Ass Post Data", gson.toJson(progressReportAns))
        }

        /* This is the previous button click */
        binding.btnPrev.setOnClickListener {
            callBack()
        }

        /* This is the continue button click when form is complete */
        binding.btnContinue.setOnClickListener {
            binding.lpIndicator.progress = listModel.questions!!.size
            for (i in 0 until progressReportQus.size) {
                val sendR = sendQusData()
                sendR.question_id = (progressReportQus[i])
                sendR.answer = (progressReportAns[i])
                sendAnsArray.add(sendR)
            }
             callSaveProgressReport(gson.toJson(sendAnsArray))
            Log.e("Ass Post Data", gson.toJson(sendAnsArray))
        }
        prepareData()
    }
    private fun callSaveProgressReport(answerJson: String) {
        if (BWSApplication.isNetworkConnected(act)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getSaveProgressReport(userId, sessionId, stepId, nextForm, answerJson)
            listCall.enqueue(object : Callback<SaveProgressReportModel?> {
                override fun onResponse(call: Call<SaveProgressReportModel?>, response: Response<SaveProgressReportModel?>) {
                    try {
                        val listModel1 = response.body()
                        if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            Log.e("sussess", "true")
                            val preferencesd1: SharedPreferences = getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
                            val edited1 = preferencesd1.edit()
                            edited1.remove(CONSTANTS.ProgressReportQus)
                            edited1.remove(CONSTANTS.ProgressReportAns)
                            edited1.clear()
                            edited1.apply()
                            callCheckProgressReport()
                        } else if (listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                            BWSApplication.callDelete403(act, listModel1.responseMessage)
                        } else {
                            BWSApplication.showToast(listModel1.responseMessage, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SaveProgressReportModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }
    fun callCheckProgressReport() {
        if (BWSApplication.isNetworkConnected(act)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getCheckProgressReportStatus(userId, sessionId, stepId)
            listCall.enqueue(object : Callback<CheckProgressReportStatusModel?> {
                override fun onResponse(call: Call<CheckProgressReportStatusModel?>, response: Response<CheckProgressReportStatusModel?>) {
                    //                    try {
                    Log.e("sussess chk", "true")
                    val listModel1 = response.body()
                    val response = listModel1?.responseData
                    if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                        if (response != null) {
                            val listCall = APINewClient.client.getSessionProgressReport(sessionId, stepId, response.nextForm)
                            listCall.enqueue(object : Callback<StepTypeTwoSaveDataModel?> {
                                override fun onResponse(call: Call<StepTypeTwoSaveDataModel?>, response2: Response<StepTypeTwoSaveDataModel?>) {
                                    try {
                                        val listModel2 = response2.body()
                                        val response1 = listModel2?.responseData
                                        Log.e("sussess chk x x", "true")

                                        if (listModel2?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                                            if (response1 != null) {
                                                val gson = Gson()
                                                val i = Intent(act, SessionWalkScreenActivity::class.java)
                                                i.putExtra("Data", gson.toJson(response1))
                                                i.putExtra("nextForm", response.nextForm)
                                                i.putExtra("SessionId", sessionId)
                                                i.putExtra("StepId", stepId)
                                                act.startActivity(i)
                                                act.finish()
                                            }
                                        } else if (listModel2!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                                            BWSApplication.callDelete403(act, listModel2.responseMessage)
                                        } else {
                                            BWSApplication.showToast(listModel2.responseMessage, act)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<StepTypeTwoSaveDataModel?>, t: Throwable) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                                }
                            })
                        }
                    } else if (listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                        BWSApplication.callDelete403(act, listModel1.responseMessage)
                    } else {
                        BWSApplication.showToast(listModel1.responseMessage, act)
                    }
                    /*} catch (e: Exception) {
                        e.printStackTrace()
                    }*/
                }

                override fun onFailure(call: Call<CheckProgressReportStatusModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }

    private fun addInSegment(p: Properties) {
        BWSApplication.addToSegment(CONSTANTS.Assessment_Screen_Viewed, p, CONSTANTS.screen)
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        callBack()
    }

    /* This is the back click event function */
    private fun callBack() {
        if (myPos > 1) {
            myPos -= Integer.parseInt(listModel.chunkSize!!)
            val p = Properties()
            p.putValue("screen", myPos)
//            addInSegment(p)
            binding.lpIndicator.progress = myPos
            var s = myPos + Integer.parseInt(listModel.chunkSize!!)
            if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
                firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
                binding.rvFirstList.adapter = firstListAdapter
            } else if (s > listModel.questions!!.size) {
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
                firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
                binding.rvFirstList.adapter = firstListAdapter
            }else if(myPos < listModel.questions!!.size) {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
                binding.rvFirstList.adapter = firstListAdapter
            }
            /*if (mod == 0) {
                if (myPos < listModel.questions!!.size) {
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + 2, ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            } else {
                if (myPos == listModel.questions!!.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + 2, ctx, binding, act, listModel)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            }*/
        } else {
            finish()
        }
    }

    /* This function is save assessment result */
    private fun getAssSaveData() {
        val shared = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.ProgressReportQus, gson.toString())
        val json3 = shared.getString(CONSTANTS.ProgressReportAns, gson.toString())
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            progressReportQus = gson.fromJson(json2, type1)
            progressReportAns = gson.fromJson(json3, type1)
        }
    }

    /* This function is get assessment questions */
    private fun prepareData() {
        binding.tvQus.text = listModel.questionTitle
        binding.tvText1.text = listModel.questionDescription
        binding.lpIndicator.max = listModel.questions!!.size
        binding.lpIndicator.progress = 0

        binding.tvTitle.text = listModel.sectionSubtitle
        binding.tvNumberOfQus.text =  listModel.currentSection
        binding.tvTotalQus.text = listModel.totalSection
        /*if (progressReportQus.size != 0) {
            val mod1 = progressReportQus.size % 2
            myPos = if (mod1 == 0) {
                progressReportQus.size
            } else {
                progressReportQus.size - 1
            }
            binding.lpIndicator.progress = myPos
            Log.e("My Pos...", myPos.toString() + "MOD..." + mod.toString() + "Ass Size..." + progressReportQus.size.toString())
        }*/
//        if(mod == 0){

        /*if(listModel.optionType.equals("fiveoptions")) {
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER_HORIZONTAL
            binding.rvFirstList.layoutParams = params
        }else {
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.START
            binding.rvFirstList.layoutParams = params
        }*/

        var s = myPos + Integer.parseInt(listModel.chunkSize!!)
        if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
            binding.btnNext.visibility = View.GONE
            binding.btnContinue.visibility = View.VISIBLE
            firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
            binding.rvFirstList.adapter = firstListAdapter
        } else if (s > listModel.questions!!.size) {
            binding.btnNext.visibility = View.GONE
            binding.btnContinue.visibility = View.VISIBLE
            firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
            binding.rvFirstList.adapter = firstListAdapter
        }else if(myPos < listModel.questions!!.size) {
            binding.btnNext.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.GONE
            firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
            binding.rvFirstList.adapter = firstListAdapter
        }
//        }else {
//            if (myPos == listModel.questions!!.size - 1) {
//                binding.btnNext.visibility = View.GONE
//                binding.btnContinue.visibility = View.VISIBLE
//                firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, act, listModel)
//                binding.rvFirstList.adapter = firstListAdapter
//            } else if (myPos < listModel.questions!!.size) {
//                firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos, myPos + 2, ctx, binding, act, listModel)
//                binding.rvFirstList.adapter = firstListAdapter
//            }
//        }
    }

    /* This is the first options box input layout */
    class OptionsFirstListAdapter(private val listModel: List<StepTypeTwoSaveDataModel.ResponseData.Question>?, private val myPos: Int, private val mypos2: Int, private val ctx: Context, var binding: ActivitySessionProgressReportBinding, val act: Activity, val listModelMain: StepTypeTwoSaveDataModel.ResponseData) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        private var dass = SessionProgressReportActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillSubBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        /* This is the first options box input layout create */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillSubBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_sub, parent, false)
            return MyViewHolder(v)
        }

        /* This is the first options box set input layout */
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (myPos == 0) {
                binding.btnPrev.visibility = View.GONE
            } else {
                binding.btnPrev.visibility = View.VISIBLE
            }
            if (listModel != null) {
                holder.bindingAdapter.tvSecond.text = listModel[position].question
                if(listModelMain.optionType.equals("tenoptions")) {
                    holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
                    holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
                }else if(listModelMain.optionType.equals("fiveoptions")){
                    holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, listModel[position].questionOptions!!.size)
                    val layoutManager = GridLayoutManager(ctx, listModel[position].questionOptions!!.size)
                    val totalSize: Int = listModel[position].questionOptions!!.size
                    holder.bindingAdapter.rvSecondList.setHasFixedSize(false)
//                    holder.bindingAdapter.rvSecondList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.light_white))
                }else if(listModelMain.optionType.equals("twooptions")){
                    holder.bindingAdapter.rvSecondList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
                }
//                if (position == 0) {
                dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, mypos2, ctx, binding, act, listModelMain)
//                } else {
//                    dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos + 1, mypos2, ctx, binding, act, listModelMain)
//                }
                holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    /* This is the second options box input layout */
    class OptionsSecondListAdapter(val listModel: StepTypeTwoSaveDataModel.ResponseData.Question, val pos: Int, private val mmypos2: Int, val ctx: Context, var binding: ActivitySessionProgressReportBinding, val act: Activity, val listModelMain: StepTypeTwoSaveDataModel.ResponseData) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
        var mSelectedItem = -1
        var posItem: Int = -1

        var dass = SessionProgressReportActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.cbChecked.setOnClickListener {
                    callCheckedBox(absoluteAdapterPosition)
                }
                bindingAdapter.cbChecked2.setOnClickListener {
                    callCheckedBox(absoluteAdapterPosition)
                }
                bindingAdapter.rbOne.setOnClickListener {
                    callCheckedBox(absoluteAdapterPosition)
                }
            }
        }

        private fun callCheckedBox(absoluteAdapterPosition: Int) {
            setData()
            mSelectedItem = absoluteAdapterPosition
            if (posItem != -1) notifyItemChanged(posItem)
            notifyItemChanged(mSelectedItem)
            posItem = mSelectedItem
            if (dass.progressReportQus.size == 0) {
                dass.progressReportQus.add(listModel.questionId.toString())
                dass.progressReportAns.add(0, absoluteAdapterPosition.toString())

            } else {
                if (dass.progressReportQus.contains(listModel.questionId)) {
                    for (i in 0 until dass.progressReportQus.size) {
                        if (dass.progressReportQus[i] == listModel.questionId) {
                            dass.progressReportAns.removeAt(i)
                            dass.progressReportAns.add(i, absoluteAdapterPosition.toString())
                        }
                    }
                } else {
                    if (pos > dass.progressReportQus.size) {
                        dass.progressReportQus.add(pos - 1, listModel.questionId.toString())
                        dass.progressReportAns.add(pos - 1, absoluteAdapterPosition.toString())
                    } else {
                        dass.progressReportQus.add(pos, listModel.questionId.toString())
                        dass.progressReportAns.add(pos, absoluteAdapterPosition.toString())
                    }
                }
            }

            Log.e("Qus", dass.progressReportQus.toString())
            Log.e("Ans", dass.progressReportAns.toString())
            visibleGoneNext()
            dass.editor = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE).edit()
            dass.editor.putString(CONSTANTS.ProgressReportQus, dass.gson.toJson(dass.progressReportQus))
            dass.editor.putString(CONSTANTS.ProgressReportAns, dass.gson.toJson(dass.progressReportAns))
            dass.editor.apply()
            dass.editor.commit()
        }

        /* This is the second options box input layout create */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        /* This is the second options set box input layout */
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            when {
                listModelMain.optionType.equals("tenoptions") -> {
                    holder.bindingAdapter.cbChecked2.visibility = View.VISIBLE
                    holder.bindingAdapter.cbChecked.visibility = View.GONE
                    holder.bindingAdapter.tvOne.visibility = View.GONE
                    holder.bindingAdapter.llMainLayout.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
                    holder.bindingAdapter.rbOne.visibility = View.GONE
                }
                listModelMain.optionType.equals("fiveoptions") -> {
                    holder.bindingAdapter.cbChecked.visibility = View.GONE
                    holder.bindingAdapter.cbChecked2.visibility = View.GONE
                    holder.bindingAdapter.tvOne.visibility = View.VISIBLE
                    holder.bindingAdapter.rbOne.visibility = View.VISIBLE
                    holder.bindingAdapter.llMainLayout.setBackgroundColor(ContextCompat.getColor(act, R.color.light_white))
                }
                listModelMain.optionType.equals("twooptions") -> {
                    holder.bindingAdapter.cbChecked2.visibility = View.VISIBLE
                    holder.bindingAdapter.cbChecked.visibility = View.GONE
                    holder.bindingAdapter.tvOne.visibility = View.GONE
                    holder.bindingAdapter.rbOne.visibility = View.GONE
                    holder.bindingAdapter.llMainLayout.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
                }
            }
            holder.bindingAdapter.tvOne.text =listModel.questionOptions!![position].replace(" ", "\n")
            holder.bindingAdapter.cbChecked.text = listModel.questionOptions!![position]
            holder.bindingAdapter.cbChecked2.text = listModel.questionOptions!![position]
            if (dass.progressReportQus.contains(listModel.questionId)) {
                for (i in 0 until dass.progressReportQus.size) {
                    if (dass.progressReportQus[i] == listModel.questionId) {
                        posItem = Integer.parseInt(dass.progressReportAns[i])
                        mSelectedItem = posItem
                        break
                    }
                }
            }
            if (position == posItem) {
                holder.bindingAdapter.cbChecked.isChecked = position == posItem
                holder.bindingAdapter.cbChecked2.isChecked = position == posItem
                holder.bindingAdapter.rbOne.isChecked = position == posItem
            } else {
                holder.bindingAdapter.cbChecked.isChecked = false
                holder.bindingAdapter.cbChecked2.isChecked = false
                holder.bindingAdapter.rbOne.isChecked = false
            }
        }

        /* This function is set que, ans & arranging assessment data */
        private fun setData() {
            val shared = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.ProgressReportQus, dass.gson.toString())
            val json3 = shared.getString(CONSTANTS.ProgressReportAns, dass.gson.toString())
            if (!json2.equals(dass.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                dass.progressReportQus = dass.gson.fromJson(json2, type1)
                dass.progressReportAns = dass.gson.fromJson(json3, type1)
            }
            visibleGoneNext()
        }

        /* This function is visible & gone next button */
        private fun visibleGoneNext() {
            if (dass.progressReportQus.size >= mmypos2) {
                binding.btnNext.isClickable = true
                binding.btnNext.isEnabled = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnContinue.isClickable = true
                binding.btnContinue.isEnabled = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnNext.isEnabled = false
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnContinue.isEnabled = false
                binding.btnContinue.isClickable = false
                binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun getItemCount(): Int {
            return listModelMain.questions!![0].questionOptions!!.size
        }
    }
}