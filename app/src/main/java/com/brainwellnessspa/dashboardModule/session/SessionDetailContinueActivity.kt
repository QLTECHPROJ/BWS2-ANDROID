package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionActivitiesModel
import com.brainwellnessspa.databinding.ActivitySessionDetailContinueBinding
import com.brainwellnessspa.databinding.SessionDetailLayoutBinding

class SessionDetailContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionDetailContinueBinding
    lateinit var activity: Activity
    lateinit var adapter: SessionDetailAdapter
    var model = arrayOf(SessionActivitiesModel("1"), SessionActivitiesModel("2"), SessionActivitiesModel("3"), SessionActivitiesModel("4"), SessionActivitiesModel("5"), SessionActivitiesModel("6"), SessionActivitiesModel("7"), SessionActivitiesModel("8"), SessionActivitiesModel("9"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_detail_continue)
        activity = this@SessionDetailContinueActivity
        binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = SessionDetailAdapter(binding, model, activity)
        binding.rvList.adapter = adapter
    }

    class SessionDetailAdapter(var binding: ActivitySessionDetailContinueBinding, var catName: Array<SessionActivitiesModel>, val activity: Activity) : RecyclerView.Adapter<SessionDetailAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SessionDetailLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionDetailLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_detail_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvNumber.text = catName[position].title

            if (catName.size == 1) {
                holder.bindingAdapter.viewDown.visibility = View.GONE;
            } else {
                holder.bindingAdapter.viewDown.visibility = View.VISIBLE;
            }

            if (position==(catName.size -1))
            {
                holder.bindingAdapter.viewDown.visibility = View.GONE;
            }

        }

        override fun getItemCount(): Int {
            return catName.size
        }
    }

}