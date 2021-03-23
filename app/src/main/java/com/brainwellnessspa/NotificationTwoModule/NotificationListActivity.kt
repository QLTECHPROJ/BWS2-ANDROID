package com.brainwellnessspa.NotificationTwoModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Activities.UserListActivity
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.databinding.ActivityNotificationListBinding
import com.brainwellnessspa.databinding.NotificationListLayoutBinding
import com.brainwellnessspa.databinding.UserListLayoutBinding

class NotificationListActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationListBinding
    lateinit var adapter: NotiListAdapter
    private val notiList = ArrayList<NotiListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)

        binding.rvNotiList.layoutManager = LinearLayoutManager(this@NotificationListActivity)
        adapter = NotiListAdapter(notiList)
        binding.rvNotiList.adapter = adapter

        prepareNotiData()
    }

    private fun prepareNotiData() {
        var notiAdd = NotiListModel("Playlist Reminder", "Time to listen to your playlist","Just Now")
        notiList.add(notiAdd)
        notiAdd = NotiListModel("Home Maintenance Audio successfully added in Night Playlist","","Just Now")
        notiList.add(notiAdd)
        notiAdd = NotiListModel("Your reminder is set","","Just Now")
        notiList.add(notiAdd)
        notiAdd = NotiListModel("Your playlist is ready.","","Just Now")
        notiList.add(notiAdd)

        adapter.notifyDataSetChanged();
    }

    class NotiListAdapter(listModel: List<NotiListModel>) : RecyclerView.Adapter<NotiListAdapter.MyViewHolder>() {
        private val listModel: List<NotiListModel> = listModel

        inner class MyViewHolder(bindingAdapter: NotificationListLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            var bindingAdapter: NotificationListLayoutBinding = bindingAdapter
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: NotificationListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvTitle.text = listModel.get(position).title
            holder.bindingAdapter.tvDesc.text = listModel.get(position).desc
            holder.bindingAdapter.tvTime.text = listModel.get(position).time
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}

//ic_remind_noti_icon
//ic_music_noti_icon