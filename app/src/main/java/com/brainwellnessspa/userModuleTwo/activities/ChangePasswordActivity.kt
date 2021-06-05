package com.brainwellnessspa.userModuleTwo.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
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

            if (currentPaswd.equals("", ignoreCase = true)) {
                binding.ivCurrentPswdVisible.isClickable = false
                binding.ivCurrentPswdVisible.isEnabled = false
                binding.ivCurrentPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.light_gray),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivCurrentPswdVisible.isEnabled = false
                binding.ivCurrentPswdInVisible.isClickable = false
                binding.ivCurrentPswdInVisible.isEnabled = false
            } else {
                binding.ivCurrentPswdVisible.isClickable = true
                binding.ivCurrentPswdVisible.isEnabled = true
                binding.ivCurrentPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivCurrentPswdInVisible.isClickable = true
                binding.ivCurrentPswdInVisible.isEnabled = true
            }

            if (newPswd.equals("", ignoreCase = true)) {
                binding.ivNewPswdVisible.isClickable = false
                binding.ivNewPswdVisible.isEnabled = false
                binding.ivNewPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.light_gray),
                    PorterDuff.Mode.SRC_IN
                )

                binding.ivNewPswdInVisible.isClickable = false
                binding.ivNewPswdInVisible.isEnabled = false
            } else {
                binding.ivNewPswdVisible.isClickable = true
                binding.ivNewPswdVisible.isEnabled = true
                binding.ivNewPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivNewPswdInVisible.isClickable = true
                binding.ivNewPswdInVisible.isEnabled = true
            }

            if (confirmPswd.equals("", ignoreCase = true)) {
                binding.ivConfirmPswdVisible.isClickable = false
                binding.ivConfirmPswdVisible.isEnabled = false
                binding.ivConfirmPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.light_gray),
                    PorterDuff.Mode.SRC_IN
                )

                binding.ivConfirmPswdInVisible.isClickable = false
                binding.ivConfirmPswdInVisible.isEnabled = false
            } else {
                binding.ivConfirmPswdVisible.isClickable = true
                binding.ivConfirmPswdVisible.isEnabled = true
                binding.ivConfirmPswdVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivConfirmPswdInVisible.isClickable = true
                binding.ivConfirmPswdInVisible.isEnabled = true
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

        if (binding.etCurrentPswd.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivCurrentPswdVisible.isClickable = false
            binding.ivCurrentPswdVisible.isEnabled = false
            binding.ivCurrentPswdVisible.setColorFilter(
                ContextCompat.getColor(activity, R.color.light_gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.ivCurrentPswdInVisible.isClickable = false
            binding.ivCurrentPswdInVisible.isEnabled = false
        }

        if (binding.etNewPswd.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivNewPswdVisible.isClickable = false
            binding.ivNewPswdVisible.isEnabled = false
            binding.ivNewPswdVisible.setColorFilter(
                ContextCompat.getColor(activity, R.color.light_gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.ivNewPswdInVisible.isClickable = false
            binding.ivNewPswdInVisible.isEnabled = false
        }

        if (binding.etConfirmPswd.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivConfirmPswdVisible.isClickable = false
            binding.ivConfirmPswdVisible.isEnabled = false
            binding.ivConfirmPswdVisible.setColorFilter(
                ContextCompat.getColor(activity, R.color.light_gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.ivConfirmPswdInVisible.isClickable = false
            binding.ivConfirmPswdInVisible.isEnabled = false
        }


        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnSave.setOnClickListener {
            changePassword()
        }

        binding.ivCurrentPswdVisible.setOnClickListener {
            binding.etCurrentPswd.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
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
            binding.etConfirmPswd.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
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

    @SuppressLint("SetTextI18n")
    private fun changePassword() {
        if (binding.etCurrentPswd.text.toString() == "") {
            binding.txtCurrentPswdError.text = "Please provide the current password"
            binding.txtCurrentPswdError.visibility = View.VISIBLE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.GONE
        } else if (binding.etCurrentPswd.text.toString().length < 8
            || !isValidPassword(binding.etCurrentPswd.text.toString())
        ) {
            binding.txtCurrentPswdError.text = "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
            binding.txtCurrentPswdError.visibility = View.VISIBLE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.GONE
        } else if (binding.etNewPswd.text.toString() == "") {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.VISIBLE
            binding.txtNewPswdError.text = "Please provide the new password"
            binding.txtConfirmPswdError.visibility = View.GONE
        } else if (binding.etNewPswd.text.toString().length < 8
            || !isValidPassword(binding.etNewPswd.text.toString())
        ) {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.VISIBLE
            binding.txtNewPswdError.text =
                "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
            binding.txtConfirmPswdError.visibility = View.GONE
        } else if (binding.etConfirmPswd.text.toString() == "") {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.VISIBLE
            binding.txtConfirmPswdError.text = "Please provide the confirm new password"
        } else if (binding.etConfirmPswd.text.toString().length < 8
            || !isValidPassword(binding.etConfirmPswd.text.toString())
        ) {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.VISIBLE
            binding.txtConfirmPswdError.text =
                "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
        } else if (binding.etConfirmPswd.text.toString() != binding.etNewPswd.text.toString()
        ) {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.VISIBLE
            binding.txtConfirmPswdError.text = "Please check if both the passwords are same"
        } else {
            binding.txtCurrentPswdError.visibility = View.GONE
            binding.txtNewPswdError.visibility = View.GONE
            binding.txtConfirmPswdError.visibility = View.GONE
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
                            binding.txtCurrentPswdError.visibility = View.GONE
                            binding.txtNewPswdError.visibility = View.GONE
                            binding.txtConfirmPswdError.visibility = View.GONE
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
        val passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}