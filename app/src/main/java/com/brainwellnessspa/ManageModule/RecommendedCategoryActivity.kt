package com.brainwellnessspa.ManageModule

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.AverageSleepTimeModel
import com.brainwellnessspa.DashboardTwoModule.Model.RecommendedCategoryModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.databinding.*
import com.google.android.flexbox.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecommendedCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendedCategoryBinding
    lateinit var adapter: SelectedCategory
    lateinit var ctx: Context
    private lateinit var adapter1: AllCategory
    private val userList = ArrayList<UserListModel>()
    private val userList1 = ArrayList<UserListModel>()
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_category)
        ctx = this@RecommendedCategoryActivity
        activity = this@RecommendedCategoryActivity
        prepareRecommnedData()

    }

    private fun prepareRecommnedData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<RecommendedCategoryModel> = APINewClient.getClient().getRecommendedCategory()
            listCall.enqueue(object : Callback<RecommendedCategoryModel> {
                override fun onResponse(call: Call<RecommendedCategoryModel>, response: Response<RecommendedCategoryModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: RecommendedCategoryModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.rvPerantCat.layoutManager = LinearLayoutManager(ctx)
                            adapter1 = AllCategory(listModel, ctx)
                            binding.rvPerantCat.adapter = adapter1

                            binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
                            adapter = SelectedCategory(userList)
                            binding.rvSelectedCategory.adapter = adapter

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

    class SelectedCategory(private val listModel: List<UserListModel>) : RecyclerView.Adapter<SelectedCategory.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.selected_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvCategory.text = listModel[position].name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    class AllCategory(private val listModel: RecommendedCategoryModel, var ctx: Context) : RecyclerView.Adapter<AllCategory.MyViewHolder>() {
        private lateinit var adapter2: ChildCategory
        private val userList2 = ArrayList<UserListModel>()
        private fun prepareCatData() {
            var userAdd = UserListModel("Parental Stress")
            userList2.add(userAdd)
            userAdd = UserListModel("Alcohol Addiction")
            userList2.add(userAdd)
            userAdd = UserListModel("Money Stress")
            userList2.add(userAdd)
            userAdd = UserListModel("Eating Disorder")
            userList2.add(userAdd)
            userAdd = UserListModel("Motivation / Empowernment / Mindset")
            userList2.add(userAdd)
            userAdd = UserListModel("Communication / Self Epression / Public Speaking")
            userList2.add(userAdd)
            userAdd = UserListModel("Relationship Breakdown")
            userList2.add(userAdd)
            userAdd = UserListModel("Study and Eam Stress")
            userList2.add(userAdd)
            userAdd = UserListModel("Parental Stress")
            userList2.add(userAdd)
            userAdd = UserListModel("Motivation / Empowernment / Mindset")
            userList2.add(userAdd)
            userAdd = UserListModel("Communication / Self Epression / Public Speaking")
            userList2.add(userAdd)
            userAdd = UserListModel("Relationship Breakdown")
            userList2.add(userAdd)
            userAdd = UserListModel("Study and Eam Stress")
            userList2.add(userAdd)
        }

        inner class MyViewHolder(var bindingAdapter: AllCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvHeader.text = listModel.toString()

            prepareCatData()
            val layoutManager = FlexboxLayoutManager(ctx)
            layoutManager.flexWrap = FlexWrap.WRAP
            layoutManager.alignItems = AlignItems.STRETCH
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            holder.bindingAdapter.rvChildCategory.layoutManager = layoutManager
            adapter2 = ChildCategory(listModel.responseData)
            holder.bindingAdapter.rvChildCategory.adapter = adapter2
        }

        override fun getItemCount(): Int {
//            return listModel.size
            return 0
        }
    }

    class ChildCategory(private val listModel: RecommendedCategoryModel.ResponseData?) : RecyclerView.Adapter<ChildCategory.MyViewHolder>() {
        inner class MyViewHolder(var bindingAdapter: AllCatDataRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCatDataRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_cat_data_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//            holder.bindingAdapter.tvText.text = listModel[position].name
        }

        override fun getItemCount(): Int {
//            return listModel.size
            return 0
        }
    }
}