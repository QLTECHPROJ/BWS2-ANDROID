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
import com.brainwellnessspa.databinding.ActivityAggressionQuestionsBinding
import com.brainwellnessspa.databinding.PerceptionsMainLayoutBinding

class AggressionQuestionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAggressionQuestionsBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: AggressionQuestionsAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. Feelings of Loneliness"), SessionActivitiesModel("2. Feelings of being Isolated"), SessionActivitiesModel("3. Feelings of Unhappiness"), SessionActivitiesModel("4. Feelings of Desperation"), SessionActivitiesModel("5. Feelings of being unable to cope with life"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aggression_questions)

        ctx = this@AggressionQuestionsActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = AggressionQuestionsAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class AggressionQuestionsAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<AggressionQuestionsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PerceptionsMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.perceptions_main_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
            holder.binding.tvOne.text = "1 (Low)"
            holder.binding.tvTwo.text = "2"
            holder.binding.tvThree.text = "3"
            holder.binding.tvFour.text = "4"
            holder.binding.tvFive.text = "5 (High)"
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: PerceptionsMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

}