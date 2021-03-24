package com.brainwellnessspa.ManageModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.databinding.ActivitySleepTimeBinding
import com.brainwellnessspa.databinding.SleepTimeRawBinding

class SleepTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepTimeBinding
    lateinit var adapter: SleepTimeAdapter
    lateinit var ctx: Context
    private val userList = ArrayList<UserListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sleep_time)
        ctx = this@SleepTimeActivity
        prepareUserData()
        binding.rvTimeSlot.layoutManager = GridLayoutManager(ctx,2)
        adapter = SleepTimeAdapter(userList,ctx)
        binding.rvTimeSlot.adapter = adapter
    }

    private fun prepareUserData() {
        var userAdd = UserListModel("< 3")
        userList.add(userAdd)
        userAdd = UserListModel("3 - 4")
        userList.add(userAdd)
        userAdd = UserListModel("4 - 5")
        userList.add(userAdd)
        userAdd = UserListModel("5 - 6")
        userList.add(userAdd)
        userAdd = UserListModel("6 - 7")
        userList.add(userAdd)
        userAdd = UserListModel("7 - 8")
        userList.add(userAdd)
        userAdd = UserListModel("8 - 9")
        userList.add(userAdd)
        userAdd = UserListModel("9 - 10")
        userList.add(userAdd)
        userAdd = UserListModel("> 10")
        userList.add(userAdd)
    }
    class SleepTimeAdapter(private val listModel: List<UserListModel>, var ctx: Context) : RecyclerView.Adapter<SleepTimeAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SleepTimeRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SleepTimeRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.sleep_time_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvhours.text = listModel[position].name
            holder.bindingAdapter.llHourSlots.setOnClickListener {
                val i = Intent(ctx, RecommendedCategoryActivity::class.java)
                ctx.startActivity(i)
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}