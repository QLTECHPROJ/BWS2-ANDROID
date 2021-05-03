package com.brainwellnessspa.ProfileTwoModule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.ChangePinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityChangePinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePinActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePinBinding
    var USERID: String? = null
    var CoUserID: String? = null
    lateinit var activity: Activity

    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val CurrentPIN: String = binding.etCurrentPIN.getText().toString().trim()
            val NewPIN: String = binding.etNewPIN.getText().toString().trim()
            val ConfirmPIN: String = binding.etConfirmPIN.getText().toString().trim()
            if (CurrentPIN.equals("", ignoreCase = true) &&
                    NewPIN.equals("", ignoreCase = true) && ConfirmPIN.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (CurrentPIN.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (NewPIN.equals("", ignoreCase = true)) {
                binding.btnSave.setEnabled(false)
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (ConfirmPIN.equals("", ignoreCase = true)) {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pin)
        activity = this@ChangePinActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")

        binding.etCurrentPIN.addTextChangedListener(userTextWatcher)
        binding.etNewPIN.addTextChangedListener(userTextWatcher)
        binding.etConfirmPIN.addTextChangedListener(userTextWatcher)

        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnSave.setOnClickListener {
            changePin()
        }
    }

    private fun changePin() {
        if (binding.etCurrentPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = "Current Login PIN is required"
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
        } else if (!binding.etCurrentPIN.text.toString().equals("") && binding.etCurrentPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = "Please enter valid Current Login PIN"
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
        } else if (binding.etNewPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = "New Login PIN is required"
            binding.flConfirmPIN.error = ""
        } else if (!binding.etNewPIN.text.toString().equals("") && binding.etNewPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = "Please enter valid New Login PIN"
            binding.flConfirmPIN.error = ""
        } else if (binding.etConfirmPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "Confirm new Login PIN is required"
        } else if (!binding.etConfirmPIN.text.toString().equals("") && binding.etConfirmPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "Please enter valid Confirm new Login PIN"
        } else if (!binding.etConfirmPIN.text.toString().equals(binding.etNewPIN.text.toString())) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "New & Confirm Login PIN not match"
        } else {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                val listCall: Call<ChangePinModel> = APINewClient.getClient().getChangePin(USERID,
                        CoUserID, binding.etCurrentPIN.text.toString(),
                        binding.etConfirmPIN.text.toString())
                listCall.enqueue(object : Callback<ChangePinModel> {
                    override fun onResponse(call: Call<ChangePinModel>, response: Response<ChangePinModel>) {
                        try {
                            binding.flCurrentPIN.error = ""
                            binding.flNewPIN.error = ""
                            binding.flConfirmPIN.error = ""
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                            val listModel: ChangePinModel = response.body()!!
                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                finish()
                                BWSApplication.showToast(listModel.responseMessage, applicationContext)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, applicationContext)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ChangePinModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}