package com.brainwellnessspa.dashboardModule.session

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionActivitiesModel
import com.brainwellnessspa.databinding.FragmentSessionDetailBinding
import com.brainwellnessspa.databinding.SessionMainLayoutBinding

class SessionDetailFragment : Fragment() {
    lateinit var binding: FragmentSessionDetailBinding
    lateinit var adapter: SessionMainAdapter
    var model = arrayOf(SessionActivitiesModel("Session 1"), SessionActivitiesModel("Session 2"), SessionActivitiesModel("Session 3"), SessionActivitiesModel("Session 4"), SessionActivitiesModel("Session 5"), SessionActivitiesModel("Session 6"), SessionActivitiesModel("Session 7"), SessionActivitiesModel("Session 8"), SessionActivitiesModel("Session 9"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
        binding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = SessionMainAdapter(binding, model, activity)
        binding.rvList.adapter = adapter
        return binding.root
    }

    /* session_unselected_bg
    * session_selected_bg
    * session_idea_icon
    * ic_session_lock_icon*/
    class SessionMainAdapter(var binding: FragmentSessionDetailBinding, var catName: Array<SessionActivitiesModel>, val activity: FragmentActivity?) : RecyclerView.Adapter<SessionMainAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SessionMainLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionMainLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_main_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvTitle.text = catName[position].title

            if (catName.size == 1) {
                holder.bindingAdapter.viewDown.visibility = View.GONE;
            } else {
                holder.bindingAdapter.viewDown.visibility = View.VISIBLE;
            }

            if (position==(catName.size -1))
            {
                holder.bindingAdapter.viewDown.visibility = View.GONE;
            }

            if (position== 0)
            {
                holder.bindingAdapter.viewUp.visibility = View.GONE;
            }
            holder.bindingAdapter.rlNext.setOnClickListener {
                val i = Intent(activity, SessionDetailContinueActivity::class.java)
                activity?.startActivity(i)
            }
        }

        override fun getItemCount(): Int {
            return catName.size
        }
    }

}