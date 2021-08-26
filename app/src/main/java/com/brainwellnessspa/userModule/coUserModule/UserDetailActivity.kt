package com.brainwellnessspa.userModule.coUserModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityUserDetailBinding
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.userModule.models.AddUserModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserDetailBinding
    private var mainAccountID: String? = ""
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val name: String = binding.etName.text.toString().trim()
            val email: String = binding.etEmail.text.toString().trim()
            if (name == "" && email == "") {
                checkBtnStatus(0)
            } else if (name == "") {
                checkBtnStatus(0)
            } else if (email == "") {
                checkBtnStatus(0)
            } else {
                checkBtnStatus(1)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        activity = this@UserDetailActivity

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.etName.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)

        binding.btnProceed.setOnClickListener {
            setupUserDetail(activity)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setupUserDetail(activity: Activity) {
        if (binding.etName.text.toString() == "") {
            binding.txtNameError.visibility = View.VISIBLE
            binding.txtNameError.text = getString(R.string.pls_provide_name)
            binding.txtEmailError.visibility = View.GONE
        } else if (binding.etEmail.text.toString() == "") {
            binding.txtNameError.visibility = View.GONE
            binding.txtEmailError.visibility = View.VISIBLE
            binding.txtEmailError.text = getString(R.string.pls_provide_email)
        } else if (!binding.etEmail.text.toString().isEmailValid()) {
            binding.txtNameError.visibility = View.GONE
            binding.txtEmailError.visibility = View.VISIBLE
            binding.txtEmailError.text = getString(R.string.pls_provide_valid_email)
        } else {
            binding.txtNameError.visibility = View.GONE
            binding.txtEmailError.visibility = View.GONE
            if (isNetworkConnected(this)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall: Call<AddUserModel> = APINewClient.client.getAddUser(mainAccountID, binding.etName.text.toString(), binding.etEmail.text.toString())
                listCall.enqueue(object : Callback<AddUserModel> {
                    override fun onResponse(call: Call<AddUserModel>, response: Response<AddUserModel>) {
                        try {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            binding.txtNameError.visibility = View.GONE
                            binding.txtEmailError.visibility = View.GONE
                            val listModel: AddUserModel = response.body()!!
                            when {
                                listModel.responseCode.equals(getString(R.string.ResponseCodesuccess)) -> {
                                    if (addCouserBackStatus == 1) {
                                        finish()
                                    } else {
                                        val intent = Intent(applicationContext, UserListActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        startActivity(intent)
                                        finish()
                                    }
                                    showToast(listModel.responseMessage, activity)
                                }
                                listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted)) -> {
                                    deleteCall(activity)
                                    showToast(listModel.responseMessage, activity)
                                    val i = Intent(activity, SignInActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    startActivity(i)
                                    finish()
                                }
                                else -> {
                                    showToast(listModel.responseMessage, activity)
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<AddUserModel>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun checkBtnStatus(check: Int) {
        if (check == 0) {
            binding.btnProceed.isEnabled = false
            binding.btnProceed.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnProceed.setBackgroundResource(R.drawable.gray_round_cornor)
        } else if (check == 1) {
            binding.btnProceed.isEnabled = true
            binding.btnProceed.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnProceed.setBackgroundResource(R.drawable.light_green_rounded_filled)
        }
    }
}