package com.brainwellnessspa.ManageModule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BillingOrderModule.Activities.CancelMembershipActivity
import com.brainwellnessspa.DashboardTwoModule.Model.PlanlistInappModel
import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity
import com.brainwellnessspa.MembershipModule.Adapters.SubscriptionAdapter
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityManageBinding
import com.brainwellnessspa.databinding.MembershipFaqLayoutBinding
import com.brainwellnessspa.databinding.PlanListFilteredLayoutBinding
import com.brainwellnessspa.databinding.VideoSeriesBoxLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ManageActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    lateinit var binding: ActivityManageBinding
    lateinit var adapter: MembershipFaqAdapter
    lateinit var subscriptionAdapter: SubscriptionAdapter
    lateinit var videoListAdapter: VideoListAdapter
    lateinit var planListAdapter: PlanListAdapter
    lateinit var activity: Activity
    var USERID: String? = null
    var CoUserID: String? = null
    var value: Int = 0
    var step = 1
    var min = 1

    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage)
        activity = this@ManageActivity
        val shared1: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        ctx = this@ManageActivity
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnFreeJoin.setOnClickListener {
            val i = Intent(ctx, OrderSummaryActivity::class.java)
            startActivity(i)
        }

        binding.simpleSeekbar.progress = 1
        binding.simpleSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progresValue: Int, fromUser: Boolean) {
                value = min + progresValue * step
                binding.tvNoOfPerson.setText(value.toString())
                Log.e("ValueOf", value.toString())
                planListAdapter.getFilter().filter(value.toString())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onResume() {
        prepareUserData()
        super.onResume()
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlanlistInappModel> =
                APINewClient.getClient().getPlanlistInapp(CoUserID)
            listCall.enqueue(object : Callback<PlanlistInappModel> {
                override fun onResponse(
                    call: Call<PlanlistInappModel>,
                    response: Response<PlanlistInappModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: PlanlistInappModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            binding.nestedScroll.isSmoothScrollingEnabled = true
                            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                            binding.ivRestaurantImage.layoutParams.height =
                                (measureRatio.height * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.layoutParams.width =
                                (measureRatio.widthImg * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                            Glide.with(ctx).load(listModel.responseData!!.image).thumbnail(0.05f)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
                                .priority(Priority.HIGH)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                                .into(binding.ivRestaurantImage)

                            binding.tvTitle.text = listModel.responseData!!.title
                            binding.tvDesc.text = listModel.responseData!!.desc
                            binding.tvPlanFeatures01.text =
                                listModel.responseData!!.planFeatures!![0].feature
                            binding.tvPlanFeatures02.text =
                                listModel.responseData!!.planFeatures!![1].feature
                            binding.tvPlanFeatures03.text =
                                listModel.responseData!!.planFeatures!![2].feature
                            binding.tvPlanFeatures04.text =
                                listModel.responseData!!.planFeatures!![3].feature

                            binding.tvFreeTrial.text = listModel.responseData!!.plan!![0].freeTrial

                            binding.rvList.layoutManager =
                                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                            subscriptionAdapter = SubscriptionAdapter(
                                listModel.responseData!!.audioFiles, ctx
                            )
                            binding.rvList.adapter = subscriptionAdapter

                            binding.rvVideoList.layoutManager =
                                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                            videoListAdapter = VideoListAdapter(
                                listModel.responseData!!.testminialVideo!!, ctx
                            )
                            binding.rvVideoList.adapter = videoListAdapter

                            binding.rvPlanList.layoutManager = LinearLayoutManager(activity)
                            planListAdapter = PlanListAdapter(
                                listModel.responseData!!.plan!!, ctx
                            )
                            binding.rvPlanList.adapter = planListAdapter

                            binding.rvFaqList.layoutManager =
                                LinearLayoutManager(this@ManageActivity)
                            adapter = MembershipFaqAdapter(
                                listModel.responseData!!.fAQs!!,
                                ctx,
                                binding.rvFaqList,
                                binding.tvFound
                            )
                            binding.rvFaqList.adapter = adapter
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanlistInappModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }


    class VideoListAdapter(
        private val listModelList: List<PlanlistInappModel.ResponseData.TestminialVideo>,
        var ctx: Context
    ) :
        RecyclerView.Adapter<VideoListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: VideoSeriesBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.video_series_box_layout, parent, false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//            val mediaController = MediaController(ctx)
//            mediaController.setAnchorView(holder.binding.videoView)

            //specify the location of media file

            //specify the location of media file
//            val uri: Uri =
//                Uri.parse(listModelList[position].videoLink)

            //Setting MediaController and URI, then starting the videoView

            //Setting MediaController and URI, then starting the videoView
//            holder.binding.videoView.setMediaController(mediaController)
//            holder.binding.videoView.setVideoURI(uri)
//            holder.binding.videoView.requestFocus()
//            holder.binding.videoView.start()
            holder.binding.tvHeadingTwo.text = listModelList[position].videoDesc
            holder.binding.tvName.text = listModelList[position].userName
            holder.binding.tvReadMore.setVisibility(View.GONE)
            /*   val linecount: Int = holder.binding.tvHeadingTwo.getLineCount()
               if (linecount >= 4) {
                   holder.binding.tvReadMore.setVisibility(View.VISIBLE)
               } else {
                   holder.binding.tvReadMore.setVisibility(View.GONE)
               }*/

            holder.binding.tvReadMore.setOnClickListener { v12: View? ->
                val dialog1 = Dialog(ctx)
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog1.setContentView(R.layout.full_desc_layout)
                dialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog1.window!!
                    .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                val tvDesc = dialog1.findViewById<TextView>(R.id.tvDesc)
                val tvClose = dialog1.findViewById<RelativeLayout>(R.id.tvClose)
                tvDesc.setText(listModelList[position].videoDesc)
                dialog1.setOnKeyListener { v3: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog1.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                tvClose.setOnClickListener { v14: View? -> dialog1.dismiss() }
                dialog1.show()
                dialog1.setCancelable(false)
            }

            /* binding.youtubeView.initialize(CancelMembershipActivity.API_KEY, this) */

            fun getYouTubePlayerProvider(): YouTubePlayer.Provider {
                return holder.binding.youtubeView
            }
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(binding: VideoSeriesBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.getRoot()) {
            var binding: VideoSeriesBoxLayoutBinding

            init {
                this.binding = binding
            }
        }
    }

    class PlanListAdapter(
        private val listModelList: List<PlanlistInappModel.ResponseData.Plan>,
        var ctx: Context
    ) :
        RecyclerView.Adapter<PlanListAdapter.MyViewHolder>(), Filterable {

        private var listData: List<PlanlistInappModel.ResponseData.Plan> = listModelList
        private var listFilterData: List<PlanlistInappModel.ResponseData.Plan> = listModelList
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanListFilteredLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.plan_list_filtered_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        /*{"PlanPosition":"1","ProfileCount":"3","PlanID":"5","PlanAmount":"14.99",
        "PlanCurrency":"Aus","PlanInterval":"Weekly","PlanImage":"",
        "PlanTenure":"1 Week","PlanNextRenewal":"17 May, 2021",
        "FreeTrial":"TRY 14 DAYS FOR FREE","SubName":"Week \/ Per 3 User","RecommendedFlag":"0","PlanFlag":"1"}*/
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTilte.text = listFilterData[position].planInterval
            holder.binding.tvContent.text = listFilterData[position].subName
            holder.binding.tvAmount.text = listFilterData[position].planAmount

            if (listFilterData[position].recommendedFlag.equals("1", ignoreCase = true)) {
                holder.binding.rlMostPopular.visibility = View.VISIBLE
            } else {
                holder.binding.rlMostPopular.visibility = View.INVISIBLE
            }
        }

        override fun getItemCount(): Int {
            return listFilterData.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val filterResults = FilterResults()
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        listFilterData = listData
                    } else {
                        val filteredList: MutableList<PlanlistInappModel.ResponseData.Plan> =
                            ArrayList()
                        for (row in listData) {
                            if (row.profileCount!!.toLowerCase(Locale.getDefault())
                                    .contains(charString.toLowerCase(Locale.getDefault()))
                            ) {
                                filteredList.add(row)
                            }
                        }
                        listFilterData = filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    if (listFilterData.size == 0) {
//                        binding.tvFound.setVisibility(View.VISIBLE)
//                        binding.tvFound.setText("Couldn't find $searchFilter. Try searching again")
//                        binding.rvCountryList.setVisibility(View.GONE)
                    } else {
//                        binding.tvFound.setVisibility(View.GONE)
//                        binding.rvCountryList.setVisibility(View.VISIBLE)
                        listFilterData =
                            filterResults.values as List<PlanlistInappModel.ResponseData.Plan>
                        notifyDataSetChanged()
                    }
                }
            }
        }

        inner class MyViewHolder(binding: PlanListFilteredLayoutBinding) :
            RecyclerView.ViewHolder(binding.getRoot()) {
            var binding: PlanListFilteredLayoutBinding

            init {
                this.binding = binding
            }
        }
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(CancelMembershipActivity.VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        errorReason: YouTubeInitializationResult
    ) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, CancelMembershipActivity.RECOVERY_DIALOG_REQUEST)
                .show()
        } else {
            val errorMessage = String.format(
                getString(R.string.error_player), errorReason.toString()
            )
            BWSApplication.showToast(errorMessage, activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CancelMembershipActivity.RECOVERY_DIALOG_REQUEST) {
//            getYouTubePlayerProvider().initialize(CancelMembershipActivity.API_KEY, this)
        }
    }

    class MembershipFaqAdapter(
        private val modelList: List<PlanlistInappModel.ResponseData.Faq>,
        var ctx: Context,
        var rvFaqList: RecyclerView,
        var tvFound: TextView
    ) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipFaqLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.membership_faq_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(modelList[position].title)
            holder.binding.tvDesc.setText(modelList[position].desc)
            holder.binding.ivClickRight.setOnClickListener { _ ->
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray)
                holder.binding.tvDesc.setFocusable(true)
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.setVisibility(View.VISIBLE)
                holder.binding.ivClickRight.setVisibility(View.GONE)
                holder.binding.ivClickDown.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon)
            }
            holder.binding.ivClickDown.setOnClickListener { _ ->
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.membership_faq_not_clicked)
                holder.binding.tvDesc.setVisibility(View.GONE)
                holder.binding.ivClickRight.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setVisibility(View.GONE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon)
            }
            if (modelList.size == 0) {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.GONE
            } else {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(binding: MembershipFaqLayoutBinding) :
            RecyclerView.ViewHolder(binding.getRoot()) {
            var binding: MembershipFaqLayoutBinding

            init {
                this.binding = binding
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}