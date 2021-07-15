package com.brainwellnessspa.dashboardModule.session

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionActivitiesModel
import com.brainwellnessspa.databinding.ActivitySessionActivitiesBinding
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding

class SessionActivitiesActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionActivitiesBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: SessionActivitieAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("writing a journal"), SessionActivitiesModel("What are you grateful for today?"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_activities)

        ctx = this@SessionActivitiesActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionActivitieAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class SessionActivitieAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionActivitieAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudioFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audio_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: AudioFaqLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

}