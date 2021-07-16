package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.BrainFeelingStatusModel
import com.brainwellnessspa.databinding.ActivityBrainStatusBinding
import com.brainwellnessspa.databinding.BrainFeelingStatusLayoutBinding
import com.google.android.flexbox.*

class BrainStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivityBrainStatusBinding
    lateinit var adapter: BrainFeelingStatusAdapter
    lateinit var activity: Activity
    var model = arrayOf(BrainFeelingStatusModel("Constant thoughts"), BrainFeelingStatusModel("Overthinking"), BrainFeelingStatusModel("Negative"), BrainFeelingStatusModel("Tired"), BrainFeelingStatusModel("Processing Quick"), BrainFeelingStatusModel("Processing Slow"), BrainFeelingStatusModel("Busy"), BrainFeelingStatusModel("Angry"), BrainFeelingStatusModel("Fatigued"), BrainFeelingStatusModel("Exhausted"), BrainFeelingStatusModel("Sad"), BrainFeelingStatusModel("Emotional"), BrainFeelingStatusModel("Foggy"), BrainFeelingStatusModel("Confused"), BrainFeelingStatusModel("Exhausted"), BrainFeelingStatusModel("Overwhelmed"), BrainFeelingStatusModel("Depressed"), BrainFeelingStatusModel("Anxious"), BrainFeelingStatusModel("Brain Processing Slower"), BrainFeelingStatusModel("Positive"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brain_status)
        activity = this@BrainStatusActivity
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvList.layoutManager = layoutManager

        adapter = BrainFeelingStatusAdapter(binding, activity, model)
        binding.rvList.adapter = adapter
    }

    class BrainFeelingStatusAdapter(var binding: ActivityBrainStatusBinding, var activity: Activity, var catName: Array<BrainFeelingStatusModel>) : RecyclerView.Adapter<BrainFeelingStatusAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: BrainFeelingStatusLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BrainFeelingStatusLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.brain_feeling_status_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvText.text = catName[position].title
        }

        override fun getItemCount(): Int {
            return catName.size
        }
    }

}