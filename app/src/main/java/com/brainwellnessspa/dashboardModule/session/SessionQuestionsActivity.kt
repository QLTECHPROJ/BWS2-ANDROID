package com.brainwellnessspa.dashboardModule.session

import android.annotation.SuppressLint
import android.app.Activity
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
import com.brainwellnessspa.databinding.ActivitySessionQuestionsBinding
import com.brainwellnessspa.databinding.PreceptionsMainLayoutBinding

class SessionQuestionsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionQuestionsBinding
    lateinit var ctx: Context
    lateinit var activity: Activity
    var userID: String? = ""
    lateinit var adapter: SessionQuestionsAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. If I do not have enoughtime to do everything, I do not worry about it."),
            SessionActivitiesModel("2. My worries overwhelm me"),
            SessionActivitiesModel("3. I do not tend to worry about things."),
            SessionActivitiesModel("4. Many situations make me worry."),
            SessionActivitiesModel("5. I know I should not worry about things, but I just cannot help it."))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_questions)

        ctx = this@SessionQuestionsActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionQuestionsAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class SessionQuestionsAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionQuestionsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PreceptionsMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.preceptions_main_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ResourceType")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model: SessionActivitiesModel = modelList[position]
            holder.binding.tvTitle.text = model.title
            holder.binding.tvOne.text = "1 (not at all typical of me)"
            holder.binding.tvTwo.text = "2"
            holder.binding.tvThree.text = "3"
            holder.binding.tvFour.text = "4"
            holder.binding.tvFive.text = "5 (very typical of me)"
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(var binding: PreceptionsMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}