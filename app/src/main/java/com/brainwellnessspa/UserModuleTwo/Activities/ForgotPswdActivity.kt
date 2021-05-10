package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPasswordModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.databinding.ActivityForgotPswdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPswdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPswdBinding
    private lateinit var dialog: Dialog
    lateinit var activity: Activity

    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val Email: String = binding.etEmail.getText().toString().trim()
            if (Email.equals("", ignoreCase = true)) {
                binding.btnResetPswd.setEnabled(false)
                binding.btnResetPswd.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnResetPswd.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnResetPswd.setEnabled(true)
                binding.btnResetPswd.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnResetPswd.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pswd)
        activity = this@ForgotPswdActivity
        binding.llBack.setOnClickListener {
            finish()
        }
        binding.etEmail.addTextChangedListener(userTextWatcher)

        binding.btnResetPswd.setOnClickListener {
            prepareData()
        }
    }

    private fun prepareData() {
        if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
            binding.flEmail.error = "Email address is required"
        } else if (!binding.etEmail.text.toString().equals("")
            && !BWSApplication.isEmailValid(binding.etEmail.text.toString())
        ) {
            binding.flEmail.error = "Valid Email address is required"
        } else {
            binding.flEmail.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    this@ForgotPswdActivity
                )
                val listCall: Call<ForgotPasswordModel> =
                    APINewClient.getClient().getForgotPassword(binding.etEmail.text.toString())
                listCall.enqueue(object : Callback<ForgotPasswordModel> {
                    override fun onResponse(
                        call: Call<ForgotPasswordModel>,
                        response: Response<ForgotPasswordModel>
                    ) {
                        try {
                            binding.flEmail.error = ""
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                this@ForgotPswdActivity
                            )
                            val listModel: ForgotPasswordModel = response.body()!!
                            if (listModel.responseCode.equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                dialog = Dialog(this@ForgotPswdActivity)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(R.layout.alert_popup_layout)
                                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.window!!.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                val tvGoBack: TextView = dialog.findViewById(R.id.tvGoBack)
                                val tvSubTitle: TextView = dialog.findViewById(R.id.tvSubTitle)
                                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss()
                                    }
                                    false
                                }
                                tvSubTitle.text = listModel.responseMessage
                                tvGoBack.setOnClickListener {
                                    dialog.dismiss()
                                    finish()
                                }
                                dialog.show()
                                dialog.setCancelable(false)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ForgotPasswordModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            this@ForgotPswdActivity
                        )
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}