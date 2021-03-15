package com.brainwellnessspa.DassAssSliderTwo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.OptionsDataListModel
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding

class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    lateinit var adapter: OptionsListAdapter
    private val dataListModel = ArrayList<OptionsDataListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)

        binding.rvAssFormList.layoutManager = LinearLayoutManager(this@DassAssSliderActivity)
        adapter = OptionsListAdapter(dataListModel)
        binding.rvAssFormList.adapter = adapter

        binding.btnDone.setOnClickListener {
            val i = Intent(this@DassAssSliderActivity, AssProcessActivity::class.java)
            i.putExtra(CONSTANTS.ASSPROCESS,"1")
            startActivity(i)
        }
        prepareOptionsData()
    }

    private fun prepareOptionsData() {
        var userAdd = OptionsDataListModel("Never")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("Sometimes")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("Often")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("Always always")
        dataListModel.add(userAdd)

        adapter.notifyDataSetChanged();
    }


    class OptionsListAdapter(listModel: List<OptionsDataListModel>) : RecyclerView.Adapter<OptionsListAdapter.MyViewHolder>() {
        private val listModel: List<OptionsDataListModel>

        init {
            this.listModel = listModel
        }

        inner class MyViewHolder(bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            var bindingAdapter: FormFillLayoutBinding

            init {
                this.bindingAdapter = bindingAdapter
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.btnOption.text = listModel.get(position).name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}