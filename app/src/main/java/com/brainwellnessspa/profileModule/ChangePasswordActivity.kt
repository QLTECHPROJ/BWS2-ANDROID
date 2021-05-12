package com.brainwellnessspa.profileModule

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.userModuleTwo.models.ChangePasswordModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    var USERID: String? = null
    var CoUserID: String? = null
    lateinit var activity: Activity

    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val CurrentPswd: String = binding.etCurrentPswd.getText().toString().trim()
            val NewPswd: String = binding.etNewPswd.getText().toString().trim()
            val ConfirmPswd: String = binding.etConfirmPswd.getText().toString().trim()
            if (CurrentPswd.equals("", ignoreCase = true) &&
                NewPswd.equals("", ignoreCase = true) && ConfirmPswd.equals("", ignoreCase = true)
            ) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (CurrentPswd.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (NewPswd.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (ConfirmPswd.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnSave.setEnabled(true)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        activity = this@ChangePasswordActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")

        binding.etCurrentPswd.addTextChangedListener(userTextWatcher)
        binding.etNewPswd.addTextChangedListener(userTextWatcher)
        binding.etConfirmPswd.addTextChangedListener(userTextWatcher)

        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnSave.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        if (binding.etCurrentPswd.text.toString().equals("")) {
            binding.flCurrentPswd.error = "Current Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        } else if (binding.etCurrentPswd.text.toString().length < 8
            || !isValidPassword(binding.etCurrentPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = "Valid current Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        } else if (binding.etNewPswd.text.toString().equals("")) {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = "New Login Password is required"
            binding.flConfirmPswd.error = ""
        } else if (binding.etNewPswd.text.toString().length < 8
            || !isValidPassword(binding.etNewPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = "Valid new Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        } else if (binding.etConfirmPswd.text.toString().equals("")) {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = "Confirm new Login Password is required"
        } else if (binding.etConfirmPswd.text.toString().length < 8
            || !isValidPassword(binding.etConfirmPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = "Valid confirm new Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        }  else if (!binding.etConfirmPswd.text.toString()
                .equals(binding.etNewPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = "New & Confirm Login Password not match"
        } else {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    this@ChangePasswordActivity
                )
                val listCall: Call<ChangePasswordModel> =
                    APINewClient.getClient().getChangePassword(
                        USERID, CoUserID,
                        binding.etCurrentPswd.text.toString(),
                        binding.etConfirmPswd.text.toString()
                    )
                listCall.enqueue(object : Callback<ChangePasswordModel> {
                    override fun onResponse(
                        call: Call<ChangePasswordModel>,
                        response: Response<ChangePasswordModel>
                    ) {
                        try {
                            binding.flCurrentPswd.error = ""
                            binding.flNewPswd.error = ""
                            binding.flConfirmPswd.error = ""
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                this@ChangePasswordActivity
                            )
                            val listModel: ChangePasswordModel = response.body()!!
                            if (listModel.responseCode.equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                finish()
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ChangePasswordModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            this@ChangePasswordActivity
                        )
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}