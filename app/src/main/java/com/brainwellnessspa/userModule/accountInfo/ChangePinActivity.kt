package com.brainwellnessspa.userModule.accountInfo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityChangePinBinding
import com.brainwellnessspa.userModule.models.ChangePinModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePinActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePinBinding
    var userID: String? = ""
    private var coUserID: String? = ""
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val currentPIN: String = binding.etCurrentPIN.text.toString().trim()
            val newPIN: String = binding.etNewPIN.text.toString().trim()
            val confirmPIN: String = binding.etConfirmPIN.text.toString().trim()
            if (currentPIN.equals("", ignoreCase = true) && newPIN.equals("", ignoreCase = true) && confirmPIN.equals("", ignoreCase = true)) {
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

            if (binding.etCurrentPIN.length() == 4) {
                binding.etNewPIN.isFocusable = true
                binding.etNewPIN.requestFocus()
            }

            if (binding.etCurrentPIN.length() == 4 && binding.etNewPIN.length() == 4) {
                binding.etConfirmPIN.isFocusable = true
                binding.etConfirmPIN.requestFocus()
            }

            if (binding.etCurrentPIN.length() == 4 && binding.etNewPIN.length() == 4 && binding.etConfirmPIN.length() == 4) {
                if (currentFocus != null) {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pin)
        activity = this@ChangePinActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val p = Properties()
        BWSApplication.addToSegment("Change Pin Screen Viewed", p, CONSTANTS.screen)
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

    @SuppressLint("SetTextI18n")
    private fun changePin() {
        if (binding.etCurrentPIN.text.toString() == "") {
            binding.txtCurrentPINError.text = "Please provide a current valid PIN"
            binding.txtCurrentPINError.visibility = View.VISIBLE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etCurrentPIN.text.toString() != "" && binding.etCurrentPIN.text.toString().length != 4) {
            binding.txtCurrentPINError.text = "Please provide a current valid PIN"
            binding.txtCurrentPINError.visibility = View.VISIBLE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etNewPIN.text.toString() == "") {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = "Please provide the latest PIN to login"
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etNewPIN.text.toString() != "" && binding.etNewPIN.text.toString().length != 4) {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = "Please provide the latest PIN to login"
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etConfirmPIN.text.toString() == "") {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please provide the latest PIN to login"
        } else if (binding.etConfirmPIN.text.toString() != "" && binding.etConfirmPIN.text.toString().length != 4) {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please provide the latest PIN to login"
        } else if (binding.etConfirmPIN.text.toString() != binding.etNewPIN.text.toString()) {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please check if both the PINs are same"
        } else {
            binding.txtCurrentPINError.visibility = View.GONE
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.GONE
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                val listCall: Call<ChangePinModel> = APINewClient.client.getChangePin(coUserID, binding.etCurrentPIN.text.toString(), binding.etConfirmPIN.text.toString())
                listCall.enqueue(object : Callback<ChangePinModel> {
                    override fun onResponse(call: Call<ChangePinModel>, response: Response<ChangePinModel>) {
                        try {
                            binding.txtCurrentPINError.visibility = View.GONE
                            binding.txtNewPINError.visibility = View.GONE
                            binding.txtConfirmPINError.visibility = View.GONE
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePinActivity)
                            val listModel: ChangePinModel = response.body()!!
                            when {
                                listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    val p = Properties()
                                    BWSApplication.addToSegment("Login Pin Changed", p, CONSTANTS.track)
                                    finish()
                                    BWSApplication.showToast(listModel.responseMessage, activity)
                                }
                                listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
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
                                }
                                else -> {
                                    BWSApplication.showToast(listModel.responseMessage, activity)
                                }
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