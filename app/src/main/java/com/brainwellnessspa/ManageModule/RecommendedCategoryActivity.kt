package com.brainwellnessspa.ManageModule

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.RecommendedCategoryModel
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel
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

        val preferencesx: SharedPreferences = getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
        val editx = preferencesx.edit()
        editx.remove(CONSTANTS.selectedCategoriesTitle)
        editx.remove(CONSTANTS.selectedCategories)
        editx.remove(CONSTANTS.selectedCategoriesName)
        editx.remove(CONSTANTS.selectedCategoriesSort)
        editx.clear()
        editx.commit()
        prepareRecommnedData()
        getCatSaveData()
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
                            adapter1 = AllCategory(binding, listModel.responseData, ctx!!)
                            binding.rvPerantCat.adapter = adapter1
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
                holder.bindingAdapter.tvHeader.text = listModel.get(position).view
                val layoutManager = FlexboxLayoutManager(ctx)
                layoutManager.flexWrap = FlexWrap.WRAP
                layoutManager.alignItems = AlignItems.STRETCH
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.FLEX_START
                holder.bindingAdapter.rvChildCategory.layoutManager = layoutManager

                if (position == 0) {
                    adapter2 = ChildCategory(binding, listModel.get(position).details, listModel, position, ctx)
                } else {
                    adapter2 = ChildCategory(binding, listModel.get(position).details, listModel, position, ctx)
                }
                holder.bindingAdapter.rvChildCategory.adapter = adapter2
            }
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
                            catList.selectedCategoriesTitle.add(listModel.get(pos).view.toString())
                            catList.selectedCategories.add(0, adapterPosition.toString())
                            catList.selectedCategoriesName.add(listModel.get(pos).details!!.get(position).problemName.toString())
                            catList.selectedCategoriesSort.add(0, pos.toString())

                        } else {
                            if (catList.selectedCategoriesTitle.contains(listModel.get(pos).view)) {
                                for (i in 0 until catList.selectedCategoriesTitle.size) {
                                    if (catList.selectedCategoriesTitle[i] == listModel.get(pos).view) {
                                        catList.selectedCategories.removeAt(i)
                                        catList.selectedCategoriesName.removeAt(i)
                                        catList.selectedCategoriesSort.removeAt(i)
                                        catList.selectedCategories.add(i, adapterPosition.toString())
                                        catList.selectedCategoriesName.add(i, listModel.get(pos).details!!.get(position).problemName.toString())
                                        catList.selectedCategoriesSort.add(i, pos.toString())
                                    }
                                }
                            } else {
                                if (pos > catList.selectedCategoriesTitle.size) {
                                    catList.selectedCategoriesTitle.add(pos - 1, listModel.get(pos).view.toString())
                                    catList.selectedCategories.add(pos - 1, adapterPosition.toString())
                                    catList.selectedCategoriesName.add(pos - 1, listModel.get(pos).details!!.get(position).problemName.toString())
                                    catList.selectedCategoriesSort.add(pos - 1, pos.toString())
                                } else {
                                    catList.selectedCategoriesTitle.add(pos, listModel.get(pos).view.toString())
                                    catList.selectedCategories.add(pos, adapterPosition.toString())
                                    catList.selectedCategoriesName.add(pos, listModel.get(pos).details!!.get(position).problemName.toString())
                                    catList.selectedCategoriesSort.add(pos, pos.toString())
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
                    Log.e("mSelectedItem", mSelectedItem.toString())

//                    binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
//                    catList.catListadapter = SelectedCategory(binding)
//                    binding.rvSelectedCategory.adapter = catList.catListadapter
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCatDataRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_cat_data_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            if (catList.selectedCategoriesTitle.contains(listModel!!.get(position).view)) {
                for (i in 0 until catList.selectedCategoriesTitle.size) {
                    if (catList.selectedCategoriesTitle[i] == listModel.get(position).view) {
                        posItem = Integer.parseInt(catList.selectedCategories.get(i))
                        mSelectedItem = posItem
                        break
                    }
                }
            }

            if (position == posItem) {
                holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
            } else {
                holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_gray)
            }
            holder.bindingAdapter.tvText.text = responseListModel!!.get(position).problemName.toString()
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
            return listModel!!.get(pos).details!!.size
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
    }

    class SelectedCategory(var binding: ActivityRecommendedCategoryBinding) : RecyclerView.Adapter<SelectedCategory.MyViewHolder>() {
        var catList = RecommendedCategoryActivity()
        val shared = catList.ctx!!.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.selectedCategoriesTitle, catList.gson.toString())
        val json3 = shared.getString(CONSTANTS.selectedCategories, catList.gson.toString())
        val json5 = shared.getString(CONSTANTS.selectedCategoriesName, catList.gson.toString())
        val json4 = shared.getString(CONSTANTS.selectedCategoriesSort, catList.gson.toString())

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.selected_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (!json2.equals(catList.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
                catList.selectedCategoriesTitle = catList.gson.fromJson(json2, type1)
                catList.selectedCategories = catList.gson.fromJson(json3, type1)
                catList.selectedCategoriesName = catList.gson.fromJson(json5, type1)
                catList.selectedCategoriesSort = catList.gson.fromJson(json4, type1)
            }
            val elements: Array<String> = json5!!.split(",".toRegex()).toTypedArray()
            val category = Arrays.asList(*elements)
            holder.bindingAdapter.tvCategory.text = category.toString()
        }

        override fun getItemCount(): Int {
            return catList.selectedCategoriesName.toString().length
        }
    }
    private fun sendCategoryData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SucessModel> = APINewClient.getClient().getSaveRecommendedCategory(CoUserID, gson.toJson(selectedCategoriesName).toString(), SleepTime)
            listCall.enqueue(object : Callback<SucessModel> {
                override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SucessModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            /*val i = Intent(activity, AssProcessActivity::class.java)
                            i.putExtra(CONSTANTS.ASSPROCESS, "1")
                            i.putExtra(CONSTANTS.IndexScore, listModel.getResponseData()?.indexScore)
                            startActivity(i)*/
                            finish()
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, applicationContext)
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

    override fun onBackPressed() {
        val i = Intent(ctx, SleepTimeActivity::class.java)
        i.putExtra("SleepTime", SleepTime)
        startActivity(i)
        finish()
    }
}