package com.brainwellnessspa.userModule.coUserModule

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
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityCouserSetupPinBinding
import com.brainwellnessspa.userModule.models.SetLoginPinModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouserSetupPinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCouserSetupPinBinding
    var mainAccountID: String? = ""
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val newPIN: String = binding.etNewPIN.text.toString().trim()
            val confirmPIN: String = binding.etConfirmPIN.text.toString().trim()
            if (newPIN.equals("", ignoreCase = true) && confirmPIN.equals("", ignoreCase = true)) {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (newPIN.equals("", ignoreCase = true)) {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (confirmPIN.equals("", ignoreCase = true)) {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnDone.isEnabled = true
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_couser_setup_pin)
        activity = this@CouserSetupPinActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.etNewPIN.addTextChangedListener(userTextWatcher)
        binding.etConfirmPIN.addTextChangedListener(userTextWatcher)

        binding.btnDone.setOnClickListener {
            setupPin(activity)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setupPin(activity: Activity) {
        if (binding.etNewPIN.text.toString() == "") {
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = "Please provide the latest PIN to login"
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etNewPIN.text.toString() != "" && binding.etNewPIN.text.toString().length != 4) {
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = "Please provide the latest PIN to login"
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etConfirmPIN.text.toString() == "") {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please provide the latest PIN to login"
        } else if (binding.etConfirmPIN.text.toString() != "" && binding.etConfirmPIN.text.toString().length != 4) {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please provide the latest PIN to login"
        } else if (binding.etConfirmPIN.text.toString() != binding.etNewPIN.text.toString()) {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = "Please check if both the PINs are same"
        } else {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.GONE
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall: Call<SetLoginPinModel> = APINewClient.client.getSetLoginPin(mainAccountID, binding.etConfirmPIN.text.toString())
                listCall.enqueue(object : Callback<SetLoginPinModel> {
                    override fun onResponse(call: Call<SetLoginPinModel>, response: Response<SetLoginPinModel>) {
                        try {
                            binding.txtNewPINError.visibility = View.GONE
                            binding.txtConfirmPINError.visibility = View.GONE
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val listModel: SetLoginPinModel = response.body()!!
                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                val dialog = Dialog(applicationContext)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(R.layout.add_couser_continue_layout)
                                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                val mainLayout = dialog.findViewById<ConstraintLayout>(R.id.mainLayout)
                                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss()
                                        return@setOnKeyListener true
                                    }
                                    false
                                }

                                mainLayout.setOnClickListener {
                                    val intent = Intent(applicationContext, UserDetailActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    dialog.dismiss()
                                }

                                dialog.show()
                                dialog.setCancelable(true)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SetLoginPinModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
    }
}