package com.brainwellnessspa.ResourceModule.Fragments

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.ResourceModule.Activities.ResourceDetailsActivity
import com.brainwellnessspa.ResourceModule.Models.ResourceListModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.AudioBooksLayoutBinding
import com.brainwellnessspa.databinding.FragmentAudioBooksBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AudioBooksFragment : Fragment() {
    lateinit var binding: FragmentAudioBooksBinding
    var audio_books: String? = null
    var USERID: String? = null
    var CoUserID: String? = null
    var Category: String? = null
    private var mLastClickTime: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_books, container, false)
        val view = binding.root
        val bundle = this.arguments
        if (bundle != null) {
            audio_books = bundle.getString("audio_books")
            Category = bundle.getString("Category")
        }
        val shared1: SharedPreferences =
            requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        val manager = GridLayoutManager(activity, 2)
        binding.rvAudioBooksList.layoutManager = manager
        binding.rvAudioBooksList.itemAnimator = DefaultItemAnimator()
        return view
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    fun prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
        val listCall =
            APINewClient.getClient().getResourceList(CoUserID, CONSTANTS.FLAG_ONE, Category)
        listCall.enqueue(object : Callback<ResourceListModel?> {
            override fun onResponse(
                call: Call<ResourceListModel?>,
                response: Response<ResourceListModel?>
            ) {
                try {
                    val listModel = response.body()
                    if (listModel!!.responseCode.equals(
                            getString(R.string.ResponseCodesuccess),
                            ignoreCase = true
                        )
                    ) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val adapter =
                            AudioBooksAdapter(listModel.responseData, activity, audio_books)
                        binding.rvAudioBooksList.adapter = adapter
                        if (listModel.responseData!!.isNotEmpty()) {
                            binding.llError.visibility = View.GONE
                            binding.rvAudioBooksList.visibility = View.VISIBLE
                        } else {
                            binding.llError.visibility = View.VISIBLE
                            binding.rvAudioBooksList.visibility = View.GONE
                        }
                    } else if (listModel.responseCode.equals(
                            getString(R.string.ResponseCodefail),
                            ignoreCase = true
                        )
                    ) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResourceListModel?>, t: Throwable) {
                BWSApplication.hideProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    activity
                )
            }
        })
    }

    inner class AudioBooksAdapter(
        var listModelList: List<ResourceListModel.ResponseData>?,
        var ctx: Context?,
        var audio_books: String?
    ) : RecyclerView.Adapter<AudioBooksAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudioBooksLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.audio_books_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.42f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvTitle.text = listModelList!![position].title
            holder.binding.tvCreator.text = listModelList!![position].author
            Glide.with(ctx!!).load(listModelList!![position].image).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(40))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)
            holder.binding.rlMainLayout.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val i = Intent(activity, ResourceDetailsActivity::class.java)
                i.putExtra("audio_books", audio_books)
                i.putExtra("id", listModelList!![position].iD)
                i.putExtra("title", listModelList!![position].title)
                i.putExtra("author", listModelList!![position].author)
                i.putExtra("linkOne", listModelList!![position].resourceLink1)
                i.putExtra("linkTwo", listModelList!![position].resourceLink2)
                i.putExtra("image", listModelList!![position].detailimage)
                i.putExtra("description", listModelList!![position].description)
                i.putExtra("mastercat", listModelList!![position].masterCategory)
                i.putExtra("subcat", listModelList!![position].subCategory)
                startActivity(i)
                activity!!.overridePendingTransition(0, 0)
            }
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: AudioBooksLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}