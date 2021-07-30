package com.brainwellnessspa.assessmentProgressModule.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.callIdentify
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.models.AssessmentQusModel
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding
import com.brainwellnessspa.databinding.FormFillSubBinding
import com.brainwellnessspa.userModule.models.AssessmentSaveDataModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* This activity is assessment form activity */
class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    lateinit var firstListAdapter: OptionsFirstListAdapter
    lateinit var secondListAdapter: OptionsSecondListAdapter
    lateinit var ctx: Context
    var assQus = arrayListOf<String>()
    var assAns = arrayListOf<String>()
    var assSort = arrayListOf<String>()
    lateinit var listModel1: AssessmentQusModel
    lateinit var activity: Activity
    var myPos: Int = 0
    private var doubleBackToExitPressedOnce = false
    var mainAccountID: String? = ""
    var userId: String? = ""
    var email: String? = ""
    var gson: Gson = Gson()
    lateinit var editor: SharedPreferences.Editor

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)

        /* This is the get string mainAccountID, UserId & email */
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        email = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        ctx = this@DassAssSliderActivity
        activity = this@DassAssSliderActivity
        getAssSaveData()
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

        /* This is the next button click */
        binding.btnNext.setOnClickListener {
            getAssSaveData()
            if (myPos < listModel1.responseData!!.questions!!.size - 1) {
                myPos += 2 //                binding.tvNumberOfQus.text = myPos.toString()
                binding.lpIndicator.progress = myPos
                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                }
                val p = Properties()
                p.putValue("screen", myPos)
                BWSApplication.addToSegment(CONSTANTS.Assessment_Screen_Viewed, p, CONSTANTS.screen)
                if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, activity)
                    binding.rvFirstList.adapter = firstListAdapter
                } else {
                    firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, myPos + 2, ctx, binding, activity)
                    binding.rvFirstList.adapter = firstListAdapter
                }
            }
            if (myPos > 2) {
                binding.btnPrev.visibility = View.VISIBLE
            } else {
                binding.btnPrev.visibility = View.GONE
            }
        }

        /* This is the previous button click */
        binding.btnPrev.setOnClickListener {
            callBack()
        }

        /* This is the continue button click when form is complete */
        binding.btnContinue.setOnClickListener {
            binding.lpIndicator.progress = listModel1.responseData!!.questions!!.size
            sendAssessmentData()
            Log.e("Ass Post Data", gson.toJson(assAns))
        }
        prepareData()
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        callBack()
    }

    /* This is the back click event function */
    private fun callBack() {
        if (myPos > 1) {
            myPos -= 2
            val p = Properties()
            p.putValue("screen", myPos)
            BWSApplication.addToSegment(CONSTANTS.Assessment_Screen_Viewed, p, CONSTANTS.screen)
            binding.lpIndicator.progress = myPos //            binding.tvNumberOfQus.text = myPos.toString()
            if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, activity)
                binding.rvFirstList.adapter = firstListAdapter
            } else {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, myPos + 2, ctx, binding, activity)
                binding.rvFirstList.adapter = firstListAdapter
            }
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            this.doubleBackToExitPressedOnce = true
            BWSApplication.showToast("Press again to exit", activity)

            Handler(Looper.myLooper()!!).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    /* This function is save assessment result */
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

    /* This function is get assessment questions */
    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AssessmentQusModel> = APINewClient.client.assessmentQus
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
                            binding.tvQus.text = listModel.responseData!!.toptitle
                            binding.tvText1.text = listModel.responseData!!.subtitle
                            binding.tvText.text = condition
                            binding.lpIndicator.max = listModel.responseData!!.questions!!.size
                            binding.lpIndicator.progress = 0 //                            binding.tvNumberOfQus.text = myPos.toString()
                            //                            binding.tvTotalQus.text = listModel.responseData!!.questions!!.size.toString
                            if (assQus.size != 0) {
                                val mod = assQus.size % 2
                                myPos = if (mod == 0) {
                                    assQus.size
                                } else {
                                    assQus.size - 1
                                }
                                binding.lpIndicator.progress = myPos
                                Log.e("My Pos...", myPos.toString() + "MOD..." + mod.toString() + "Ass Size..." + assQus.size.toString())
                            }
                            val p = Properties()
                            p.putValue("screen", myPos)
                            BWSApplication.addToSegment(CONSTANTS.Assessment_Screen_Viewed, p, CONSTANTS.screen)
                            if (myPos == listModel1.responseData!!.questions!!.size - 1) {
                                binding.btnNext.visibility = View.GONE
                                binding.btnContinue.visibility = View.VISIBLE
                                firstListAdapter = OptionsFirstListAdapter(listModel1.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 1, ctx, binding, activity)
                                binding.rvFirstList.adapter = firstListAdapter
                            } else if (myPos < listModel.responseData!!.questions!!.size) { //                                if(myPos ==)
                                firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 2), myPos, myPos + 2, ctx, binding, activity)
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

    /* This is the first options box input layout */
    class OptionsFirstListAdapter(private val listModel: List<AssessmentQusModel.ResponseData.Questions>?, private val myPos: Int, private val mypos2: Int, private val ctx: Context, var binding: ActivityDassAssSliderBinding, val activity: Activity) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
        private var dass = DassAssSliderActivity()

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
                holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
                if (position == 0) {
                    dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, mypos2, ctx, binding, activity)
                } else {
                    dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos + 1, mypos2, ctx, binding, activity)
                }
                holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    /* This is the second options box input layout */
    class OptionsSecondListAdapter(val listModel: AssessmentQusModel.ResponseData.Questions, val pos: Int, private val mmypos2: Int, val ctx: Context, var binding: ActivityDassAssSliderBinding, val activity: Activity) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
        var mSelectedItem = -1
        var posItem: Int = -1

        var dass = DassAssSliderActivity()

        inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.cbChecked.setOnClickListener {
                    setData()
                    mSelectedItem = absoluteAdapterPosition
                    if (posItem != -1) notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem
                    if (dass.assQus.size == 0) {
                        dass.assQus.add(listModel.question.toString())
                        dass.assAns.add(0, absoluteAdapterPosition.toString())
                        dass.assSort.add(0, pos.toString())

                    } else {
                        if (dass.assQus.contains(listModel.question)) {
                            for (i in 0 until dass.assQus.size) {
                                if (dass.assQus[i] == listModel.question) {
                                    dass.assAns.removeAt(i)
                                    dass.assSort.removeAt(i)
                                    dass.assAns.add(i, absoluteAdapterPosition.toString())
                                    dass.assSort.add(i, pos.toString())
                                }
                            }
                        } else {
                            if (pos > dass.assQus.size) {
                                dass.assQus.add(pos - 1, listModel.question.toString())
                                dass.assAns.add(pos - 1, absoluteAdapterPosition.toString())
                                dass.assSort.add(pos - 1, pos.toString())
                            } else {
                                dass.assQus.add(pos, listModel.question.toString())
                                dass.assAns.add(pos, absoluteAdapterPosition.toString())
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

        /* This is the second options box input layout create */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        /* This is the second options set box input layout */
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            if (dass.assQus.contains(listModel.question)) {
                for (i in 0 until dass.assQus.size) {
                    if (dass.assQus[i] == listModel.question) {
                        posItem = Integer.parseInt(dass.assAns[i])
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

        /* This function is set que, ans & arranging assessment data */
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

        /* This function is visible & gone next button */
        private fun visibleGoneNext() {
            if (dass.assQus.size >= mmypos2) {
                binding.btnNext.isClickable = true
                binding.btnNext.isEnabled = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnContinue.isClickable = true
                binding.btnContinue.isEnabled = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnNext.isEnabled = false
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnContinue.isEnabled = false
                binding.btnContinue.isClickable = false
                binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun getItemCount(): Int {
            val countx = listModel.answer!!.split("| ").toTypedArray()
            return Integer.parseInt(countx[countx.size - 1]) + 1
        }
    }

    /* This function is send assessment data */
    private fun sendAssessmentData() {
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

        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AssessmentSaveDataModel> = APINewClient.client.getAssessmentSaveData(userId, gson.toJson(assAns).toString())
            listCall.enqueue(object : Callback<AssessmentSaveDataModel> {
                override fun onResponse(call: Call<AssessmentSaveDataModel>, response: Response<AssessmentSaveDataModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AssessmentSaveDataModel = response.body()!!
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                val preferencesd1: SharedPreferences = getSharedPreferences(CONSTANTS.AssMain, MODE_PRIVATE)
                                val edited1 = preferencesd1.edit()
                                edited1.remove(CONSTANTS.AssQus)
                                edited1.remove(CONSTANTS.AssAns)
                                edited1.remove(CONSTANTS.AssSort)
                                edited1.clear()
                                edited1.apply()
                                val shareded = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                                val editor = shareded.edit()
                                editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.responseData?.indexScore)
                                editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.responseData?.scoreLevel)
                                editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, CONSTANTS.FLAG_ONE)
                                editor.apply()
                                val p = Properties()
                                p.putValue("ans", gson.toJson(assAns).toString())
                                p.putValue("WellnessScore", listModel.responseData?.indexScore)
                                p.putValue("scoreLevel", listModel.responseData?.scoreLevel)
                                BWSApplication.addToSegment(CONSTANTS.Assessment_Form_Submitted, p, CONSTANTS.track)
                                callIdentify(ctx)
                                val i = Intent(activity, AssProcessActivity::class.java)
                                i.putExtra(CONSTANTS.ASSPROCESS, "1")
                                i.putExtra(CONSTANTS.IndexScore, listModel.responseData?.indexScore)
                                i.putExtra(CONSTANTS.ScoreLevel, listModel.responseData?.scoreLevel)
                                startActivity(i)
                                finish()

                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                BWSApplication.deleteCall(activity)
                                BWSApplication.showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                            else -> {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }
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