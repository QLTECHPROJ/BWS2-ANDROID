package com.brainwellnessspa.userModule.signupLogin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityAuthOtpBinding
import com.brainwellnessspa.utility.SmsReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task

class AuthOtpActivity : AppCompatActivity(), SmsReceiver.OTPReceiveListener {
    lateinit var binding: ActivityAuthOtpBinding
    lateinit var activity: Activity
    private var smsReceiver: SmsReceiver? = null
    private lateinit var editTexts: Array<EditText>
    private var tvSendOTPbool = true
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_otp)
        activity = this@AuthOtpActivity

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.tvSendCodeText.text = getString(R.string.sms_code_quotes)/*$Code$MobileNo*/

        editTexts = arrayOf(binding.edtOTP1, binding.edtOTP2, binding.edtOTP3, binding.edtOTP4)
        binding.edtOTP1.addTextChangedListener(
            PinTextWatcher(
                activity,
                binding,
                editTexts,
                0,
                tvSendOTPbool
            )
        )
        binding.edtOTP2.addTextChangedListener(
            PinTextWatcher(
                activity,
                binding,
                editTexts,
                1,
                tvSendOTPbool
            )
        )
        binding.edtOTP3.addTextChangedListener(
            PinTextWatcher(
                activity,
                binding,
                editTexts,
                2,
                tvSendOTPbool
            )
        )
        binding.edtOTP4.addTextChangedListener(
            PinTextWatcher(
                activity,
                binding,
                editTexts,
                3,
                tvSendOTPbool
            )
        )
        binding.edtOTP1.setOnKeyListener(PinOnKeyListener(0))
        binding.edtOTP2.setOnKeyListener(PinOnKeyListener(1))
        binding.edtOTP3.setOnKeyListener(PinOnKeyListener(2))
        binding.edtOTP4.setOnKeyListener(PinOnKeyListener(3))
        startSMSListener()

        binding.btnSendCode.setOnClickListener {
            val i = Intent(activity, EmailVerifyActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SmsReceiver()
            smsReceiver!!.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task: Task<Void> = client.startSmsRetriever()
            task.addOnSuccessListener { }
            task.addOnFailureListener { /* Fail to start API */ }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        receiver?.let { LocalBroadcastManager.getInstance(this).registerReceiver(it, IntentFilter("otp")) }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        receiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
        }
    }

    class PinTextWatcher internal constructor(
        val activity: Activity,
        val binding: ActivityAuthOtpBinding,
        private var editTexts: Array<EditText>,
        private val currentIndex: Int,
        private var tvSendOTPbool: Boolean
    ) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            val otp1: String = binding.edtOTP1.text.toString().trim()
            val otp2: String = binding.edtOTP2.text.toString().trim()
            val otp3: String = binding.edtOTP3.text.toString().trim()
            val otp4: String = binding.edtOTP4.text.toString().trim()
            if (otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty()) {
                binding.btnSendCode.isEnabled = true
                binding.btnSendCode.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendCode.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnSendCode.isEnabled = false
                binding.btnSendCode.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text =
                text[0].toString() // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.isEmpty()) {
                if (!tvSendOTPbool) {
                    editTexts[0].requestFocus()
                } else {
                    moveToPrevious()
                }
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            get() {
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity.currentFocus != null) {
                val inputMethodManager: InputMethodManager =
                    activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    class PinOnKeyListener internal constructor(private val currentIndex: Int) :
        View.OnKeyListener {
        val act = AuthOtpActivity()
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action === KeyEvent.ACTION_DOWN) {
                if (act.editTexts[currentIndex].text.toString()
                        .isEmpty() && currentIndex != 0
                ) act.editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOTPReceived(otp: String?) {
        var splited = arrayOfNulls<String>(0)
        if (otp != null) {
            splited = when {
                otp.startsWith("[#] Your OTP is") -> {
                    otp.split(" ").toTypedArray()
                }
                otp.startsWith("(#) Your OTP is") -> {
                    otp.split(" ").toTypedArray()
                }
                otp.startsWith("<#> Your OTP is") -> {
                    otp.split(" ").toTypedArray()
                }
                otp.startsWith("?<#?> Your OTP is") -> {
                    otp.split(" ").toTypedArray()
                }
                else -> {
                    otp.split(" ").toTypedArray()
                }
            }
        }
        val message = splited[4]
        binding.edtOTP1.setText(message!![0].toString())
        binding.edtOTP2.setText(message[1].toString())
        binding.edtOTP3.setText(message[2].toString())
        binding.edtOTP4.setText(message[3].toString())
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
    }


    override fun onOTPTimeOut() {
//        showToast("OTP Time out");
    }

    override fun onOTPReceivedError(error: String?) {
//        showToast(error);
    }

}