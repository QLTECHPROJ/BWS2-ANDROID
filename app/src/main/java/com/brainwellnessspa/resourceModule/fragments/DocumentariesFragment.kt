package com.brainwellnessspa.resourceModule.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.DocumentariesListLayoutBinding
import com.brainwellnessspa.databinding.FragmentDocumentariesBinding
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

class DocumentariesFragment : Fragment() {
    lateinit var binding: FragmentDocumentariesBinding
    var documentaries: String? = ""
    var mainAccountID: String? = ""
    var userId: String? = ""
    private var mLastClickTime: Long = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documentaries, container, false)
        val view = binding.root
        val bundle = this.arguments
        if (bundle != null) {
            documentaries = bundle.getString("documentaries")
        }
        val shared1: SharedPreferences = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvDocumentariesList.layoutManager = mLayoutManager
        binding.rvDocumentariesList.itemAnimator = DefaultItemAnimator()
        return view
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    fun prepareData() {
        showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
        val listCall = APINewClient.client.getResourceList(userId, CONSTANTS.FLAG_TWO, category)
        listCall.enqueue(object : Callback<ResourceListModel?> {
            override fun onResponse(call: Call<ResourceListModel?>, response: Response<ResourceListModel?>) {
                try {
                    val listModel = response.body()
                    if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val adapter = DocumentariesAdapter(listModel.responseData, activity, documentaries)
                        binding.rvDocumentariesList.adapter = adapter
                        if (listModel.responseData!!.isNotEmpty()) {
                            binding.llError.visibility = View.GONE
                            binding.rvDocumentariesList.visibility = View.VISIBLE
                        } else {
                            binding.llError.visibility = View.VISIBLE
                            binding.rvDocumentariesList.visibility = View.GONE
                        }
                    } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                        callDelete403(activity, listModel.responseMessage)
                    } else if (listModel.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResourceListModel?>, t: Throwable) {
                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            }
        })
    }

    inner class DocumentariesAdapter(private val listModelList: List<ResourceListModel.ResponseData>?, var ctx: Context?, private var documentaries: String?) : RecyclerView.Adapter<DocumentariesAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DocumentariesListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.documentaries_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModelList!![position].title
            holder.binding.tvCreator.text = listModelList[position].author
            val measureRatio = measureRatio(ctx, 0f, 25f, 11f, 0.80f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx!!).load(listModelList[position].image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(30))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.rlMainLayout.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val i = Intent(activity, ResourceDetailsActivity::class.java)
                i.putExtra("documentaries", documentaries)
                i.putExtra("id", listModelList[position].iD)
                i.putExtra("title", listModelList[position].title)
                i.putExtra("author", listModelList[position].author)
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

        inner class MyViewHolder(var binding: DocumentariesListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}