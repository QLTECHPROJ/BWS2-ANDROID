package com.brainwellnessspa.ManageModule

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
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.databinding.*
import com.google.android.flexbox.*


class RecommendedCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendedCategoryBinding
    lateinit var adapter: SelectedCategory
    lateinit var ctx: Context
    private lateinit var adapter1: AllCategory
    private val userList = ArrayList<UserListModel>()
    private val userList1 = ArrayList<UserListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_category)
        ctx = this@RecommendedCategoryActivity
        prepareUserData()
        prepareHeaderData()
        binding.rvSelectedCategory.layoutManager = GridLayoutManager(ctx, 3)
        adapter = SelectedCategory(userList)
        binding.rvSelectedCategory.adapter = adapter

        binding.rvPerantCat.layoutManager = LinearLayoutManager(ctx)
        adapter1 = AllCategory(userList1, ctx)
        binding.rvPerantCat.adapter = adapter1
    }
    private fun prepareUserData() {
        var userAdd = UserListModel("Parental Stress")
        userList.add(userAdd)
        userAdd = UserListModel("Parental Stress")
        userList.add(userAdd)
        userAdd = UserListModel("Parental Stress")
        userList.add(userAdd)
    }

    private fun prepareHeaderData() {
        var userAdd = UserListModel("Mental Health")
        userList1.add(userAdd)
        userAdd = UserListModel("Self - Development")
        userList1.add(userAdd)
        userAdd = UserListModel("Addiction")
        userList1.add(userAdd)
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

    class AllCategory(private val listModel: List<UserListModel>, var ctx: Context) : RecyclerView.Adapter<AllCategory.MyViewHolder>() {
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
            holder.bindingAdapter.tvHeader.text = listModel[position].name

            prepareCatData()
            val layoutManager = FlexboxLayoutManager(ctx)
            layoutManager.flexWrap = FlexWrap.WRAP
            layoutManager.alignItems = AlignItems.STRETCH
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            holder.bindingAdapter.rvChildCategory.layoutManager = layoutManager
            adapter2 = ChildCategory(userList2)
            holder.bindingAdapter.rvChildCategory.adapter = adapter2
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    class ChildCategory(private val listModel: List<UserListModel>) : RecyclerView.Adapter<ChildCategory.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: AllCatDataRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCatDataRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.all_cat_data_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvText.text = listModel[position].name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

}