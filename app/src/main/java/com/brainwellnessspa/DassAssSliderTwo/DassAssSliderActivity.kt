package com.brainwellnessspa.DassAssSliderTwo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.OptionsDataListModel
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding

class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    lateinit var firstListAdapter: OptionsFirstListAdapter
    lateinit var secondListAdapter: OptionsSecondListAdapter
    private val dataListModel = ArrayList<OptionsDataListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)

        binding.rvFirstList.layoutManager = GridLayoutManager(this@DassAssSliderActivity, 3)
        firstListAdapter = OptionsFirstListAdapter(dataListModel)
        binding.rvFirstList.adapter = firstListAdapter

        binding.rvSecondList.layoutManager = GridLayoutManager(this@DassAssSliderActivity, 3)
        secondListAdapter = OptionsSecondListAdapter(dataListModel)
        binding.rvSecondList.adapter = secondListAdapter

        binding.btnDone.setOnClickListener {
            val i = Intent(this@DassAssSliderActivity, AssProcessActivity::class.java)
            i.putExtra(CONSTANTS.ASSPROCESS, "1")
            startActivity(i)
        }
        prepareOptionsData()
    }

    private fun prepareOptionsData() {
        var userAdd = OptionsDataListModel("0")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("1")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("2")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("3")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("4")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("5")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("6")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("7")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("8")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("9")
        dataListModel.add(userAdd)
        userAdd = OptionsDataListModel("10")
        dataListModel.add(userAdd)

        firstListAdapter.notifyDataSetChanged();
        secondListAdapter.notifyDataSetChanged();
    }


    class OptionsFirstListAdapter(listModel: List<OptionsDataListModel>) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
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
            holder.bindingAdapter.cbChecked.text = listModel.get(position).name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    class OptionsSecondListAdapter(listModel: List<OptionsDataListModel>) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
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
            holder.bindingAdapter.cbChecked.text = listModel.get(position).name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }
}