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
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityCouserSetupPinBinding
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.userModule.models.SetLoginPinModel

import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouserSetupPinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCouserSetupPinBinding
    var mainAccountID: String? = ""
    var userId: String? = ""
    private var subUserId: String? = ""
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val newPIN: String = binding.etNewPIN.text.toString().trim()
            val confirmPIN: String = binding.etConfirmPIN.text.toString().trim()
            if (newPIN == "" && confirmPIN == "") {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (newPIN == "") {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (confirmPIN == "") {
                binding.btnDone.isEnabled = false
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnDone.isEnabled = true
                binding.btnDone.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                binding.btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }

            if (binding.etNewPIN.length() == 4) {
                binding.etConfirmPIN.isFocusable = true
                binding.etConfirmPIN.requestFocus()
            }

            if (binding.etNewPIN.length() == 4 && binding.etConfirmPIN.length() == 4) {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_couser_setup_pin)
        activity = this@CouserSetupPinActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val p = Properties()
        addToSegment("Set Up Pin Screen Viewed", p, CONSTANTS.screen)
        if (intent.extras != null) {
            subUserId = intent.getStringExtra("subUserId")
        }
        binding.llBack.setOnClickListener {
            if (IsFirstClick.equals("1")) {
                finishAffinity()
            } else {
                finish()
            }
        }

        binding.etNewPIN.addTextChangedListener(userTextWatcher)
        binding.etConfirmPIN.addTextChangedListener(userTextWatcher)

        binding.btnDone.setOnClickListener {
            setupPin(activity)
        }

        binding.ivInfo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvAction = dialog.findViewById<TextView>(R.id.tvAction)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            val llDiscalimer = dialog.findViewById<LinearLayout>(R.id.llDiscalimer)
            tvTitle.text = "Why Set Up PIN ?"
            tvDesc.text = "This PIN will help you log in to your personal profile."
            tvAction.text = getString(R.string.ok)
            llDiscalimer.visibility = View.GONE
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            dialog.setCancelable(true)
        }

    }

    override fun onBackPressed() {
        if (IsFirstClick.equals("1")) {
            finishAffinity()
        } else {
            finish()
        }
    }

    private fun setupPin(activity: Activity) {
        if (binding.etNewPIN.text.toString() == "") {
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = getString(R.string.pls_provide_latest_pin)
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etNewPIN.text.toString() != "" && binding.etNewPIN.text.toString().length < 4) {
            binding.txtNewPINError.visibility = View.VISIBLE
            binding.txtNewPINError.text = getString(R.string.pls_provide_latest_pin)
            binding.txtConfirmPINError.visibility = View.GONE
        } else if (binding.etConfirmPIN.text.toString() == "") {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = getString(R.string.pls_provide_latest_pin)
        } else if (binding.etConfirmPIN.text.toString() != "" && binding.etConfirmPIN.text.toString().length < 4) {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = getString(R.string.pls_provide_latest_pin)
        } else if (binding.etConfirmPIN.text.toString() != binding.etNewPIN.text.toString()) {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.VISIBLE
            binding.txtConfirmPINError.text = getString(R.string.check_both_pin_same_or_not)
        } else {
            binding.txtNewPINError.visibility = View.GONE
            binding.txtConfirmPINError.visibility = View.GONE

            if (isNetworkConnected(this)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                var mainUserId = ""
                mainUserId = if (subUserId.equals("")) {
                    userId.toString()
                } else {
                    subUserId.toString()
                }
                val listCall: Call<SetLoginPinModel> = APINewClient.client.getSetLoginPin(mainUserId, binding.etConfirmPIN.text.toString())

                listCall.enqueue(object : Callback<SetLoginPinModel> {
                    override fun onResponse(call: Call<SetLoginPinModel>, response: Response<SetLoginPinModel>) {
                        try {
                            binding.txtNewPINError.visibility = View.GONE
                            binding.txtConfirmPINError.visibility = View.GONE
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val listModel: SetLoginPinModel? = response.body()
                            if (listModel != null) {
                                if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                    showToast(listModel.responseMessage, activity)
                                    if (mainAccountID == listModel.responseData?.userId) {
                                        val intent = Intent(applicationContext, WalkScreenActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        intent.putExtra(CONSTANTS.ScreenView, "4")
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        if (comeHomeScreen.equals("1")) {
                                            finish()
                                        } else {
                                            val intent = Intent(applicationContext, UserListActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted))) {
                                    callDelete403(activity, listModel.responseMessage)
                                } else {
                                    showToast(listModel.responseMessage, activity)
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SetLoginPinModel>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
    }
}