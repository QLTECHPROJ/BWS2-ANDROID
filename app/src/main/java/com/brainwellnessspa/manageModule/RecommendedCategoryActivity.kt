package com.brainwellnessspa.manageModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.dashboardModule.manage.PreparePlaylistActivity
import com.brainwellnessspa.dashboardModule.models.RecommendedCategoryModel
import com.brainwellnessspa.dashboardModule.models.SaveRecommendedCatModel
import com.brainwellnessspa.dashboardModule.models.sendRecommndedData
import com.brainwellnessspa.databinding.*
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class RecommendedCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendedCategoryBinding
    lateinit var catListadapter: SelectedCategory
    var ctx: Context? = null
    var userId: String? = null
    var backClick: String? = null
    lateinit var gsonBuilder: GsonBuilder
    lateinit var section: java.util.ArrayList<String>
    var sleepTime: String? = null
    var coUserId: String? = null
    var coEmail: String? = null
    private lateinit var adapter1: AllCategory
    lateinit var activity: Activity
    var selectedCategoriesTitle = arrayListOf<String>()
    var selectedCategoriesName = arrayListOf<String>()
    var gson: Gson = Gson()
    lateinit var searchEditText: EditText
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_category)
        ctx = this@RecommendedCategoryActivity
        activity = this@RecommendedCategoryActivity
        if (intent.extras != null) {
            sleepTime = intent.getStringExtra("SleepTime")
            backClick = intent.getStringExtra("BackClick")
        }

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        coEmail = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")

        val p = Properties()
        p.putValue("coUserId", coUserId)
        addToSegment("Recommeded Category Screen Viewed", p, CONSTANTS.screen)

        val layoutManager = FlexboxLayoutManager(ctx)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvSelectedCategory.layoutManager = layoutManager
        getCatSaveData()
        prepareRecommnedData()
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity,R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity,R.color.gray))
        val closeButton: ImageView = binding.searchView.findViewById(R.id.search_close_btn)
        binding.searchView.clearFocus()
        closeButton.setOnClickListener {
            prepareRecommnedData()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                try {
                    if (!search.equals("",ignoreCase = true)){
                        adapter1.filter.filter(search)
                    }else {
                        prepareRecommnedData()
                    }
//                        SearchFlag = search
                    Log.e("searchsearch", "" + search)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return false
            }
        })

        binding.btnContinue.setOnClickListener {
            getCatSaveData()
            val array = arrayListOf<sendRecommndedData>()
            for (i in 0 until selectedCategoriesTitle.size) {
                val sendR = sendRecommndedData()
                sendR.View = (selectedCategoriesTitle[i])
                sendR.ProblemName = (selectedCategoriesName[i])
                array.add(sendR)
            }
            sendCategoryData(gson.toJson(array))
        }
    }

    private fun prepareRecommnedData() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<RecommendedCategoryModel> =
                APINewClient.getClient().getRecommendedCategory(coUserId)
            listCall.enqueue(object : Callback<RecommendedCategoryModel> {
                override fun onResponse(
                    call: Call<RecommendedCategoryModel>,
                    response: Response<RecommendedCategoryModel>
                ) {
                    try {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: RecommendedCategoryModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            binding.searchView.clearFocus()
                            searchEditText.setText("")
                            binding.searchView.setQuery("", false)
                            binding.rvPerantCat.layoutManager = LinearLayoutManager(ctx)
//                            if(listModel.responseData!!.size > 3) {
//                                val listModelNew = arrayListOf<RecommendedCategoryModel.ResponseData>()
//                                for(i in 0..2){
//                                    listModelNew.add(listModel.responseData!![i])
//                                }
//                                adapter1 = AllCategory(binding, listModelNew, ctx!!)
//                                binding.rvPerantCat.adapter = adapter1
//                            }else{
                            binding.llError.visibility = View.GONE
                            binding.rvPerantCat.visibility = View.VISIBLE
                            adapter1 =
                                AllCategory(binding, listModel.responseData!!, ctx!!, activity)
                            binding.rvPerantCat.adapter = adapter1
//                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<RecommendedCategoryModel>, t: Throwable) {
                    hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    class AllCategory(
        var binding: ActivityRecommendedCategoryBinding,
        var listModel: ArrayList<RecommendedCategoryModel.ResponseData>,
        var ctx: Context,
        var activity: Activity
    ) : RecyclerView.Adapter<AllCategory.MyViewHolder>(), Filterable {
        private lateinit var adapter2: ChildCategory

        private var listFilterData: ArrayList<RecommendedCategoryModel.ResponseData> = listModel

        inner class MyViewHolder(var bindingAdapter: AllCategoryRawBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCategoryRawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.all_category_raw,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//            if(listFilterData[position].details!!.size!=0) {
                holder.bindingAdapter.tvHeader.text = listFilterData[position].view
                val layoutManager = FlexboxLayoutManager(ctx)
                layoutManager.flexWrap = FlexWrap.WRAP
                layoutManager.alignItems = AlignItems.STRETCH
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.FLEX_START
                holder.bindingAdapter.rvChildCategory.layoutManager = layoutManager
                callAdapter(position, holder, position)
         /*   }else{
                holder.bindingAdapter.llMainLayout.visibility = View.GONE
            }*/
        }

        private fun callAdapter(position: Int, holder: MyViewHolder, pos: Int) {
            adapter2 = ChildCategory(
                binding,
                listFilterData[position].details,
                listFilterData,
                pos,
                ctx,
                activity
            )
            holder.bindingAdapter.rvChildCategory.adapter = adapter2
        }

        override fun getItemCount(): Int {
            return listFilterData.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val filterResults = FilterResults()
                    val filteredList = ArrayList<RecommendedCategoryModel.ResponseData>()
                    if (charSequence.toString().isEmpty() || charSequence.toString() == "") {
                        listFilterData.addAll(listModel)
                    } else {
                        val filteredListnew = ArrayList<RecommendedCategoryModel.ResponseData>()
                         filteredListnew.addAll(listModel)
                        for (i1 in filteredListnew) {
                            val modelFilterList = RecommendedCategoryModel.ResponseData()
                            val r = ArrayList<RecommendedCategoryModel.ResponseData.Detail>()
                            for (i in i1.details!!) {
                                if (i.problemName!!.toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT))) {
                                    r.add(i)
                                }
                            }
                            modelFilterList.id=i1.id
                            modelFilterList.view = i1.view
                            if(r.size!=0) {
                                modelFilterList.details = r
                                filteredList.add(modelFilterList)
                            }
                        }
                    }
                    filterResults.values = filteredList
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    listFilterData = filterResults.values as ArrayList<RecommendedCategoryModel.ResponseData>
                    if (listFilterData.isEmpty()) {
                        binding.llError.visibility = View.VISIBLE
                        binding.rvPerantCat.visibility = View.GONE
                        binding.tvFound.text = "No result found"
                    } else {
                        binding.llError.visibility = View.GONE
                        binding.tvFound.visibility = View.VISIBLE
                        binding.rvPerantCat.visibility = View.VISIBLE
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    class ChildCategory(
        var binding: ActivityRecommendedCategoryBinding,
        private val responseListModel: List<RecommendedCategoryModel.ResponseData.Detail>?,
        private val listModel: ArrayList<RecommendedCategoryModel.ResponseData>?,
        val pos: Int,
        var ctx: Context,
        var activity: Activity
    ) : RecyclerView.Adapter<ChildCategory.MyViewHolder>() {
        private var mSelectedItem = -1
        var posItem: Int = -1
        var catList = RecommendedCategoryActivity()

        inner class MyViewHolder(var bindingAdapter: AllCatDataRawBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root) {
            init {
                bindingAdapter.llCategory.setOnClickListener {
                    setData()
                    mSelectedItem = absoluteAdapterPosition
                    if (posItem != -1)
                        notifyItemChanged(posItem)
                    notifyItemChanged(mSelectedItem)
                    posItem = mSelectedItem
                    if (listModel != null) {
                        if (catList.selectedCategoriesTitle.size == 0) {
                            catList.selectedCategoriesTitle.add(listModel[pos].view.toString())
                            catList.selectedCategoriesName.add(listModel[pos].details!![position].problemName.toString())

                        } else {
                            if (catList.selectedCategoriesTitle.contains(listModel[pos].view)) {
                                for (i in 0 until catList.selectedCategoriesTitle.size) {
                                    if (catList.selectedCategoriesTitle[i] == listModel[pos].view) {
                                        if (catList.selectedCategoriesName[i] == listModel[pos].details!![position].problemName) {
                                            catList.selectedCategoriesTitle.removeAt(i)
                                            catList.selectedCategoriesName.removeAt(i)
                                            posItem = -1
                                            mSelectedItem = -1
                                            notifyItemChanged(posItem)
                                            notifyItemChanged(mSelectedItem)
                                        } else {
                                            catList.selectedCategoriesName.removeAt(i)
                                            catList.selectedCategoriesName.add(
                                                i,
                                                listModel[pos].details!![position].problemName.toString()
                                            )
                                        }
                                    }
                                }
                            } else {
                                if (catList.selectedCategoriesTitle.size < 3) {
//                                    if (pos > catList.selectedCategoriesTitle.size) {
                                    catList.selectedCategoriesTitle.add(listModel[pos].view.toString())
                                    catList.selectedCategoriesName.add(listModel[pos].details!![position].problemName.toString())
//                                    } else {
//                                        catList.selectedCategoriesTitle.add(pos, listModel[pos].view.toString())
//                                        catList.selectedCategories.add(pos, adapterPosition.toString())
//                                        catList.selectedCategoriesName.add(pos, listModel[pos].details!![position].problemName.toString())
//                                        catList.selectedCategoriesSort.add(pos, pos.toString())
//                                    }
                                } else {
                                    showToast(
                                        "You can Select only three Problems Please unselect Old for new select",
                                        activity
                                    )
                                }
                            }
                        }
                    }

                    catList.editor =
                        ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE).edit()
                    catList.editor.putString(
                        CONSTANTS.selectedCategoriesTitle,
                        catList.gson.toJson(catList.selectedCategoriesTitle)
                    ) //Friend
                    catList.editor.putString(
                        CONSTANTS.selectedCategoriesName,
                        catList.gson.toJson(catList.selectedCategoriesName)
                    ) //Friend
                    catList.editor.apply()
                    catList.editor.commit()
                    Log.e("selectedCategoriesTitle", catList.selectedCategoriesTitle.toString())
                    Log.e("selectedCategoriesName", catList.selectedCategoriesName.toString())
                    Log.e("posItem", posItem.toString())

                    val layoutManager = FlexboxLayoutManager(ctx)
                    layoutManager.flexWrap = FlexWrap.WRAP
                    layoutManager.alignItems = AlignItems.STRETCH
                    layoutManager.flexDirection = FlexDirection.ROW
                    layoutManager.justifyContent = JustifyContent.FLEX_START
                    binding.rvSelectedCategory.layoutManager = layoutManager
                    catList.catListadapter =
                        SelectedCategory(binding, ctx, catList.selectedCategoriesName)
                    binding.rvSelectedCategory.adapter = catList.catListadapter
                }

                bindingAdapter.llCategoryPink.setOnClickListener {
                    deleteData()
                }

                bindingAdapter.llCategoryGreen.setOnClickListener {
                    deleteData()
                }

                bindingAdapter.llCategoryBlue.setOnClickListener {
                    deleteData()
                }
            }

            private fun deleteData() {
                setData()
                if (catList.selectedCategoriesTitle.contains(listModel!![pos].view)) {
                    for (i in 0 until catList.selectedCategoriesTitle.size) {
                        if (catList.selectedCategoriesTitle[i] == listModel[pos].view) {
                            if (catList.selectedCategoriesName[i] == listModel[pos].details!![position].problemName) {
                                catList.selectedCategoriesTitle.removeAt(i)
                                catList.selectedCategoriesName.removeAt(i)
                                catList.editor = ctx.getSharedPreferences(
                                        CONSTANTS.RecommendedCatMain,
                                        MODE_PRIVATE
                                ).edit()
                                catList.editor.putString(
                                        CONSTANTS.selectedCategoriesTitle,
                                        catList.gson.toJson(catList.selectedCategoriesTitle)
                                ) //Friend
                                catList.editor.putString(
                                        CONSTANTS.selectedCategoriesName,
                                        catList.gson.toJson(catList.selectedCategoriesName)
                                ) //Friend
                                catList.editor.apply()
                                catList.editor.commit()
                                Log.e(
                                        "selectedCategoriesTitle",
                                        catList.selectedCategoriesTitle.toString()
                                )
                                Log.e(
                                        "selectedCategoriesName",
                                        catList.selectedCategoriesName.toString()
                                )
                                Log.e("posItem", posItem.toString())

                                val layoutManager = FlexboxLayoutManager(ctx)
                                layoutManager.flexWrap = FlexWrap.WRAP
                                layoutManager.alignItems = AlignItems.STRETCH
                                layoutManager.flexDirection = FlexDirection.ROW
                                layoutManager.justifyContent = JustifyContent.FLEX_START
                                binding.rvSelectedCategory.layoutManager = layoutManager
                                catList.catListadapter = SelectedCategory(
                                        binding,
                                        ctx,
                                        catList.selectedCategoriesName
                                )
                                binding.rvSelectedCategory.adapter = catList.catListadapter
                                binding.llError.visibility = View.GONE
                                binding.rvPerantCat.visibility = View.VISIBLE
                                catList.adapter1 =
                                        AllCategory(binding, listModel, ctx, activity)
                                binding.rvPerantCat.adapter = catList.adapter1
                                if(catList.selectedCategoriesTitle.size>0) {
                                    binding.btnContinue.isEnabled = true
                                    binding.btnContinue.isClickable = true
                                    binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
                                }else {
                                    binding.btnContinue.isEnabled = false
                                    binding.btnContinue.isClickable = false
                                    binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
                                }
                                break
                            }
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AllCatDataRawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.all_cat_data_raw,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            setData()
            if (catList.selectedCategoriesTitle.size != 0) {
                if (catList.selectedCategoriesTitle.contains(listModel!![pos].view)) {
                    for (i in 0 until catList.selectedCategoriesTitle.size) {
                        if (catList.selectedCategoriesTitle[i] == listModel[pos].view) {
                            if (catList.selectedCategoriesName[i] == responseListModel!![position].problemName) {
                                posItem = position
                                mSelectedItem = posItem
                                break
                            }
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
                    if (catList.selectedCategoriesName[0] == responseListModel!![position].problemName.toString() &&
                        catList.selectedCategoriesTitle[0] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llCategory.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    }
                } else if (catList.selectedCategoriesTitle.size == 2) {
                    if (catList.selectedCategoriesName[0] == responseListModel!![position].problemName.toString() &&
                        catList.selectedCategoriesTitle[0] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    } else if (catList.selectedCategoriesName[1] == responseListModel[position].problemName.toString() &&
                        catList.selectedCategoriesTitle[1] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llCategory.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    }
                } else if (catList.selectedCategoriesTitle.size == 3) {
                    if (catList.selectedCategoriesName[0] == responseListModel!![position].problemName.toString() &&
                        catList.selectedCategoriesTitle[0] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    } else if (catList.selectedCategoriesName[1] == responseListModel[position].problemName.toString() &&
                        catList.selectedCategoriesTitle[1] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    } else if (catList.selectedCategoriesName[2] == responseListModel[position].problemName.toString() &&
                        catList.selectedCategoriesTitle[2] == listModel!![pos].view
                    ) {
                        holder.bindingAdapter.llCategory.visibility = View.GONE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.VISIBLE
                    } else {
                        holder.bindingAdapter.llCategory.visibility = View.VISIBLE
                        holder.bindingAdapter.llCategoryPink.visibility = View.GONE
                        holder.bindingAdapter.llCategoryGreen.visibility = View.GONE
                        holder.bindingAdapter.llCategoryBlue.visibility = View.GONE
                    }
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
            val json5 = shared.getString(CONSTANTS.selectedCategoriesName, catList.gson.toString())
            catList.sleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
            if (!json2.equals(catList.gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
                catList.selectedCategoriesTitle = catList.gson.fromJson(json2, type1)
                catList.selectedCategoriesName = catList.gson.fromJson(json5, type1)
            }
            if(catList.selectedCategoriesTitle.size>0) {
                binding.btnContinue.isEnabled = true
                binding.btnContinue.isClickable = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }else {
                binding.btnContinue.isEnabled = false
                binding.btnContinue.isClickable = false
                binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun getItemCount(): Int {
            return listModel!![pos].details!!.size
        }
    }

    private fun getCatSaveData() {
        val shared = ctx!!.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.selectedCategoriesTitle, gson.toString())
        val json5 = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        sleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        if (!json2.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
            selectedCategoriesTitle = gson.fromJson(json2, type1)
            selectedCategoriesName = gson.fromJson(json5, type1)
        }
        if (selectedCategoriesTitle.size > 0) {
            val layoutManager = FlexboxLayoutManager(ctx)
            layoutManager.flexWrap = FlexWrap.WRAP
            layoutManager.alignItems = AlignItems.STRETCH
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.rvSelectedCategory.layoutManager = layoutManager
            catListadapter = SelectedCategory(binding, ctx!!, selectedCategoriesName)
            binding.rvSelectedCategory.adapter = catListadapter
            binding.btnContinue.isEnabled=true
            binding.btnContinue.isClickable=true
            binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
        }else{
            binding.btnContinue.isEnabled=false
            binding.btnContinue.isClickable=false
            binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
        }
    }

    class SelectedCategory(
        var binding: ActivityRecommendedCategoryBinding,
        var ctx: Context,
        var selectedCategoriesName: ArrayList<String>
    ) : RecyclerView.Adapter<SelectedCategory.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.selected_category_raw,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvCategory.text = selectedCategoriesName[position]
            holder.bindingAdapter.tvhours.text = (position + 1).toString()
            if (selectedCategoriesName.size == 3) {
                when (position) {
                    0 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                    }
                    1 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_green)
                    }
                    2 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_blue)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_blue)
                    }
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
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SaveRecommendedCatModel> =
                APINewClient.getClient().getSaveRecommendedCategory(coUserId, toJson, sleepTime)
            listCall.enqueue(object : Callback<SaveRecommendedCatModel> {
                override fun onResponse(
                    call: Call<SaveRecommendedCatModel>,
                    response: Response<SaveRecommendedCatModel>
                ) {
                    try {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: SaveRecommendedCatModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            val shared = getSharedPreferences(
                                CONSTANTS.RecommendedCatMain,
                                Context.MODE_PRIVATE
                            )
                            val editor = shared.edit()
                            editor.putString(
                                CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                listModel.responseData!!.avgSleepTime
                            )
                            editor.apply()

                            val i = Intent(activity, PreparePlaylistActivity::class.java)
                            i.putExtra("BackClick", backClick)
                            startActivity(i)
                            section = java.util.ArrayList<String>()
                            gsonBuilder = GsonBuilder()
                            val gson: Gson = gsonBuilder.create()
                            for (i in listModel.responseData!!.categoryData!!.indices) {
                                section.add(listModel.responseData!!.categoryData!!.get(i).mainCat.toString())
                                section.add(listModel.responseData!!.categoryData!!.get(i).recommendedCat.toString())
                            }
                            val p = Properties()
                            p.putValue("coUserId", coUserId)
                            p.putValue("avgSleepTime", listModel.responseData!!.avgSleepTime)
                            p.putValue("areaOfFocus", gson.toJson(section))
                            addToSegment("Area of Focus Savedd", p, CONSTANTS.track)
                            finish()
                        } else {
                            showToast(listModel.responseMessage, activity)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SaveRecommendedCatModel>, t: Throwable) {
                    hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onBackPressed() {
        when {
            backClick.equals("0", ignoreCase = true) -> {
                val i = Intent(ctx, SleepTimeActivity::class.java)
                i.putExtra("SleepTime", sleepTime)
                startActivity(i)
                finish()
            }
            backClick.equals("1", ignoreCase = true) -> {
                finish()
            }
            else -> {
                finish()
            }
        }
    }
}