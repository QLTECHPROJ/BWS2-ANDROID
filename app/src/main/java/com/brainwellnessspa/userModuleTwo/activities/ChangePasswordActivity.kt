package com.brainwellnessspa.userModuleTwo.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.userModuleTwo.models.ChangePasswordModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityChangePasswordBinding
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    var userID: String? = null
    private var coUserID: String? = null
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val currentPaswd: String = binding.etCurrentPswd.text.toString().trim()
            val newPswd: String = binding.etNewPswd.text.toString().trim()
            val confirmPswd: String = binding.etConfirmPswd.text.toString().trim()
            if (currentPaswd.equals("", ignoreCase = true) &&
                newPswd.equals("", ignoreCase = true) && confirmPswd.equals("", ignoreCase = true)
            ) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (currentPaswd.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (newPswd.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (confirmPswd.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnSave.isEnabled = true
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
        userID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        val p = Properties()
        p.putValue("userId", userID)
        p.putValue("coUserId", coUserID)
        BWSApplication.addToSegment("Change Password Screen Viewed", p, CONSTANTS.screen)
        binding.etCurrentPswd.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.etNewPswd.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.etConfirmPswd.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.ivCurrentPswdVisible.visibility = View.VISIBLE
        binding.ivCurrentPswdInVisible.visibility = View.GONE
        binding.ivNewPswdVisible.visibility = View.VISIBLE
        binding.ivNewPswdInVisible.visibility = View.GONE
        binding.ivConfirmPswdVisible.visibility = View.VISIBLE
        binding.ivConfirmPswdInVisible.visibility = View.GONE
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

        binding.ivCurrentPswdVisible.setOnClickListener {
            binding.etCurrentPswd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivCurrentPswdVisible.visibility = View.GONE
            binding.ivCurrentPswdInVisible.visibility = View.VISIBLE
            binding.etCurrentPswd.setSelection(binding.etCurrentPswd.text.toString().length)
        }
        binding.ivCurrentPswdInVisible.setOnClickListener {
            binding.etCurrentPswd.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivCurrentPswdVisible.visibility = View.VISIBLE
            binding.ivCurrentPswdInVisible.visibility = View.GONE
            binding.etCurrentPswd.setSelection(binding.etCurrentPswd.text.toString().length)
        }

        binding.ivNewPswdVisible.setOnClickListener {
            binding.etNewPswd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivNewPswdVisible.visibility = View.GONE
            binding.ivNewPswdInVisible.visibility = View.VISIBLE
            binding.etNewPswd.setSelection(binding.etNewPswd.text.toString().length)
        }
        binding.ivNewPswdInVisible.setOnClickListener {
            binding.etNewPswd.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivNewPswdVisible.visibility = View.VISIBLE
            binding.ivNewPswdInVisible.visibility = View.GONE
            binding.etNewPswd.setSelection(binding.etNewPswd.text.toString().length)
        }

        binding.ivConfirmPswdVisible.setOnClickListener {
            binding.etConfirmPswd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivConfirmPswdVisible.visibility = View.GONE
            binding.ivConfirmPswdInVisible.visibility = View.VISIBLE
            binding.etConfirmPswd.setSelection(binding.etConfirmPswd.text.toString().length)
        }
        binding.ivConfirmPswdInVisible.setOnClickListener {
            binding.etConfirmPswd.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivConfirmPswdVisible.visibility = View.VISIBLE
            binding.ivConfirmPswdInVisible.visibility = View.GONE
            binding.etConfirmPswd.setSelection(binding.etConfirmPswd.text.toString().length)
        }

    }

    private fun changePassword() {
        if (binding.etCurrentPswd.text.toString() == "") {
            binding.flCurrentPswd.error = "Current Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        } else if (binding.etCurrentPswd.text.toString().length < 8
            || !isValidPassword(binding.etCurrentPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = "Valid current Login Password is required"
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = ""
        } else if (binding.etNewPswd.text.toString() == "") {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = "New Login Password is required"
            binding.flConfirmPswd.error = ""
        } else if (binding.etNewPswd.text.toString().length < 8
            || !isValidPassword(binding.etNewPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = "Valid new Login Password is required"
            binding.flConfirmPswd.error = ""
        } else if (binding.etConfirmPswd.text.toString() == "") {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = "Confirm new Login Password is required"
        } else if (binding.etConfirmPswd.text.toString().length < 8
            || !isValidPassword(binding.etConfirmPswd.text.toString())
        ) {
            binding.flCurrentPswd.error = ""
            binding.flNewPswd.error = ""
            binding.flConfirmPswd.error = "Valid confirm new Login Password is required"
        }  else if (binding.etConfirmPswd.text.toString() != binding.etNewPswd.text.toString()
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
                        userID, coUserID,
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
                                val p = Properties()
                                p.putValue("userId", userID)
                                p.putValue("coUserId", coUserID)
                                BWSApplication.addToSegment("Password Changed", p, CONSTANTS.track)
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

    private fun isValidPassword(password: String?): Boolean {
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