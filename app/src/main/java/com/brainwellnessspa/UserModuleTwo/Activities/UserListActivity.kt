package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityUserListBinding
import com.brainwellnessspa.databinding.UserListLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class UserListActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserListBinding
    var PopUp: String = "0"
    lateinit var dialog: Dialog
    var USERID: String? = null
    lateinit var adapter: UserListAdapter
    private lateinit var editTexts: Array<EditText>
    var tvSendOTPbool: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        if (intent.extras != null) {
            PopUp = intent.getStringExtra(CONSTANTS.PopUp).toString()
        }

        if (PopUp.equals("1", ignoreCase = true)) {

        } else {

        }
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.llAddNewUser.setOnClickListener {
            val i = Intent(this@UserListActivity, AddProfileActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnLogIn.setOnClickListener {
            val i = Intent(this@UserListActivity, BottomNavigationActivity::class.java)
            startActivity(i)
        }
        binding.rvUserList.layoutManager = LinearLayoutManager(this@UserListActivity)

        prepareUserData()
    }

    private fun prepareUserData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@UserListActivity)
            val listCall: Call<AddedUserListModel> = APINewClient.getClient().getUserList(USERID)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(call: Call<AddedUserListModel>, response: Response<AddedUserListModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@UserListActivity)
                        val listModel: AddedUserListModel = response.body()!!
                        if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            adapter = UserListAdapter(listModel.getResponseData()!!, this@UserListActivity, dialog, editTexts, tvSendOTPbool, binding.progressBar, binding.progressBarHolder, binding.tvForgotPin)
                            binding.rvUserList.adapter = adapter
                            BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                        } else {
                            BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddedUserListModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@UserListActivity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    class UserListAdapter(private val listModel: List<AddedUserListModel.ResponseData>, private var activity: Activity, private var dialog: Dialog, var editTexts: Array<EditText>,
                          var tvSendOTPbool: Boolean = true, var progressBar: ProgressBar, var progressBarHolder: FrameLayout, var tvForgotPin: TextView) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {

        var clickabled: Boolean = false

        inner class MyViewHolder(var bindingAdapter: UserListLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: UserListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_list_layout, parent, false)
            return MyViewHolder(v)
        }

        //        ic_checked_to_icon
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvName.text = listModel.get(position).name

            holder.bindingAdapter.ivCheck.setImageResource(R.drawable.ic_checked_icon)
            if (clickabled) {
                holder.bindingAdapter.ivCheck.visibility = View.VISIBLE
            } else {
                holder.bindingAdapter.ivCheck.visibility = View.INVISIBLE
            }
            holder.bindingAdapter.rlCheckedUser.setOnClickListener { view ->
                dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.comfirm_pin_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                val btnDone: Button = dialog.findViewById(R.id.btnDone)
                val txtError: TextView = dialog.findViewById(R.id.txtError)
                val edtOTP1: EditText = dialog.findViewById(R.id.edtOTP1)
                val edtOTP2: EditText = dialog.findViewById(R.id.edtOTP2)
                val edtOTP3: EditText = dialog.findViewById(R.id.edtOTP3)
                val edtOTP4: EditText = dialog.findViewById(R.id.edtOTP4)

                editTexts = arrayOf<EditText>(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                edtOTP1.addTextChangedListener(PinTextWatcher(activity, 0, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP2.addTextChangedListener(PinTextWatcher(activity, 1, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP3.addTextChangedListener(PinTextWatcher(activity, 2, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP4.addTextChangedListener(PinTextWatcher(activity, 3, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP1.setOnKeyListener(PinOnKeyListener(0, editTexts))
                edtOTP2.setOnKeyListener(PinOnKeyListener(1, editTexts))
                edtOTP3.setOnKeyListener(PinOnKeyListener(2, editTexts))
                edtOTP4.setOnKeyListener(PinOnKeyListener(3, editTexts))
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnDone.setOnClickListener {
                    clickabled = true;
                    if (edtOTP1.text.toString().equals("", ignoreCase = true)
                            && edtOTP2.text.toString().equals("", ignoreCase = true)
                            && edtOTP3.text.toString().equals("", ignoreCase = true)
                            && edtOTP4.text.toString().equals("", ignoreCase = true)) {
                        txtError.visibility = View.VISIBLE
                        txtError.text = "Please enter OTP"
                    } else {
                        txtError.visibility = View.GONE
                        txtError.error = ""
                        if (BWSApplication.isNetworkConnected(activity)) {
                            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
                            val listCall: Call<VerifyPinModel> = APINewClient.getClient().getVerifyPin(listModel.get(position).coUserId,
                                    edtOTP1.getText().toString() + "" +
                                            edtOTP2.getText().toString() + "" +
                                            edtOTP3.getText().toString() + "" +
                                            edtOTP4.getText().toString())
                            listCall.enqueue(object : Callback<VerifyPinModel> {
                                override fun onResponse(call: Call<VerifyPinModel>, response: Response<VerifyPinModel>) {
                                    try {
                                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                                        val listModel: VerifyPinModel = response.body()!!
                                        if (listModel.getResponseCode().equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            val i = Intent(activity, WalkScreenActivity::class.java)
                                            activity.startActivity(i)
                                            dialog.dismiss()
                                            BWSApplication.showToast(listModel.getResponseMessage(), activity)
                                        } else if (listModel.getResponseCode().equals(activity.getString(R.string.ResponseCodefail), ignoreCase = true)) {
                                            txtError.visibility = View.VISIBLE
                                            txtError.error = listModel.getResponseMessage()
                                        } else {
                                            txtError.visibility = View.VISIBLE
                                            txtError.error = listModel.getResponseMessage()
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<VerifyPinModel>, t: Throwable) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                                }
                            })
                        } else {
                            BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
                        }
                    }
                }
                dialog.show()
                dialog.setCancelable(false)
            }

            tvForgotPin.setOnClickListener {
                dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.comfirm_pin_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                val btnDone: Button = dialog.findViewById(R.id.btnDone)
                val txtError: TextView = dialog.findViewById(R.id.txtError)
                val edtOTP1: EditText = dialog.findViewById(R.id.edtOTP1)
                val edtOTP2: EditText = dialog.findViewById(R.id.edtOTP2)
                val edtOTP3: EditText = dialog.findViewById(R.id.edtOTP3)
                val edtOTP4: EditText = dialog.findViewById(R.id.edtOTP4)

                editTexts = arrayOf<EditText>(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                edtOTP1.addTextChangedListener(PinTextWatcher(activity, 0, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP2.addTextChangedListener(PinTextWatcher(activity, 1, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP3.addTextChangedListener(PinTextWatcher(activity, 2, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP4.addTextChangedListener(PinTextWatcher(activity, 3, editTexts, btnDone, edtOTP1, edtOTP2, edtOTP3, edtOTP4, tvSendOTPbool))
                edtOTP1.setOnKeyListener(PinOnKeyListener(0, editTexts))
                edtOTP2.setOnKeyListener(PinOnKeyListener(1, editTexts))
                edtOTP3.setOnKeyListener(PinOnKeyListener(2, editTexts))
                edtOTP4.setOnKeyListener(PinOnKeyListener(3, editTexts))
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnDone.setOnClickListener {
                    if (edtOTP1.text.toString().equals("", ignoreCase = true)
                            && edtOTP2.text.toString().equals("", ignoreCase = true)
                            && edtOTP3.text.toString().equals("", ignoreCase = true)
                            && edtOTP4.text.toString().equals("", ignoreCase = true)) {
                        txtError.visibility = View.VISIBLE
                        txtError.text = "Please enter OTP"
                    } else {
                        txtError.visibility = View.GONE
                        txtError.error = ""
                        if (BWSApplication.isNetworkConnected(activity)) {
                            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
                            val listCall: Call<VerifyPinModel> = APINewClient.getClient().getVerifyPin(listModel.get(position).coUserId,
                                    edtOTP1.getText().toString() + "" +
                                            edtOTP2.getText().toString() + "" +
                                            edtOTP3.getText().toString() + "" +
                                            edtOTP4.getText().toString())
                            listCall.enqueue(object : Callback<VerifyPinModel> {
                                override fun onResponse(call: Call<VerifyPinModel>, response: Response<VerifyPinModel>) {
                                    try {
                                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                                        val listModel: VerifyPinModel = response.body()!!
                                        if (listModel.getResponseCode().equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            val i = Intent(activity, WalkScreenActivity::class.java)
                                            activity.startActivity(i)
                                            dialog.dismiss()
                                            BWSApplication.showToast(listModel.getResponseMessage(), activity)
                                        } else if (listModel.getResponseCode().equals(activity.getString(R.string.ResponseCodefail), ignoreCase = true)) {
                                            txtError.visibility = View.VISIBLE
                                            txtError.error = listModel.getResponseMessage()
                                        } else {
                                            txtError.visibility = View.VISIBLE
                                            txtError.error = listModel.getResponseMessage()
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<VerifyPinModel>, t: Throwable) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                                }
                            })
                        } else {
                            BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
                        }
                    }
                }
                dialog.show()
                dialog.setCancelable(false)
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    class PinTextWatcher internal constructor(val activity: Activity, private val currentIndex: Int,
                                              var editTexts: Array<EditText>, val btnDone: Button,
                                              val edtOTP1: EditText, val edtOTP2: EditText,
                                              val edtOTP3: EditText, val edtOTP4: EditText,
                                              var tvSendOTPbool: Boolean) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            val OTP1: String = edtOTP1.getText().toString().trim()
            val OTP2: String = edtOTP2.getText().toString().trim()
            val OTP3: String = edtOTP3.getText().toString().trim()
            val OTP4: String = edtOTP4.getText().toString().trim()
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                btnDone.setEnabled(true)
                btnDone.setTextColor(activity.getResources().getColor(R.color.white))
                btnDone.setBackgroundResource(R.drawable.extra_round_cornor)
            } else {
                btnDone.setEnabled(false)
                btnDone.setTextColor(activity.getResources().getColor(R.color.white))
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
            editTexts.get(currentIndex).removeTextChangedListener(this)
            editTexts.get(currentIndex).setText(text)
            editTexts.get(currentIndex).setSelection(text.length)
            editTexts.get(currentIndex).addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.length == 0) {
                if (!tvSendOTPbool) {
                    editTexts.get(0).requestFocus()
                } else {
                    moveToPrevious()
                }
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts.get(currentIndex + 1).requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts.get(currentIndex).clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts.get(currentIndex - 1).requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            private get() {
                for (editText in editTexts) if (editText.text.toString().trim { it <= ' ' }.length == 0) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity.getCurrentFocus() != null) {
                val inputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()!!.getWindowToken(), 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst = true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    class PinOnKeyListener internal constructor(val currentIndex: Int, var editTexts: Array<EditText>) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts.get(currentIndex).getText().toString().isEmpty() && currentIndex != 0) editTexts.get(currentIndex - 1).requestFocus()
            }
            return false
        }
    }
}


