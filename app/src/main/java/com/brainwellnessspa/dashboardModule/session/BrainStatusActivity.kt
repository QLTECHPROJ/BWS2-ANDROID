package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.BrainCatListModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.ActivityBrainStatusBinding
import com.brainwellnessspa.databinding.BrainFeelingStatusLayoutBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrainStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivityBrainStatusBinding
    lateinit var adapter: BrainFeelingStatusAdapter
    lateinit var activity: Activity
    lateinit var ctx: Context
    var gson: Gson = Gson()
    var userId: String? = ""
    var SessionId: String? = "1"
    var Type: String? = "before"
    var EEPCatId = arrayListOf<String>()
    var EEPCatName = arrayListOf<String>()
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brain_status)
        activity = this@BrainStatusActivity
        ctx = this@BrainStatusActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val preferred = ctx.getSharedPreferences(CONSTANTS.EEPCatMain, Context.MODE_PRIVATE)
        val edited = preferred.edit()
        edited.remove(CONSTANTS.EEPCatName)
        edited.clear()
        edited.apply()
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvList.layoutManager = layoutManager
        prepareData()
        binding.btnContinue.setOnClickListener {
            getCatSaveData()
            sendCategoryData()
        }
    }

    private fun sendCategoryData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SucessModel> = APINewClient.client.getBrainFeelingSaveCat(userId, SessionId, Type, gson.toJson(EEPCatId))
            listCall.enqueue(object : Callback<SucessModel> {
                override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SucessModel = response.body()!!
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess)) -> {
                                BWSApplication.showToast(listModel.responseMessage, activity)

                                val i = Intent(activity, SessionPcDetailActivity::class.java)
                                startActivity(i)
                                finish()
                                //                                BWSApplication.localIntent = Intent("Reminder")
//                                BWSApplication.localBroadcastManager = LocalBroadcastManager.getInstance(ctx)
//                                BWSApplication.localIntent.putExtra("MyReminder", "update")
//                                BWSApplication.localBroadcastManager.sendBroadcast(BWSApplication.localIntent)
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted)) -> {
                                BWSApplication.callDelete403(activity, listModel.responseMessage)
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodefail)) -> {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }
                            else -> {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SucessModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.braincatLists
            listCall.enqueue(object : Callback<BrainCatListModel?> {
                override fun onResponse(call: Call<BrainCatListModel?>, response: Response<BrainCatListModel?>) {
                    try {
                        val listModel = response.body()
                        val responsedb = listModel?.responseData
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (responsedb != null) {
                                adapter = BrainFeelingStatusAdapter(binding, activity, responsedb.data, ctx, binding)
                            }
                            binding.rvList.adapter = adapter
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BrainCatListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun getCatSaveData() {
        val shared = ctx.getSharedPreferences(CONSTANTS.EEPCatMain, Context.MODE_PRIVATE)
        val json1 = shared.getString(CONSTANTS.EEPCatId, gson.toString())
        val json5 = shared.getString(CONSTANTS.EEPCatName, gson.toString())
        if (!json5.equals(gson.toString())) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            EEPCatId = gson.fromJson(json1, type1)
            EEPCatName = gson.fromJson(json5, type1)
        }
        if (EEPCatName.size > 0) {
            binding.btnContinue.isEnabled = true
            binding.btnContinue.isClickable = true
            binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
        } else {
            binding.btnContinue.isEnabled = false
            binding.btnContinue.isClickable = false
            binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
        }
    }

    class BrainFeelingStatusAdapter(var binding: ActivityBrainStatusBinding, var activity: Activity, var listModel: List<BrainCatListModel.ResponseData.Data>?, var ctx: Context, var binding1: ActivityBrainStatusBinding) : RecyclerView.Adapter<BrainFeelingStatusAdapter.MyViewHolder>() {

        var catList = BrainStatusActivity()

        inner class MyViewHolder(var bindingAdapter: BrainFeelingStatusLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BrainFeelingStatusLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.brain_feeling_status_layout, parent, false)
            setData()
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvText.text = listModel!![position].name
            holder.bindingAdapter.llCategory.setOnClickListener {
                if (listModel != null) {
                    if (catList.EEPCatName.size == 0) {
                        catList.EEPCatName.add(listModel!![position].name.toString())
                        catList.EEPCatId.add(listModel!![position].id.toString())
                        if (listModel!![position].catFlag.equals("0", ignoreCase = true)) {
                            holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                        } else if (listModel!![position].catFlag.equals("1", ignoreCase = true)) {
                            holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                        }
                    } else {
                        if (catList.EEPCatName.contains(listModel!![position].name)) {
                            for (i in 0 until catList.EEPCatName.size) {
                                if (catList.EEPCatName[i] == listModel!![position].name) {
                                    catList.EEPCatId.removeAt(i)
                                    catList.EEPCatName.removeAt(i)
                                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_gray)
                                    break
                                }
                            }
                        } else {
                            if (catList.EEPCatName.size < 3) {
                                catList.EEPCatId.add(listModel!![position].id.toString())
                                catList.EEPCatName.add(listModel!![position].name.toString())
                                if (listModel!![position].catFlag.equals("0", ignoreCase = true)) {
                                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                                } else if (listModel!![position].catFlag.equals("1", ignoreCase = true)) {
                                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                                }
                            } else {
                                BWSApplication.showToast("You can pick up to 3 areas of focus. They can be changed anytime.", activity)
                            }
                        }
                    }
                }

                catList.editor = ctx.getSharedPreferences(CONSTANTS.EEPCatMain, Context.MODE_PRIVATE).edit()
                catList.editor.putString(CONSTANTS.EEPCatName, catList.gson.toJson(catList.EEPCatName)) //Friend
                catList.editor.putString(CONSTANTS.EEPCatId, catList.gson.toJson(catList.EEPCatId)) //Friend
                catList.editor.apply()
                catList.editor.commit()
                Log.e("EEPCatName", catList.EEPCatName.toString())
                Log.e("EEPCatId", catList.EEPCatId.toString())
                setData()
            }
            if (catList.EEPCatName.size != 0) {
                if (catList.EEPCatName.contains(listModel!![position].name)) {
                    for (i in 0 until catList.EEPCatName.size) {
                        if (catList.EEPCatName[i] == listModel!![position].name && listModel!![position].catFlag.equals("0", ignoreCase = true)) {
                            holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                            break
                        } else if (catList.EEPCatName[i] == listModel!![position].name && listModel!![position].catFlag.equals("1", ignoreCase = true)) {
                            holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                            break
                        }
                    }
                } else {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_gray)
                }
            }
        }

        private fun setData() {
            val shared = ctx.getSharedPreferences(CONSTANTS.EEPCatMain, Context.MODE_PRIVATE)
            val json1 = shared.getString(CONSTANTS.EEPCatId, catList.gson.toString())
            val json5 = shared.getString(CONSTANTS.EEPCatName, catList.gson.toString())
            if (!json5.equals(catList.gson.toString())) {
                val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
                catList.EEPCatId = catList.gson.fromJson(json1, type1)
                catList.EEPCatName = catList.gson.fromJson(json5, type1)
            }
            if (catList.EEPCatName.size > 0) {
                binding.btnContinue.isEnabled = true
                binding.btnContinue.isClickable = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnContinue.isEnabled = false
                binding.btnContinue.isClickable = false
                binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }
}