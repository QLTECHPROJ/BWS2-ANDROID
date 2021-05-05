package com.brainwellnessspa.ManageModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.RecommendedCategoryModel
import com.brainwellnessspa.DashboardTwoModule.Model.SaveRecommendedCatModel
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel
import com.brainwellnessspa.DashboardTwoModule.Model.sendRecommndedData
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.*
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class RecommendedCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendedCategoryBinding
    lateinit var catListadapter: SelectedCategory
    var ctx: Context? = null
    var USERID: String? = null
    var SleepTime: String? = null
    var CoUserID: String? = null
    var CoEMAIL: String? = null
    private lateinit var adapter1: AllCategory
    lateinit var activity: Activity
    var selectedCategoriesTitle = arrayListOf<String>()
    var selectedCategories = arrayListOf<String>()
    var selectedCategoriesName = arrayListOf<String>()
    var selectedCategoriesSort = arrayListOf<String>()
    var gson: Gson = Gson()
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_category)
        ctx = this@RecommendedCategoryActivity
        activity = this@RecommendedCategoryActivity
        if (intent.extras != null) {
            SleepTime = intent.getStringExtra("SleepTime")
        }

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        CoEMAIL = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
        getCatSaveData()
        prepareRecommnedData()

        binding.btnContinue.setOnClickListener {

            var array = arrayListOf<sendRecommndedData>()

            for (i in 0 until selectedCategoriesTitle.size) {
                var sendR: sendRecommndedData = sendRecommndedData()

                sendR.View = (selectedCategoriesTitle[i])
                sendR.ProblemName = (selectedCategoriesName[i])
                array.add(sendR)
            }



            sendCategoryData(gson.toJson(array))
        }
    }

    private fun prepareRecommnedData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<RecommendedCategoryModel> = APINewClient.getClient().getRecommendedCategory(CoUserID)
            listCall.enqueue(object : Callback<RecommendedCategoryModel> {
                override fun onResponse(call: Call<RecommendedCategoryModel>, response: Response<RecommendedCategoryModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: RecommendedCategoryModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.rvPerantCat.layoutManager = LinearLayoutManager(ctx)
//                            if(listModel.responseData!!.size > 3) {
//                                val listModelNew = arrayListOf<RecommendedCategoryModel.ResponseData>()
//                                for(i in 0..2){
//                                    listModelNew.add(listModel.responseData!![i])
//                                }
//                                adapter1 = AllCategory(binding, listModelNew, ctx!!)
//                                binding.rvPerantCat.adapter = adapter1
//                            }else{
                            adapter1 = AllCategory(binding, listModel.responseData, ctx!!)
                            binding.rvPerantCat.adapter = adapter1
//                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<RecommendedCategoryModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class AllCategory(var binding: ActivityRecommendedCategoryBinding, private val listModel: List<RecommendedCategoryModel.ResponseData>?, var ctx: Context) : RecyclerView.Adapter<AllCategory.MyViewHolder>() {
        private lateinit var adapter2: ChildCategory

        inner class MyViewHolder(var bindingAdapter: AllCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (listModel != null) {
                holder.bindingAdapter.tvHeader.text = listModel[position].view
                val layoutManager = FlexboxLayoutManager(ctx)
                layoutManager.flexWrap = FlexWrap.WRAP
                layoutManager.alignItems = AlignItems.STRETCH
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.FLEX_START
                holder.bindingAdapter.rvChildCategory.layoutManager = layoutManager

                callAdapter(position, holder, position)
//                if (listModel.size == 1) {
//                    callAdapter(position, holder, position)
//                }
//                else if (listModel.size == 2) {
//                    if (position == 0) {
//                        callAdapter(position, holder, position)
//                    }
//                    else if (position == 1) {
//                        callAdapter(position, holder, position)
//                    }
//                }
//                else if (listModel.size == 3) {
//                    if (position == 0) {
//                        callAdapter(position, holder, position)
//                    }
//                    else if (position == 1) {
//                        callAdapter(position, holder, position + 1)
//                    }
//                    else if (position == 2) {
//                        callAdapter(position, holder, position + 1)
//                    }
//                }
            }
        }

        private fun callAdapter(position: Int, holder: MyViewHolder, pos: Int) {
            adapter2 = ChildCategory(binding, listModel!![position].details, listModel, pos, ctx)
            holder.bindingAdapter.rvChildCategory.adapter = adapter2
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    class ChildCategory(var binding: ActivityRecommendedCategoryBinding, private val responseListModel: List<RecommendedCategoryModel.ResponseData.Detail>?,
                        private val listModel: List<RecommendedCategoryModel.ResponseData>?, val pos: Int, var ctx: Context) : RecyclerView.Adapter<ChildCategory.MyViewHolder>() {
        private var mSelectedItem = -1
        var posItem: Int = -1
        var catList = RecommendedCategoryActivity()

        inner class MyViewHolder(var bindingAdapter: AllCatDataRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.tvText.setOnClickListener {
                    setData()
                    mSelectedItem = adapterPosition
                    if (posItem != -1)
                        notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem
                    if (listModel != null) {
                        if (catList.selectedCategoriesTitle.size == 0) {
                            catList.selectedCategoriesTitle.add(listModel[pos].view.toString())
                            catList.selectedCategories.add(0, adapterPosition.toString())
                            catList.selectedCategoriesName.add(listModel[pos].details!![position].problemName.toString())
                            catList.selectedCategoriesSort.add(0, pos.toString())

                        } else {
                            if (catList.selectedCategoriesTitle.contains(listModel[pos].view)) {
                                for (i in 0 until catList.selectedCategoriesTitle.size) {
                                    if (catList.selectedCategoriesTitle[i] == listModel[pos].view) {
                                        catList.selectedCategories.removeAt(i)
                                        catList.selectedCategoriesName.removeAt(i)
                                        catList.selectedCategoriesSort.removeAt(i)
                                        catList.selectedCategories.add(i, adapterPosition.toString())
                                        catList.selectedCategoriesName.add(i, listModel[pos].details!![position].problemName.toString())
                                        catList.selectedCategoriesSort.add(i, pos.toString())
                                    }
                                }
                            } else {
                                if (catList.selectedCategoriesTitle.size < 3) {
//                                    if (pos > catList.selectedCategoriesTitle.size) {
                                    catList.selectedCategoriesTitle.add(listModel[pos].view.toString())
                                    catList.selectedCategories.add(adapterPosition.toString())
                                    catList.selectedCategoriesName.add(listModel[pos].details!![position].problemName.toString())
                                    catList.selectedCategoriesSort.add(pos.toString())
//                                    } else {
//                                        catList.selectedCategoriesTitle.add(pos, listModel[pos].view.toString())
//                                        catList.selectedCategories.add(pos, adapterPosition.toString())
//                                        catList.selectedCategoriesName.add(pos, listModel[pos].details!![position].problemName.toString())
//                                        catList.selectedCategoriesSort.add(pos, pos.toString())
//                                    }
                                }
                            }
                        }
                    }

                    catList.editor = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE).edit()
                    catList.editor.putString(CONSTANTS.selectedCategoriesTitle, catList.gson.toJson(catList.selectedCategoriesTitle)) //Friend
                    catList.editor.putString(CONSTANTS.selectedCategories, catList.gson.toJson(catList.selectedCategories)) //Friend
                    catList.editor.putString(CONSTANTS.selectedCategoriesName, catList.gson.toJson(catList.selectedCategoriesName)) //Friend
                    catList.editor.putString(CONSTANTS.selectedCategoriesSort, catList.gson.toJson(catList.selectedCategoriesSort)) //Friend
                    catList.editor.apply()
                    catList.editor.commit()
                    Log.e("selectedCategoriesTitle", catList.selectedCategoriesTitle.toString())
                    Log.e("selectedCategories", catList.selectedCategories.toString())
                    Log.e("selectedCategoriesName", catList.selectedCategoriesName.toString())
                    Log.e("selectedCategoriesSort", catList.selectedCategoriesSort.toString())
                    Log.e("posItem", posItem.toString())

                    binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
                    catList.catListadapter = SelectedCategory(binding, ctx, catList.selectedCategoriesName)
                    binding.rvSelectedCategory.adapter = catList.catListadapter
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCatDataRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_cat_data_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            if (catList.selectedCategoriesTitle.size != 0) {
                if (catList.selectedCategoriesTitle.contains(listModel!![pos].view)) {
                    for (i in 0 until catList.selectedCategoriesTitle.size) {
                        if (catList.selectedCategoriesTitle[i] == listModel[pos].view) {
                            posItem = Integer.parseInt(catList.selectedCategories[i])
                            mSelectedItem = posItem
                            break
                        }
                    }
                }
            }
//            if (position == posItem) {
//                if (pos == 0) {
//                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
//                } else if (pos == 1) {
//                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
//                } else if (pos == 2) {
//                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_blue)
//                }
//            } else {
//                holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_gray)
//            }
            if (position == posItem) {
                if (catList.selectedCategoriesTitle.size == 1) {
                    holder.bindingAdapter.llCategory.visibility = View.GONE
                    holder.bindingAdapter.llCategoryPink.visibility = View.VISIBLE
                    holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                    holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                } else if (catList.selectedCategoriesTitle.size == 2) {
                    holder.bindingAdapter.llCategory.visibility = View.GONE
                    holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                    holder.bindingAdapter.llCategoryGreen.visibility = View.VISIBLE
                    holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                } else if (catList.selectedCategoriesTitle.size == 3) {
                    holder.bindingAdapter.llCategory.visibility = View.GONE
                    holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                    holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                    holder.bindingAdapter.llCategoryBlue.visibility = View.VISIBLE
                }
            } else {
                holder.bindingAdapter.llCategory.visibility = View.VISIBLE
                holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
            }
            holder.bindingAdapter.tvText.text = responseListModel!![position].problemName.toString()
            holder.bindingAdapter.tvText1.text = responseListModel[position].problemName.toString()
            holder.bindingAdapter.tvText2.text = responseListModel[position].problemName.toString()
            holder.bindingAdapter.tvText3.text = responseListModel[position].problemName.toString()
        }

        private fun setData() {
            val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.selectedCategoriesTitle, catList.gson.toString())
            val json3 = shared.getString(CONSTANTS.selectedCategories, catList.gson.toString())
            val json5 = shared.getString(CONSTANTS.selectedCategoriesName, catList.gson.toString())
            val json4 = shared.getString(CONSTANTS.selectedCategoriesSort, catList.gson.toString())
            if (!json2.equals(catList.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
                catList.selectedCategoriesTitle = catList.gson.fromJson(json2, type1)
                catList.selectedCategories = catList.gson.fromJson(json3, type1)
                catList.selectedCategoriesName = catList.gson.fromJson(json5, type1)
                catList.selectedCategoriesSort = catList.gson.fromJson(json4, type1)
            }
        }

        override fun getItemCount(): Int {
            return listModel!![pos].details!!.size
        }
    }

    private fun getCatSaveData() {
        val shared = ctx!!.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.selectedCategoriesTitle, gson.toString())
        val json3 = shared.getString(CONSTANTS.selectedCategories, gson.toString())
        val json5 = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        val json4 = shared.getString(CONSTANTS.selectedCategoriesSort, gson.toString())
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            selectedCategoriesTitle = gson.fromJson(json2, type1)
            selectedCategories = gson.fromJson(json3, type1)
            selectedCategoriesName = gson.fromJson(json5, type1)
            selectedCategoriesSort = gson.fromJson(json4, type1)
        }
        if (selectedCategoriesTitle.size > 0) {
            binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
            catListadapter = SelectedCategory(binding, ctx!!, selectedCategoriesName)
            binding.rvSelectedCategory.adapter = catListadapter
        }
    }

    class SelectedCategory(var binding: ActivityRecommendedCategoryBinding, var ctx: Context, var selectedCategoriesName: ArrayList<String>) : RecyclerView.Adapter<SelectedCategory.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.selected_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvCategory.text = selectedCategoriesName[position]
            holder.bindingAdapter.tvhours.text = (position + 1).toString()
            if (selectedCategoriesName.size == 3) {
                if (position == 0) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                } else if (position == 1) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_green)
                } else if (position == 2) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_blue)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_blue)
                }
            } else if (selectedCategoriesName.size == 2) {
                if (position == 0) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                } else if (position == 1) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_green)
                }
            } else if (selectedCategoriesName.size == 1) {
                if (position == 0) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                }
            }
        }

        override fun getItemCount(): Int {
            return selectedCategoriesName.size
        }
    }

    private fun sendCategoryData(toJson: String) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SaveRecommendedCatModel> = APINewClient.getClient().getSaveRecommendedCategory(CoUserID, toJson, SleepTime)
            listCall.enqueue(object : Callback<SaveRecommendedCatModel> {
                override fun onResponse(call: Call<SaveRecommendedCatModel>, response: Response<SaveRecommendedCatModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SaveRecommendedCatModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            val shared = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.responseData!!.avgSleepTime)
                            editor.commit()
                            finish()
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, activity)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SaveRecommendedCatModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onBackPressed() {
        val i = Intent(ctx, SleepTimeActivity::class.java)
        i.putExtra("SleepTime", SleepTime)
        startActivity(i)
        finish()
    }
}