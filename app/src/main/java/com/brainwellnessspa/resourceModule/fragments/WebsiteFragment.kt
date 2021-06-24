package com.brainwellnessspa.resourceModule.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentWebsiteBinding
import com.brainwellnessspa.databinding.WebsiteListLayoutBinding
import com.brainwellnessspa.resourceModule.activities.ResourceDetailsActivity
import com.brainwellnessspa.resourceModule.models.ResourceListModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebsiteFragment : Fragment() {
    lateinit var binding: FragmentWebsiteBinding
    var website: String? = ""
    var USERID: String? = ""
    var CoUserID: String? = ""
    var Category: String? = ""
    private var mLastClickTime: Long = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_website, container, false)
        val view = binding.root
        val bundle = this.arguments
        if (bundle != null) {
            website = bundle.getString("website")
            Category = bundle.getString("Category")
        }
        val shared1: SharedPreferences = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvWebsiteList.layoutManager = mLayoutManager
        binding.rvWebsiteList.itemAnimator = DefaultItemAnimator()
        return view
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    fun prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
        val listCall = APINewClient.getClient().getResourceList(CoUserID, CONSTANTS.FLAG_FOUR, Category)
        listCall.enqueue(object : Callback<ResourceListModel?> {
            override fun onResponse(call: Call<ResourceListModel?>, response: Response<ResourceListModel?>) {
                try {
                    val listModel = response.body()
                    if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val adapter = WebsiteAdapter(listModel.responseData, activity, website)
                        binding.rvWebsiteList.adapter = adapter
                        if (listModel.responseData!!.isNotEmpty()) {
                            binding.llError.visibility = View.GONE
                            binding.rvWebsiteList.visibility = View.VISIBLE
                        } else {
                            binding.llError.visibility = View.VISIBLE
                            binding.rvWebsiteList.visibility = View.GONE
                        }
                    } else if (listModel.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResourceListModel?>, t: Throwable) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            }
        })
    }

    inner class WebsiteAdapter(private val listModelList: List<ResourceListModel.ResponseData>?, var ctx: Context?, var podcasts: String?) : RecyclerView.Adapter<WebsiteAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: WebsiteListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.website_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModelList!![position].title
            if (!listModelList[position].description.equals("", ignoreCase = true)) {
                holder.binding.tvCreator.visibility = View.VISIBLE
                holder.binding.tvCreator.text = listModelList[position].description
            } else {
                holder.binding.tvCreator.visibility = View.GONE
            }
            Glide.with(ctx!!).load(listModelList[position].image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(12))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.rlMainLayout.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val i = Intent(activity, ResourceDetailsActivity::class.java)
                i.putExtra("website", website)
                i.putExtra("id", listModelList[position].iD)
                i.putExtra("title", listModelList[position].title)
                i.putExtra("linkOne", listModelList[position].resourceLink1)
                i.putExtra("linkTwo", listModelList[position].resourceLink2)
                i.putExtra("image", listModelList[position].detailimage)
                i.putExtra("description", listModelList[position].description)
                i.putExtra("mastercat", listModelList[position].masterCategory)
                i.putExtra("subcat", listModelList[position].subCategory)
                startActivity(i)
                activity!!.overridePendingTransition(0, 0)
            }
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: WebsiteListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}