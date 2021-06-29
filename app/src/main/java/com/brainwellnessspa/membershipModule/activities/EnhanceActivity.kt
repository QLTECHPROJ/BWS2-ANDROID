package com.brainwellnessspa.membershipModule.activities

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
import android.webkit.WebSettings
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.membershipModule.adapters.SubscriptionAdapter
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.webView.TncActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EnhanceActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceBinding
    lateinit var adapter: MembershipFaqAdapter
    lateinit var subscriptionAdapter: SubscriptionAdapter
    lateinit var planListAdapter: PlanListAdapter
    lateinit var activity: Activity
    lateinit var i: Intent
    var userId: String? = ""
    var coUserId: String? = ""
    var listModelGlobal: PlanlistInappModel? = null
    var value: Int = 2
    var step = 1
    var min = 1
    var planFlag: String = ""
    var planId: String = ""
    var price: String = ""
    var listModelList = arrayListOf<PlanlistInappModel.ResponseData.Plan>()
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance)
        activity = this@EnhanceActivity
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        ctx = this@EnhanceActivity

        val p = Properties()
        BWSApplication.addToSegment("Manage Plan Screen Viewed", p, CONSTANTS.screen)

        binding.rvPlanList.layoutManager = LinearLayoutManager(activity)
        i = Intent(ctx, OrderSummaryActivity::class.java)
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnFreeJoin.setOnClickListener {
            startActivity(i)
        }

        binding.tvtncs.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.putExtra(CONSTANTS.Web, "Tnc")
            startActivity(i)
        }

        binding.tvPrivacyPolicys.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.putExtra(CONSTANTS.Web, "PrivacyPolicy")
            startActivity(i)
        }

        binding.tvDisclaimers.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(660, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvTitle.setText(R.string.Disclaimer)
            tvDesc.setText(R.string.Disclaimer_text)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(true)
        }

        binding.simpleSeekbar.progress = 1
        binding.simpleSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progresValue: Int, fromUser: Boolean) {
                if (progresValue >= 1) {
                    value = min + progresValue * step
                    binding.tvNoOfPerson.text = value.toString()
                    Log.e("ValueOf", value.toString())
                    listModelList.clear()
                    for (i1 in listModelGlobal!!.responseData!!.plan!!.indices) {
                        if (listModelGlobal!!.responseData!!.plan!![i1].profileCount!! == value.toString()) {
                            listModelList.add(listModelGlobal!!.responseData!!.plan!![i1])
                        }
                    }
                    planListAdapter = PlanListAdapter(listModelList, ctx, i)
                    binding.rvPlanList.adapter = planListAdapter //                    planListAdapter.filter.filter(value.toString())
                } else {
                    binding.simpleSeekbar.progress = 1
                    value = min + 1 * step
                    binding.tvNoOfPerson.text = value.toString()
                    Log.e("ValueOf", value.toString())
                    listModelList.clear()
                    for (i1 in listModelGlobal!!.responseData!!.plan!!.indices) {
                        if (listModelGlobal!!.responseData!!.plan!![i1].profileCount!! == value.toString()) {
                            listModelList.add(listModelGlobal!!.responseData!!.plan!![i1])
                        }
                    }
                    planListAdapter = PlanListAdapter(listModelList, ctx, i)
                    binding.rvPlanList.adapter = planListAdapter
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        prepareUserData()
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlanlistInappModel> = APINewClient.client.getPlanlistInapp(coUserId)
            listCall.enqueue(object : Callback<PlanlistInappModel> {
                override fun onResponse(call: Call<PlanlistInappModel>, response: Response<PlanlistInappModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: PlanlistInappModel = response.body()!!
                        listModelGlobal = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            binding.nestedScroll.isSmoothScrollingEnabled = true
                            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
                            binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                            binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                            Glide.with(ctx).load(listModel.responseData!!.image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage)
                            listModelList.clear()
                            for (i1 in listModelGlobal!!.responseData!!.plan!!.indices) {
                                if (listModelGlobal!!.responseData!!.plan!![i1].profileCount!! == value.toString()) {
                                    listModelList.add(listModelGlobal!!.responseData!!.plan!![i1])
                                }
                            }

                            planListAdapter = PlanListAdapter(listModelList, ctx, i)
                            binding.rvPlanList.adapter = planListAdapter
                            binding.tvTitle.text = listModel.responseData!!.title
                            binding.tvDesc.text = listModel.responseData!!.desc
                            binding.tvPlanFeatures01.text = listModel.responseData!!.planFeatures!![0].feature
                            binding.tvPlanFeatures02.text = listModel.responseData!!.planFeatures!![1].feature
                            binding.tvPlanFeatures03.text = listModel.responseData!!.planFeatures!![2].feature
                            binding.tvPlanFeatures04.text = listModel.responseData!!.planFeatures!![3].feature

                            binding.tvFreeTrial.text = listModel.responseData!!.plan!![0].freeTrial

                            binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                            subscriptionAdapter = listModel.responseData!!.audioFiles?.let {
                                SubscriptionAdapter(it, ctx)
                            }!!
                            binding.rvList.adapter = subscriptionAdapter

                            binding.rvVideoList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                            val videoListAdapter = VideoSeriesListAdapter(listModel.responseData!!.testminialVideo!!, ctx)
                            binding.rvVideoList.adapter = videoListAdapter

                            binding.rvFaqList.layoutManager = LinearLayoutManager(this@EnhanceActivity)
                            adapter = MembershipFaqAdapter(listModel.responseData!!.fAQs!!, ctx, binding.rvFaqList, binding.tvFound)
                            binding.rvFaqList.adapter = adapter //                            planListAdapter.filter.filter(value.toString())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PlanlistInappModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class VideoSeriesListAdapter(private val model: List<PlanlistInappModel.ResponseData.TestminialVideo>, var ctx: Context) : RecyclerView.Adapter<VideoSeriesListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: VideoSeriesBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.video_series_box_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetJavaScriptEnabled") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind.tvHeadingTwo.text = model[position].videoDesc
            holder.bind.tvName.text = model[position].userName
            val videoUrl = model[position].videoLink!!.split("=").toTypedArray()
            val myYouTubeVideoUrl = "https://www.youtube.com/embed/" + videoUrl[1]
            val webSettings: WebSettings = holder.bind.webView.settings
            webSettings.javaScriptEnabled = true
            holder.bind.webView.settings.loadWithOverviewMode = true
            holder.bind.webView.settings.useWideViewPort = true
            holder.bind.webView.loadUrl(myYouTubeVideoUrl)
        }

        override fun getItemCount(): Int {
            return model.size
        }

        inner class MyViewHolder(var bind: VideoSeriesBoxLayoutBinding) : RecyclerView.ViewHolder(bind.root)
    }

    class PlanListAdapter(var listModelList: List<PlanlistInappModel.ResponseData.Plan>, var ctx: Context, var i: Intent) : RecyclerView.Adapter<PlanListAdapter.MyViewHolder>()/*, Filterable */ {
        private var rowIndex: Int = -1
        private var pos: Int = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlanListFilteredLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.plan_list_filtered_layout, parent, false)
            return MyViewHolder(v)
        }

        /*{"PlanPosition":"1","ProfileCount":"3","PlanID":"5","PlanAmount":"14.99",
        "PlanCurrency":"Aus","PlanInterval":"Weekly","PlanImage":"",
        "PlanTenure":"1 Week","PlanNextRenewal":"17 May, 2021",
        "FreeTrial":"TRY 14 DAYS FOR FREE","SubName":"Week \/ Per 3 User","RecommendedFlag":"0","PlanFlag":"1"}*/
        @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTilte.text = listModelList[position].planInterval
            holder.binding.tvContent.text = listModelList[position].subName
            holder.binding.tvAmount.text = "$" + listModelList[position].planAmount

            if (listModelList[position].recommendedFlag.equals("1", ignoreCase = true)) {
                holder.binding.rlMostPopular.visibility = View.VISIBLE
            } else {
                holder.binding.rlMostPopular.visibility = View.INVISIBLE
            }
            holder.binding.llPlanMain.setOnClickListener {
                rowIndex = position
                pos++
                notifyDataSetChanged()
            }
            if (rowIndex == position) {
                changeFunction(holder, listModelList, position)
            } else {
                if (listModelList[position].recommendedFlag.equals("1", ignoreCase = true) && pos == 0) {
                    holder.binding.rlMostPopular.visibility = View.VISIBLE
                    changeFunction(holder, listModelList, position)
                } else {
                    holder.binding.llPlanMain.background = ContextCompat.getDrawable(ctx, R.drawable.light_gray_round_cornors)
                    holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                }
            }

        }

        private fun changeFunction(holder: MyViewHolder, listModelList: List<PlanlistInappModel.ResponseData.Plan>, position: Int) {
            holder.binding.llPlanMain.background = ContextCompat.getDrawable(ctx, R.drawable.light_sky_round_cornors)
            holder.binding.tvTilte.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            holder.binding.tvContent.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            val gson = Gson()
            i.putExtra("PlanData", gson.toJson(listModelList))
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            i.putExtra("Promocode", "")
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        /*     override fun getFilter(): Filter {
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
             }*/

        inner class MyViewHolder(var binding: PlanListFilteredLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    class MembershipFaqAdapter(private val modelList: List<PlanlistInappModel.ResponseData.Faq>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = modelList[position].title
            holder.binding.tvDesc.text = modelList[position].desc

            holder.binding.ivClickRight.setOnClickListener {
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                holder.binding.tvDesc.isFocusable = true
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.visibility = View.VISIBLE
                holder.binding.ivClickRight.visibility = View.GONE
                holder.binding.ivClickDown.visibility = View.VISIBLE
                holder.binding.llBgChange.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_clicked)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_white_arrow_down_icon)
            }

            holder.binding.ivClickDown.setOnClickListener {
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.faq_not_clicked)
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(ctx, R.color.light_black))
                holder.binding.tvDesc.visibility = View.GONE
                holder.binding.ivClickRight.visibility = View.VISIBLE
                holder.binding.ivClickDown.visibility = View.GONE
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_right_gray_arrow_icon)
            }

            if (modelList.isEmpty()) {
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

        inner class MyViewHolder(var binding: MembershipFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    override fun onBackPressed() {
        finish()
    }
}