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
import com.brainwellnessspa.databinding.ActivitySessionSelfScaleBinding
import com.brainwellnessspa.databinding.PreceptionsAssessmentLayoutBinding
import com.brainwellnessspa.databinding.PreceptionsMainLayoutBinding

class SessionSelfScaleActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionSelfScaleBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: SessionSelfScaleAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. Feelings of Loneliness"), SessionActivitiesModel("2. Feelings of being Isolated"), SessionActivitiesModel("3. Feelings of Unhappiness"), SessionActivitiesModel("4. Feelings of Desperation"), SessionActivitiesModel("5. Feelings of being unable to cope with life"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_self_scale)
        ctx = this@SessionSelfScaleActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionSelfScaleAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class SessionSelfScaleAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionSelfScaleAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PreceptionsAssessmentLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preceptions_assessment_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
            holder.binding.tvOne.text = "Strongly Agree"
            holder.binding.tvTwo.text = "Agree"
            holder.binding.tvThree.text = "Disagree"
            holder.binding.tvFour.text = "Strongly Disagree"
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: PreceptionsAssessmentLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}