package com.brainwellnessspa.dashboardModule.profile

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.addCouserBackStatus
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEnhanceUserListBinding
import com.brainwellnessspa.databinding.EnhanceUserListLayoutBinding
import com.brainwellnessspa.userModule.coUserModule.AddCouserActivity
import com.brainwellnessspa.userModule.models.*
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
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
    var isMainAccount: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_user_list)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        isMainAccount = shared.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "")
        val mListLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUserList.layoutManager = mListLayoutManager
        binding.rvUserList.itemAnimator = DefaultItemAnimator()
        activity = this@EnhanceUserListActivity
        binding.llBack.setOnClickListener {
            finish()
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
                                enhanceUserListAdapter = EnhanceUserListAdapter(it, binding.llAddNewUser)
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

    inner class EnhanceUserListAdapter(private var manageUserListModel: ManageUserListModel.ResponseData, val llAddNewUser: LinearLayout) : RecyclerView.Adapter<EnhanceUserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: EnhanceUserListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.enhance_user_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val list = manageUserListModel.userList

            list?.let {
                holder.binding.tvName.text = list[position].name

                if (isMainAccount.equals("1", ignoreCase = true)) {
                    binding.llAddNewUser.visibility = View.VISIBLE
                } else {
                    binding.llAddNewUser.visibility = View.GONE
                }

                binding.llAddNewUser.setOnClickListener {
                    if (isMainAccount.equals("1", ignoreCase = true)) {
                        binding.llAddNewUser.visibility = View.VISIBLE
                        if (manageUserListModel.userList!!.size == manageUserListModel.maxuseradd!!.toInt()) {
                            BWSApplication.showToast("Please upgrade your plan", activity)
                        } else {
                            addCouserBackStatus = 1
                            val intent = Intent(applicationContext, AddCouserActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        binding.llAddNewUser.visibility = View.GONE
                    }
                }



                holder.binding.ivStatus.setOnClickListener {
                    selectedItem = position
                    Log.e("userId dynamic", list[position].userId.toString())
                    binding.btnRemove.isEnabled = true
                    binding.btnRemove.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnRemove.setBackgroundResource(R.drawable.light_green_rounded_filled)
                    notifyDataSetChanged()
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
                                            } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                                BWSApplication.deleteCall(activity)
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                val i = Intent(activity, SignInActivity::class.java)
                                                i.putExtra("mobileNo", "")
                                                i.putExtra("countryCode", "")
                                                i.putExtra("name", "")
                                                i.putExtra("email", "")
                                                i.putExtra("countryShortName", "")
                                                startActivity(i)
                                                finish()
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
                                            } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                                BWSApplication.deleteCall(activity)
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                val i = Intent(activity, SignInActivity::class.java)
                                                i.putExtra("mobileNo", "")
                                                i.putExtra("countryCode", "")
                                                i.putExtra("name", "")
                                                i.putExtra("email", "")
                                                i.putExtra("countryShortName", "")
                                                startActivity(i)
                                                finish()
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

                binding.btnRemove.setOnClickListener {
                    if (list[selectedItem].userId.equals(list[selectedItem].mainAccountID, ignoreCase = true)) {
                        BWSApplication.showToast("You can't remove main user over here", activity)
                    } else {
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
                            Log.e("userId", userId.toString())
                            Log.e("userId dynamic", list[position].userId.toString())
                            if (BWSApplication.isNetworkConnected(activity)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<RemoveInviteUserModel> = APINewClient.client.getRemoveInviteUser(list[selectedItem].userId, list[selectedItem].mainAccountID)
                                listCall.enqueue(object : Callback<RemoveInviteUserModel> {
                                    override fun onResponse(call: Call<RemoveInviteUserModel>, response: Response<RemoveInviteUserModel>) {
                                        try {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                            val listModel: RemoveInviteUserModel = response.body()!!
                                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                prepareEnhanceUserList(activity)
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                dialog!!.hide()
                                            } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                                dialog!!.hide()
                                                BWSApplication.deleteCall(activity)
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                                val i = Intent(activity, SignInActivity::class.java)
                                                i.putExtra("mobileNo", "")
                                                i.putExtra("countryCode", "")
                                                i.putExtra("name", "")
                                                i.putExtra("email", "")
                                                i.putExtra("countryShortName", "")
                                                startActivity(i)
                                                finish()
                                            } else {
                                                BWSApplication.showToast(listModel.responseMessage, activity)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<RemoveInviteUserModel>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                            }
                        }

                        btnNo.setOnClickListener { dialog!!.hide() }
                        dialog!!.show()
                        dialog!!.setCancelable(false)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return manageUserListModel.userList?.size!!
        }

        inner class MyViewHolder(var binding: EnhanceUserListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}