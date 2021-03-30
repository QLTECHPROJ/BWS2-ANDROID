package com.brainwellnessspa.ManageModule

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.MembershipModule.Adapters.SubscriptionAdapter
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.databinding.ActivityManageBinding
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding
import com.brainwellnessspa.databinding.MembershipFaqLayoutBinding

class ManageActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageBinding
    lateinit var adapter: MembershipFaqAdapter
    private val userList = ArrayList<UserListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage)

        binding.llBack.setOnClickListener { _ ->
            finish()
        }

        binding.rvFaqList.layoutManager = LinearLayoutManager(this@ManageActivity)
        adapter = MembershipFaqAdapter(userList, this, binding.rvFaqList, binding.tvFound)
        binding.rvFaqList.adapter = adapter
//        SubscriptionAdapter TODO Audio List Adapter
        prepareUserData();
    }

    private fun prepareUserData() {
        var userAdd = UserListModel("How long does the Membership last?")
        userList.add(userAdd)
        userAdd = UserListModel("Is there a free trial?")
        userList.add(userAdd)
        userAdd = UserListModel("Yes. Every plan comes with a 30-day free trial option")
        userList.add(userAdd)
        userAdd = UserListModel("How can I cancel if I need to?")
        userList.add(userAdd)
        userAdd = UserListModel("How do I purchase a subscription?")
        userList.add(userAdd)
        userAdd = UserListModel("What are the benefits of signing up for the Membership Program")
        userList.add(userAdd)
        userAdd = UserListModel("Will my subscription get auto-renewed?")
        userList.add(userAdd)
        userAdd = UserListModel("What's the best way to use the Membership? Where do I start?")
        userList.add(userAdd)

        adapter.notifyDataSetChanged();
    }


    class MembershipFaqAdapter(private val modelList: List<UserListModel>, var ctx: Context, var rvFaqList: RecyclerView, var tvFound: TextView) : RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MembershipFaqLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.membership_faq_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(modelList[position].name)
            holder.binding.tvDesc.setText("Yes. Every plan comes with a 30-day free trial option")
            holder.binding.ivClickRight.setOnClickListener { view ->
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray)
                holder.binding.tvDesc.setFocusable(true)
                holder.binding.tvDesc.requestFocus()
                holder.binding.tvDesc.setVisibility(View.VISIBLE)
                holder.binding.ivClickRight.setVisibility(View.GONE)
                holder.binding.ivClickDown.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon)
            }
            holder.binding.ivClickDown.setOnClickListener { view ->
                holder.binding.llBgChange.setBackgroundResource(Color.TRANSPARENT)
                holder.binding.llMainLayout.setBackgroundResource(R.drawable.membership_faq_not_clicked)
                holder.binding.tvDesc.setVisibility(View.GONE)
                holder.binding.ivClickRight.setVisibility(View.VISIBLE)
                holder.binding.ivClickDown.setVisibility(View.GONE)
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon)
            }
            if (modelList.size == 0) {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.GONE
            } else {
                tvFound.visibility = View.GONE
                rvFaqList.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return modelList.size
        }

        inner class MyViewHolder(binding: MembershipFaqLayoutBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
            var binding: MembershipFaqLayoutBinding

            init {
                this.binding = binding
            }
        }
    }
}