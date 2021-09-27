package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionListModel
import com.brainwellnessspa.dashboardModule.models.SessionStepStatusListModel
import com.brainwellnessspa.databinding.FragmentSessionDetailBinding
import com.brainwellnessspa.databinding.SessionMainLayoutBinding
import com.brainwellnessspa.utility.APINewClient
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class SessionDetailFragment : Fragment() {
    lateinit var binding: FragmentSessionDetailBinding
    lateinit var adapter: SessionMainAdapter
    lateinit var ctx: Context
    lateinit var act: Activity
    var afterSession = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
        ctx = requireActivity()
        act = requireActivity()
        binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//        prepareData()
        networkCheck()
        return binding.root
    }

    override fun onResume() {
        networkCheck()
        super.onResume()
    }

    private fun networkCheck() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            binding.llRemainDev.visibility = View.VISIBLE /* VISIBLE*/
            binding.llNoInternet.visibility = View.GONE
        } else {
            binding.llRemainDev.visibility = View.GONE
            binding.llNoInternet.visibility = View.VISIBLE /* VISIBLE*/
        }
    }

    /* session_unselected_bg
    * session_selected_bg
    * session_idea_icon
    * ic_session_lock_icon*/
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getSessionList("1")
            listCall.enqueue(object : Callback<SessionListModel?> {
                override fun onResponse(call: Call<SessionListModel?>, response: Response<SessionListModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                binding.tvTitle.text = response.completionPercentage + "%"
                                binding.tvUpdated.text = response.completedSession + "/" + response.totalSession
                                adapter = SessionMainAdapter(binding, response.data!!, activity, afterSession)
                                binding.rvList.adapter = adapter
                                binding.pbProgress.progress = response.completionPercentage!!.toInt()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }

    fun prepareSessionStepStatusListData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getSessionStepStatusList("1", "1", "1")
            listCall.enqueue(object : Callback<SessionStepStatusListModel?> {
                override fun onResponse(call: Call<SessionStepStatusListModel?>, response: Response<SessionStepStatusListModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionStepStatusListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }

    class SessionMainAdapter(var binding: FragmentSessionDetailBinding, var catName: List<SessionListModel.ResponseData.Data>, val activity: FragmentActivity?, val afterSession: ArrayList<String>) : RecyclerView.Adapter<SessionMainAdapter.MyViewHolder>() {
        var catList = SessionDetailFragment()

        inner class MyViewHolder(var bindingAdapter: SessionMainLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_main_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvTitle.text = catName[position].title
            holder.bindingAdapter.tvSentData.text = catName[position].desc

            Glide.with(activity!!).load(catName[position].statusImg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
                    .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivIcon)

            if (catName[position].sessionDate.equals("", ignoreCase = true)) {
                holder.bindingAdapter.tvDate.visibility = View.GONE
                holder.bindingAdapter.ivDate.visibility = View.GONE
            } else {
                holder.bindingAdapter.tvDate.visibility = View.VISIBLE
                holder.bindingAdapter.ivDate.visibility = View.VISIBLE
                holder.bindingAdapter.tvDate.text = catName[position].sessionDate
            }

            if (catName[position].sessionDate.equals("") && catName[position].sessionTime.equals("")) {
                holder.bindingAdapter.llDateTime.visibility = View.GONE
            } else {
                holder.bindingAdapter.llDateTime.visibility = View.VISIBLE
            }

            if (catName[position].preSessionAudioTitle.equals("") && catName[position].bookletTitle.equals("")) {
                holder.bindingAdapter.llStatus.visibility = View.GONE
            } else {
                holder.bindingAdapter.llStatus.visibility = View.VISIBLE
            }
            if (catName[position].sessionTime.equals("", ignoreCase = true)) {
                holder.bindingAdapter.tvTime.visibility = View.GONE
                holder.bindingAdapter.ivTime.visibility = View.GONE
            } else {
                holder.bindingAdapter.tvTime.visibility = View.VISIBLE
                holder.bindingAdapter.ivTime.visibility = View.VISIBLE
                holder.bindingAdapter.tvTime.text = catName[position].sessionTime
            }

            if (catName[position].preSessionAudioTitle.equals("", ignoreCase = true)) {
                holder.bindingAdapter.tvAudio.visibility = View.GONE
                holder.bindingAdapter.ivAudio.visibility = View.GONE
            } else {
                holder.bindingAdapter.tvAudio.visibility = View.VISIBLE
                holder.bindingAdapter.ivAudio.visibility = View.VISIBLE

                if (catName[position].preSessionAudioStatus.equals("0")) {
                    holder.bindingAdapter.ivAudio.setImageResource(R.drawable.ic_square_unchecked_icon)
                } else {
                    holder.bindingAdapter.ivAudio.setImageResource(R.drawable.ic_square_checked_icon)
                }
                holder.bindingAdapter.tvAudio.text = catName[position].preSessionAudioTitle
            }

            if (catName[position].bookletTitle.equals("", ignoreCase = true)) {
                holder.bindingAdapter.tvBooklet.visibility = View.GONE
                holder.bindingAdapter.ivBooklet.visibility = View.GONE
            } else {
                holder.bindingAdapter.tvBooklet.visibility = View.VISIBLE
                holder.bindingAdapter.ivBooklet.visibility = View.VISIBLE

                if (catName[position].bookletStatus.equals("0")) {
                    holder.bindingAdapter.ivBooklet.setImageResource(R.drawable.ic_square_unchecked_icon)
                } else {
                    holder.bindingAdapter.ivBooklet.setImageResource(R.drawable.ic_square_checked_icon)
                }
                holder.bindingAdapter.tvBooklet.text = catName[position].bookletTitle
            }

            when {
                catName[position].userSessionStatus.equals("Completed") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.tvLabel.visibility = View.GONE
                    holder.bindingAdapter.ivBanner.visibility = View.GONE
                    holder.bindingAdapter.llAfterSession.visibility = View.VISIBLE
                    holder.bindingAdapter.llBeforeSession.visibility = View.VISIBLE
                }
                catName[position].userSessionStatus.equals("InProgress") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_selected_bg)
                    holder.bindingAdapter.tvLabel.visibility = View.VISIBLE
                    holder.bindingAdapter.ivBanner.visibility = View.VISIBLE
                    holder.bindingAdapter.llAfterSession.visibility = View.VISIBLE
                    holder.bindingAdapter.llBeforeSession.visibility = View.VISIBLE
                }
                catName[position].userSessionStatus.equals("Lock") -> {
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.tvLabel.visibility = View.GONE
                    holder.bindingAdapter.ivBanner.visibility = View.GONE
                    holder.bindingAdapter.llAfterSession.visibility = View.GONE
                    holder.bindingAdapter.llBeforeSession.visibility = View.GONE
                }
            }

            holder.bindingAdapter.rlNext.setOnClickListener {
                when {
                    catName[position].userSessionStatus.equals("Completed") -> {
                        val i = Intent(activity, SessionDetailContinueActivity::class.java)
                        activity.startActivity(i)
                    }
                    catName[position].userSessionStatus.equals("InProgress") -> {
                        val i = Intent(activity, SessionDetailContinueActivity::class.java)
                        activity.startActivity(i)
                    }
                    catName[position].userSessionStatus.equals("Lock") -> {
                        BWSApplication.showToast("Please complete above session first", activity)
                    }
                }
            }
            val beforeText = arrayListOf<String>()
            for (i in catName[position].beforeSession!!.indices) {
                beforeText.add("<font color='" + catName[position].beforeSession!![i].color + "'>" + catName[position].beforeSession!![i].key +/* "," + */"</font>")
            }
            val afterText = arrayListOf<String>()
            for (i in catName[position].afterSession!!.indices) {
                afterText.add("<font color='" + catName[position].afterSession!![i].color + "'>" + catName[position].afterSession!![i].key + /*"," +*/ "</font>")
            }
            val s = TextUtils.join(",", beforeText)
            val s1 = TextUtils.join(",", afterText)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.bindingAdapter.tvBeforeSession.text = Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT)
                holder.bindingAdapter.tvAfterSession.text = Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT)
            } else {
                holder.bindingAdapter.tvBeforeSession.text = Html.fromHtml(s)
                holder.bindingAdapter.tvAfterSession.text = Html.fromHtml(s1)
            }

            if (catName.size == 1) {
                holder.bindingAdapter.viewDown.visibility = View.GONE
            } else {
                holder.bindingAdapter.viewDown.visibility = View.VISIBLE
            }

            if (position == (catName.size - 1)) {
                holder.bindingAdapter.viewDown.visibility = View.GONE;
            }

            if (position == 0) {
                holder.bindingAdapter.viewUp.visibility = View.GONE;
            }
        }

        override fun getItemCount(): Int {
            return catName.size
        }
    }
}