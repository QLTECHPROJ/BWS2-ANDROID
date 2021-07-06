package com.brainwellnessspa.dashboardModule.profile

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
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
import com.brainwellnessspa.userModule.models.CancelInviteUserModel
import com.brainwellnessspa.userModule.models.ManageUserListModel
import com.brainwellnessspa.userModule.models.SetInviteUserModel
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
        prepareEnhanceUserList(activity)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        binding.btnRemove.isEnabled = false
        binding.btnRemove.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        binding.btnRemove.setBackgroundResource(R.drawable.gray_round_cornor)

        prepareEnhanceUserList(activity)
        super.onResume()
    }

    fun prepareEnhanceUserList(activity: Activity) {
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
                                enhanceUserListAdapter = EnhanceUserListAdapter(it)
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

    inner class EnhanceUserListAdapter(private var manageUserListModel: ManageUserListModel.ResponseData) : RecyclerView.Adapter<EnhanceUserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: EnhanceUserListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.enhance_user_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val list = manageUserListModel.userList

            list?.let {
                holder.binding.tvName.text = list[position].name

                holder.binding.ivStatus.setOnClickListener {
                    selectedItem = position
                    notifyDataSetChanged()
                    binding.btnRemove.isEnabled = true
                    binding.btnRemove.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnRemove.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }

                if (position == selectedItem) {
                    holder.binding.ivStatus.setImageResource(R.drawable.ic_checked_user_icon)
                } else {
                    holder.binding.ivStatus.setImageResource(R.drawable.ic_uncheck_user_icon)
                }
                when {
                    list[position].inviteStatus.equals("0") -> {
                        holder.binding.ivStatus.visibility = View.VISIBLE
                        holder.binding.tvStatus.visibility = View.GONE
                        holder.binding.ivBanner.visibility = View.GONE
                        holder.binding.tvLabel.visibility = View.GONE
                    }
                    list[position].inviteStatus.equals("1") -> {
                        holder.binding.ivStatus.visibility = View.GONE
                        holder.binding.tvStatus.visibility = View.VISIBLE
                        holder.binding.ivBanner.visibility = View.VISIBLE
                        holder.binding.ivBanner.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_green_label_icon))
                        holder.binding.tvLabel.visibility = View.VISIBLE
                        holder.binding.tvLabel.text = getString(R.string.request_sent)
                        holder.binding.tvStatus.text = getString(R.string.cancel)
                        holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.progressfilled))

                        holder.binding.tvStatus.setOnClickListener {
                            if (BWSApplication.isNetworkConnected(activity)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<CancelInviteUserModel> = APINewClient.client.getCancelInviteUser(userId, list[position].mobile)
                                listCall.enqueue(object : Callback<CancelInviteUserModel> {
                                    override fun onResponse(call: Call<CancelInviteUserModel>, response: Response<CancelInviteUserModel>) {
                                        try {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                            val listModel: CancelInviteUserModel = response.body()!!
                                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                prepareEnhanceUserList(activity)
                                            } else {
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<CancelInviteUserModel>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                            }
                        }
                    }
                    list[position].inviteStatus.equals("2") -> {
                        holder.binding.ivStatus.visibility = View.GONE
                        holder.binding.tvStatus.visibility = View.VISIBLE
                        holder.binding.ivBanner.visibility = View.VISIBLE
                        holder.binding.tvLabel.visibility = View.VISIBLE
                        holder.binding.ivBanner.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_yellow_label_icon))
                        holder.binding.tvLabel.text = getString(R.string.req_expired)
                        holder.binding.tvStatus.text = getString(R.string.resend)
                        holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.light_black))

                        holder.binding.tvStatus.setOnClickListener {
                            if (BWSApplication.isNetworkConnected(activity)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<SetInviteUserModel> = APINewClient.client.getSetInviteUser(userId, list[position].name, list[position].mobile)
                                listCall.enqueue(object : Callback<SetInviteUserModel> {
                                    override fun onResponse(call: Call<SetInviteUserModel>, response: Response<SetInviteUserModel>) {
                                        try {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                            val listModel: SetInviteUserModel = response.body()!!
                                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                prepareEnhanceUserList(activity)
                                            } else {
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<SetInviteUserModel>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                            }
                        }
                    }
                }

            }

            binding.btnRemove.setOnClickListener {
                dialog = Dialog(activity)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setContentView(R.layout.remove_user_layout)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.dark_blue_gray)))
                dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val btnYes = dialog!!.findViewById<Button>(R.id.btnYes)
                val btnNo = dialog!!.findViewById<Button>(R.id.btnNo)
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