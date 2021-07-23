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
import com.brainwellnessspa.databinding.ActivitySessionPerceptionsBinding
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding
import com.brainwellnessspa.databinding.PreceptionsMainLayoutBinding

class SessionPerceptionsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionPerceptionsBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: SessionPerceptionsAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. Feelings of Loneliness"),
        SessionActivitiesModel("2. Feelings of being Isolated"),
        SessionActivitiesModel("3. Feelings of Unhappiness"),
        SessionActivitiesModel("4. Feelings of Desperation"),
        SessionActivitiesModel("5. Feelings of being unable to cope with life"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_perceptions)

        ctx = this@SessionPerceptionsActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionPerceptionsAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class SessionPerceptionsAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionPerceptionsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PreceptionsMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preceptions_main_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: PreceptionsMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

}