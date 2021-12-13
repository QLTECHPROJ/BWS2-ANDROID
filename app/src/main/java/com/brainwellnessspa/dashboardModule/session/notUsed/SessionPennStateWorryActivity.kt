package com.brainwellnessspa.dashboardModule.session.notUsed

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionActivitiesModel
import com.brainwellnessspa.databinding.ActivitySessionPerceptionsBinding
import com.brainwellnessspa.databinding.PerceptionsMainLayoutBinding

class SessionPennStateWorryActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionPerceptionsBinding
    lateinit var ctx: Context
    var userID: String? = ""
    lateinit var adapter: SessionPerceptionsAdapter
    var coUserId: String? = ""
    var model = arrayOf(SessionActivitiesModel("1. Feelings of Loneliness"), SessionActivitiesModel("2. Feelings of being Isolated"), SessionActivitiesModel("3. Feelings of Unhappiness"), SessionActivitiesModel("4. Feelings of Desperation"), SessionActivitiesModel("5. Feelings of being unable to cope with life"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_penn_state_worry)

        ctx = this@SessionPennStateWorryActivity
        val searchList: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvList.layoutManager = searchList
        binding.rvList.itemAnimator = DefaultItemAnimator()
        adapter = SessionPerceptionsAdapter(model, ctx, binding.rvList, binding.tvFound)
        binding.rvList.adapter = adapter
    }

    inner class SessionPerceptionsAdapter(private val modelList: Array<SessionActivitiesModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<SessionPerceptionsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PerceptionsMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.perceptions_main_layout, parent, false)
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

        inner class MyViewHolder(var binding: PerceptionsMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}