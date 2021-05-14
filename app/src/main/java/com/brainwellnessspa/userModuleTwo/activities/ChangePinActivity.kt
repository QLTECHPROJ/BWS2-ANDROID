package com.brainwellnessspa.userModuleTwo.activities

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
import com.brainwellnessspa.userModuleTwo.models.ChangePinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityChangePinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePinActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePinBinding
    var userID: String? = null
    private var coUserID: String? = null
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val currentPIN: String = binding.etCurrentPIN.text.toString().trim()
            val newPIN: String = binding.etNewPIN.text.toString().trim()
            val confirmPIN: String = binding.etConfirmPIN.text.toString().trim()
            if (currentPIN.equals("", ignoreCase = true) &&
                newPIN.equals("", ignoreCase = true) && confirmPIN.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (currentPIN.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (newPIN.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (confirmPIN.equals("", ignoreCase = true)) {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pin)
        activity = this@ChangePinActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")

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
        if (binding.etCurrentPIN.text.toString() == "") {
            binding.flCurrentPIN.error = "Current Login PIN is required"
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
        } else if (binding.etCurrentPIN.text.toString() != "" && binding.etCurrentPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = "Please enter valid Current Login PIN"
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
        } else if (binding.etNewPIN.text.toString() == "") {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = "New Login PIN is required"
            binding.flConfirmPIN.error = ""
        } else if (binding.etNewPIN.text.toString() != "" && binding.etNewPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = "Please enter valid New Login PIN"
            binding.flConfirmPIN.error = ""
        } else if (binding.etConfirmPIN.text.toString() == "") {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "Confirm new Login PIN is required"
        } else if (binding.etConfirmPIN.text.toString() != "" && binding.etConfirmPIN.text.toString().length != 4) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "Please enter valid Confirm new Login PIN"
        } else if (binding.etConfirmPIN.text.toString() != binding.etNewPIN.text.toString()) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "New & Confirm Login PIN not match"
        } else {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                val listCall: Call<ChangePinModel> = APINewClient.getClient().getChangePin(userID,
                        coUserID, binding.etCurrentPIN.text.toString(),
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
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
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
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}