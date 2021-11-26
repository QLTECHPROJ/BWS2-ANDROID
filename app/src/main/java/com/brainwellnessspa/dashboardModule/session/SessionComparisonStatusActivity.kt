package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
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
import com.brainwellnessspa.dashboardModule.models.BeforeAfterComparisionQuestionListModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
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
    var assAns = arrayListOf<String>()
    var assqusId = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_comparison_status)
        act = this@SessionComparisonStatusActivity

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        if (intent.extras != null) {
            sessionId = intent.getStringExtra("SessionId")
            stepId = intent.getStringExtra("StepId")
        }
        binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

        binding.btnNext.setOnClickListener {
            if (myPos < listModel.responseData!!.questions!!.size - 1) {
                myPos += 1 //                binding.tvNumberOfQus.text = myPos.toString()
                binding.lpIndicator.progress = myPos
                if (myPos == listModel.responseData!!.questions!!.size - 1) {
                    binding.btnNext.visibility = View.GONE
                    binding.btnDone.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnDone.visibility = View.GONE
                }
                firstListAdapter = OptionsFirstListAdapter(listModel.responseData!!.questions!!.subList(myPos, myPos + 1), myPos, myPos + 2, ctx, binding, act)
                binding.rvFirstList.adapter = firstListAdapter
            }
            if (myPos > 1) {
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

    private fun callBack() {
        if (myPos > 1) {
            myPos -= 1
            binding.lpIndicator.progress = myPos //            binding.tvNumberOfQus.text = myPos.toString()
            binding.btnNext.visibility = View.GONE
            binding.btnDone.visibility = View.VISIBLE
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
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                binding.lpIndicator.progress = 0
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
            if (scsa.assqusId.size != 0) {
                if (scsa.assqusId.contains(listModel!![position].question)) {
                    for (i in 0 until scsa.assqusId.size) {
                        if (scsa.assqusId[i] == listModel[position].question) {
                            if (scsa.assAns[i] == "yes") {
                                holder.bindingAdapter.cbYes1.isSelected = true
                                holder.bindingAdapter.cbNo1.isSelected = false
                                holder.bindingAdapter.cbYes1.isChecked = true
                                holder.bindingAdapter.cbNo1.isChecked = false
                            } else if (scsa.assAns[i] == "NO") {

                                holder.bindingAdapter.cbYes1.isSelected = false
                                holder.bindingAdapter.cbNo1.isSelected = true
                                holder.bindingAdapter.cbYes1.isChecked = false
                                holder.bindingAdapter.cbNo1.isChecked = true
                            }
                            break
                        }
                    }
                }
            }
            if (listModel != null) {
                holder.bindingAdapter.tvQus.text = listModel[position].question
                holder.bindingAdapter.tvSubDec.text = listModel[position].stepShortDescription
                holder.bindingAdapter.cbYes1.text = listModel[position].questionOptions!![0]
                holder.bindingAdapter.cbNo1.text = listModel[position].questionOptions!![1]

                holder.bindingAdapter.cbYes1.setOnClickListener {
                    scsa.assqusId.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "Yes")
                }
                holder.bindingAdapter.cbNo1.setOnClickListener {
                    scsa.assqusId.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "No")
                }

                if (holder.bindingAdapter.cbYes1.isChecked) {
                    scsa.assqusId.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "Yes")
                } else if (holder.bindingAdapter.cbNo1.isChecked) {
                    scsa.assqusId.add(position, (position + 1).toString())
                    scsa.assAns.add(position, "No")
                }
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }
}