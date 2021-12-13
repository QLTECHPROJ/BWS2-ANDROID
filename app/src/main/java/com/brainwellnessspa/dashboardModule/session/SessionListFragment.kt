package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionListModel
import com.brainwellnessspa.databinding.FragmentSessionListBinding
import com.brainwellnessspa.databinding.SessionMainLayoutBinding
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

class SessionListFragment : Fragment() {
    lateinit var binding: FragmentSessionListBinding
    lateinit var adapter: SessionMainAdapter
    lateinit var ctx: Context
    lateinit var act: Activity
    var userId: String = ""
    var afterSession = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_list, container, false)
        ctx = requireActivity()
        act = requireActivity()
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        prepareData()
        networkCheck()
        return binding.root
    }

    override fun onResume() {
        prepareData()
        networkCheck()
        super.onResume()
    }

    private fun networkCheck() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            binding.llRemainDev.visibility = View.GONE /* VISIBLE*/
            binding.llNoInternet.visibility = View.GONE
        } else {
            binding.llRemainDev.visibility = View.GONE
            binding.llNoInternet.visibility = View.GONE /* VISIBLE*/
        }
    }

    fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getSessionList(userId)
            listCall.enqueue(object : Callback<SessionListModel?> {
                override fun onResponse(call: Call<SessionListModel?>, response: Response<SessionListModel?>) {
                    try {
                        val listModel = response.body()
                        val response = listModel?.responseData
                        if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            if (response != null) {
                                binding.tvTitle.text = response.completionPercentage + "%"
                                binding.tvUpdated.text = response.completedSession + " / " + response.totalSession
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


    class SessionMainAdapter(var binding: FragmentSessionListBinding, var catName: List<SessionListModel.ResponseData.Data>, val activity: FragmentActivity?, val afterSession: ArrayList<String>) : RecyclerView.Adapter<SessionMainAdapter.MyViewHolder>() {

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
                    holder.bindingAdapter.ivArrow.setColorFilter(ContextCompat.getColor(activity,R.color.black), PorterDuff.Mode.SRC_IN)
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.viewDown.setBackgroundResource(R.drawable.session_viewer_line_down)
                    if (catName.size != 1) {
                        if (position <(catName.size - 1)) {
                            if(!catName[position + 1].userSessionStatus.equals("Lock")){
                                holder.bindingAdapter.viewUp.setBackgroundResource(R.drawable.session_viewer_line_up)
                            }else{
                                holder.bindingAdapter.viewUp.setBackgroundResource(R.drawable.session_viewer_gray_line_up)
                            }
                        }
                    }

//                    Glide.with(activity).load(R.drawable.session_done_icon).thumbnail(0.05f)
//                            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
//                            .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivIcon)
                    holder.bindingAdapter.tvLabel.visibility = View.GONE
                    holder.bindingAdapter.ivBanner.visibility = View.GONE
                    if (catName[position].beforeSession!!.isEmpty()) {
                        holder.bindingAdapter.llBeforeSession.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llBeforeSession.visibility = View.VISIBLE
                    }
                    if (catName[position].afterSession!!.isEmpty()) {
                        holder.bindingAdapter.llAfterSession.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llAfterSession.visibility = View.VISIBLE
                    }
                }
                catName[position].userSessionStatus.equals("Inprogress") -> {
                    holder.bindingAdapter.ivArrow.setColorFilter(ContextCompat.getColor(activity!!,R.color.black), PorterDuff.Mode.SRC_IN)
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_selected_bg)
                    holder.bindingAdapter.viewUp.setBackgroundResource(R.drawable.session_viewer_line_up)
//                    Glide.with(activity).load(R.drawable.session_idea_icon).thumbnail(0.05f)
//                            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
//                            .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivIcon)
                    holder.bindingAdapter.tvLabel.visibility = View.VISIBLE
                    holder.bindingAdapter.ivBanner.visibility = View.VISIBLE
                    if (catName.size != 1) {
                        if (position < (catName.size - 1)) {
                            if(!catName[position + 1].userSessionStatus.equals("Lock")){
                                holder.bindingAdapter.viewDown.setBackgroundResource(R.drawable.session_viewer_line_down)
                            }else{
                                holder.bindingAdapter.viewDown.setBackgroundResource(R.drawable.session_viewer_gray_line_down)
                            }
                        }
                    }
                    if (catName[position].beforeSession!!.isEmpty()) {
                        holder.bindingAdapter.llBeforeSession.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llBeforeSession.visibility = View.VISIBLE
                    }
                    if (catName[position].afterSession!!.isEmpty()) {
                        holder.bindingAdapter.llAfterSession.visibility = View.GONE
                    } else {
                        holder.bindingAdapter.llAfterSession.visibility = View.VISIBLE
                    }
                }
                catName[position].userSessionStatus.equals("Lock") -> {
                    holder.bindingAdapter.ivArrow.setColorFilter(ContextCompat.getColor(activity!!,R.color.light_gray), PorterDuff.Mode.SRC_IN)
                    holder.bindingAdapter.llBorder.setBackgroundResource(R.drawable.session_unselected_bg)
                    holder.bindingAdapter.viewDown.setBackgroundResource(R.drawable.session_viewer_gray_line_down)
                    holder.bindingAdapter.viewUp.setBackgroundResource(R.drawable.session_viewer_gray_line_up)
//                    Glide.with(activity).load(R.drawable.session_inprogress_status_icon).thumbnail(0.05f)
//                            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
//                            .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.bindingAdapter.ivIcon)
                    holder.bindingAdapter.tvLabel.visibility = View.GONE
                    holder.bindingAdapter.ivBanner.visibility = View.GONE
                    holder.bindingAdapter.llAfterSession.visibility = View.GONE
                    holder.bindingAdapter.llBeforeSession.visibility = View.GONE
                }
            }

            holder.bindingAdapter.rlNext.setOnClickListener {
                if(catName[position].userSessionStatus.equals("Lock",ignoreCase = true)){
                    BWSApplication.showToast("Please complete above session first", activity)
                }else{
                    val i = Intent(activity, SessionStepListActivity::class.java)
                    i.putExtra("SessionId", catName[position].sessionId)
                    activity.startActivity(i)
                }
             /*   when {
                    catName[position].userSessionStatus.equals("Completed") -> {
                        val i = Intent(activity, EmpowerManageActivity::class.java)
                        i.putExtra("SessionId", catName[position].sessionId)
                        activity.startActivity(i)
                    }
                    catName[position].userSessionStatus.equals("Inprogress") -> {
                        val i = Intent(activity, SimilarContinueActivity::class.java)
                        i.putExtra("SessionId", catName[position].sessionId)
                        activity.startActivity(i)
                    }
                    catName[position].userSessionStatus.equals("Lock") -> {
                        BWSApplication.showToast("Please complete above session first", activity)
                    }
                }*/
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
                holder.bindingAdapter.viewDown.visibility = View.GONE
            }

            if (position == 0) {
                holder.bindingAdapter.viewUp.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return catName.size
        }
    }
}