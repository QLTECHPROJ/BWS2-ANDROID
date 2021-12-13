package com.brainwellnessspa.dashboardModule.session.notUsed

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
import com.brainwellnessspa.databinding.ActivitySessionAssessmentBinding
import com.brainwellnessspa.databinding.PreceptionsAssessmentLayoutBinding

class SessionAssessmentActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionAssessmentBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: SessionAssessmentAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. I found it hard to wind down"), SessionActivitiesModel("2. I was aware of dryness of my mouth"), SessionActivitiesModel("3. I couldn’t seem to experience any positive feeling at all"), SessionActivitiesModel("4. I experienced breathing difficulty (e.g. excessively rapid breathing,breathlessness in the absence of physical exertion)"), SessionActivitiesModel("5. I found it difficult to work up the initiative to do things"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_assessment)

        ctx = this@SessionAssessmentActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionAssessmentAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter

        /*TODO note radio button use this drawable radio_btn_session_background*/
    }

    inner class SessionAssessmentAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionAssessmentAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PreceptionsAssessmentLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preceptions_assessment_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: PreceptionsAssessmentLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}