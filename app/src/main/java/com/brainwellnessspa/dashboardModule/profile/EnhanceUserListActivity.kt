package com.brainwellnessspa.dashboardModule.profile

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEnhanceUserListBinding
import com.brainwellnessspa.databinding.EnhanceUserListLayoutBinding
import com.brainwellnessspa.userModule.coUserModule.AddCouserActivity
import com.brainwellnessspa.userModule.models.ManageUserListModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnhanceUserListActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceUserListBinding
    var userId: String? = null
    var enhanceUserListAdapter: EnhanceUserListAdapter? = null
    var mainAccountID: String? = null
    lateinit var activity: Activity
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_user_list)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mListLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUserList.layoutManager = mListLayoutManager
        binding.rvUserList.itemAnimator = DefaultItemAnimator()
        activity = this@EnhanceUserListActivity
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.llAddNewUser.setOnClickListener {
            val intent = Intent(applicationContext, AddCouserActivity::class.java)
            startActivity(intent)
        }
        prepareEnhanceUserList(activity, binding)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        binding.btnRemove.isEnabled = false
        binding.btnRemove.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        binding.btnRemove.setBackgroundResource(R.drawable.gray_round_cornor)
        super.onResume()
    }
    fun prepareEnhanceUserList(activity: Activity, binding: ActivityEnhanceUserListBinding) {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<ManageUserListModel> = APINewClient.client.getManageUserList(mainAccountID)
            listCall.enqueue(object : Callback<ManageUserListModel> {
                override fun onResponse(call: Call<ManageUserListModel>, response: Response<ManageUserListModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: ManageUserListModel = response.body()!!
                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            listModel.responseData?.let {
                                enhanceUserListAdapter = EnhanceUserListAdapter(it,binding)
                                binding.rvUserList.adapter = enhanceUserListAdapter
                            }
                        } else {
                            BWSApplication.showToast(listModel.responseMessage, activity)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ManageUserListModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }

    }

    inner class EnhanceUserListAdapter(private var manageUserListModel: ManageUserListModel.ResponseData, binding: ActivityEnhanceUserListBinding) : RecyclerView.Adapter<EnhanceUserListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: EnhanceUserListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.enhance_user_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

/*
            android:button="@drawable/radio_btn_enhance_background"
            ic_green_label_icon
            ic_yellow_label_icon Request expired
*/
            val list = manageUserListModel.userList
            holder.binding.tvName.text = list?.get(position)?.name
            holder.binding.llMainLayout.setOnClickListener {
                binding.btnRemove.isEnabled = true
                binding.btnRemove.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnRemove.setBackgroundResource(R.drawable.light_green_rounded_filled)
                holder.binding.ivStatus.setImageResource(R.drawable.ic_checked_user_icon)
            }

            binding.btnRemove.setOnClickListener {
                dialog = Dialog(activity)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setContentView(R.layout.remove_user_layout)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.dark_blue_gray)))
                dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val btnYes = dialog!!.findViewById<Button>(R.id.btnYes)
                val btnNo = dialog!!.findViewById<Button>(R.id.btnNo)
                val progressBar = dialog!!.findViewById<ProgressBar>(R.id.progressBar)
                val progressBarHolder = dialog!!.findViewById<FrameLayout>(R.id.progressBarHolder)
                dialog!!.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog!!.hide()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnYes.setOnClickListener {
                    dialog!!.hide()
//                    BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
                }

                btnNo.setOnClickListener { dialog!!.hide() }
                dialog!!.show()
                dialog!!.setCancelable(false)
            }
        }

        override fun getItemCount(): Int {
            return manageUserListModel.userList?.size!!
        }

        inner class MyViewHolder(var binding: EnhanceUserListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}