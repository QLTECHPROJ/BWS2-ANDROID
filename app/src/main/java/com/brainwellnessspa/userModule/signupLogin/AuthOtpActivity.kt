package com.brainwellnessspa.userModule.signupLogin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
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
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityAuthOtpBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.SmsReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthOtpActivity : AppCompatActivity(), SmsReceiver.OTPReceiveListener {
    lateinit var binding: ActivityAuthOtpBinding
    lateinit var activity: Activity
    private var smsReceiver: SmsReceiver? = null
    private lateinit var editTexts: Array<EditText>
    private var tvSendOTPbool = true
    var mobileNo: String? = null
    var countryCode: String? = null
    var signupFlag: String? = null
    var name: String? = null
    var email: String? = null
    var countryShortName: String? = null
    private var receiver: BroadcastReceiver? = null
    var fcmId: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth_otp)
        activity = this@AuthOtpActivity

        if (intent != null) {
            mobileNo = intent.getStringExtra(CONSTANTS.mobileNumber)
            countryCode = intent.getStringExtra(CONSTANTS.countryCode)
            signupFlag = intent.getStringExtra(CONSTANTS.signupFlag)
            name = intent.getStringExtra(CONSTANTS.name)
            email = intent.getStringExtra(CONSTANTS.email)
            countryShortName = intent.getStringExtra(CONSTANTS.countryShortName)
        }

        binding.llBack.setOnClickListener {
            if (signupFlag.equals("1", ignoreCase = true)) {
                val i = Intent(activity, SignUpActivity::class.java)
                i.putExtra("mobileNo", mobileNo)
                i.putExtra("countryCode", countryCode)
                i.putExtra("name", name)
                i.putExtra("email", email)
                i.putExtra("countryShortName", countryShortName)
                startActivity(i)
                finish()
            } else {
                val i = Intent(activity, SignInActivity::class.java)
                i.putExtra("mobileNo", mobileNo)
                i.putExtra("countryCode", countryCode)
                i.putExtra("name", name)
                i.putExtra("email", email)
                i.putExtra("countryShortName", countryShortName)
                startActivity(i)
                finish()
            }
        }

        binding.tvSendCodeText.text = "We sent an SMS with a 4-digit code to \n+$countryCode $mobileNo"

        editTexts = arrayOf(binding.edtOTP1, binding.edtOTP2, binding.edtOTP3, binding.edtOTP4)
        binding.edtOTP1.addTextChangedListener(PinTextWatcher(activity, binding, editTexts, 0, tvSendOTPbool))
        binding.edtOTP2.addTextChangedListener(PinTextWatcher(activity, binding, editTexts, 1, tvSendOTPbool))
        binding.edtOTP3.addTextChangedListener(PinTextWatcher(activity, binding, editTexts, 2, tvSendOTPbool))
        binding.edtOTP4.addTextChangedListener(PinTextWatcher(activity, binding, editTexts, 3, tvSendOTPbool))
        binding.edtOTP1.setOnKeyListener(PinOnKeyListener(0))
        binding.edtOTP2.setOnKeyListener(PinOnKeyListener(1))
        binding.edtOTP3.setOnKeyListener(PinOnKeyListener(2))
        binding.edtOTP4.setOnKeyListener(PinOnKeyListener(3))
        startSMSListener()

        binding.btnSendCode.setOnClickListener {
            if (binding.edtOTP1.text.toString().equals("", ignoreCase = true) && binding.edtOTP2.text.toString().equals("", ignoreCase = true) && binding.edtOTP3.text.toString().equals("", ignoreCase = true) && binding.edtOTP4.text.toString().equals("", ignoreCase = true)) {
                binding.txtError.visibility = View.VISIBLE
                binding.txtError.text = "Please enter OTP"
            } else {
                binding.txtError.visibility = View.GONE
                binding.txtError.text = ""
                authotpUserAcess()
            }
        }

        binding.llEditNumber.setOnClickListener {
            if (signupFlag.equals("1", ignoreCase = true)) {
                val i = Intent(activity, SignUpActivity::class.java)
                i.putExtra("mobileNo", mobileNo)
                i.putExtra("countryCode", countryCode)
                i.putExtra("name", name)
                i.putExtra("email", email)
                i.putExtra("countryShortName", countryShortName)
                startActivity(i)
                finish()
            } else {
                val i = Intent(activity, SignInActivity::class.java)
                i.putExtra("mobileNo", mobileNo)
                i.putExtra("countryCode", countryCode)
                i.putExtra("name", name)
                i.putExtra("email", email)
                i.putExtra("countryShortName", countryShortName)
                startActivity(i)
                finish()
            }
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

    @SuppressLint("HardwareIds")
    fun authotpUserAcess() {
        if (BWSApplication.isNetworkConnected(this)) {
            val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")!!
            if (TextUtils.isEmpty(fcmId)) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this) { task: Task<InstallationTokenResult> ->
                    val newToken = task.result!!.token
                    Log.e("newToken", newToken)
                    val editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
                    editor.putString(CONSTANTS.Token, newToken) //Friend
                    editor.apply()
                    editor.commit()
                }
                val sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
                fcmId = sharedPreferences3.getString(CONSTANTS.Token, "")!!
            }

            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)

            val listCall: Call<AuthOtpModel> = APINewClient.getClient().getAuthOtpAccess(binding.edtOTP1.text.toString() + "" + binding.edtOTP2.text.toString() + "" + binding.edtOTP3.text.toString() + "" + binding.edtOTP4.text.toString(), CONSTANTS.FLAG_ONE, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID), countryCode, mobileNo, signupFlag, name, email, fcmId)
            listCall.enqueue(object : Callback<AuthOtpModel> {
                override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: AuthOtpModel = response.body()!!
                        if (listModel.ResponseCode == "200") {
                            if (signupFlag.equals("1", ignoreCase = true)) {
                                val i = Intent(activity, EmailVerifyActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                if (listModel.ResponseData.isAssessmentCompleted.equals("0", ignoreCase = true)) {
                                    val intent = Intent(activity, AssProcessActivity::class.java)
                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                    startActivity(intent)
                                    finish()
                                } else if (listModel.ResponseData.isProfileCompleted.equals("0", ignoreCase = true)) {
                                    val intent = Intent(activity, WalkScreenActivity::class.java)
                                    intent.putExtra(CONSTANTS.ScreenView, "2")
                                    startActivity(intent)
                                    finish()
                                } else if (listModel.ResponseData.AvgSleepTime.equals("", ignoreCase = true)) {
                                    val intent = Intent(activity, SleepTimeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else if (listModel.ResponseData.isProfileCompleted.equals("1", ignoreCase = true) && listModel.ResponseData.isAssessmentCompleted.equals("1", ignoreCase = true)) {
                                    val intent = Intent(activity, BottomNavigationActivity::class.java)
                                    intent.putExtra("IsFirst", "0")
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            val p = Properties()
                            p.putValue("userId", "")
                            p.putValue("name", "")
                            p.putValue("mobileNo", listModel.ResponseData.Mobile)
                            p.putValue("countryCode", countryCode)
                            p.putValue("countryName", "")
                            p.putValue("countryShortName", "")
                            p.putValue("email", "")
                            BWSApplication.addToSegment("", p, CONSTANTS.track)

                            val shared = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                            editor.apply()

                            val sharded = activity.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val edited = sharded.edit()
                            edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            val selectedCategoriesTitle = arrayListOf<String>()
                            val selectedCategoriesName = arrayListOf<String>()
                            val gson = Gson()
                            for (i in listModel.ResponseData.AreaOfFocus) {
                                selectedCategoriesTitle.add(i.MainCat)
                                selectedCategoriesName.add(i.RecommendedCat)
                            }
                            edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle)) //Friend
                            edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName)) //Friend
                            edited.apply()
                        }
                        BWSApplication.showToast(listModel.ResponseMessage, activity)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, null, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onResume() {
        receiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, IntentFilter("otp"))
        }
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

    class PinTextWatcher internal constructor(val activity: Activity, val binding: ActivityAuthOtpBinding, private var editTexts: Array<EditText>, private val currentIndex: Int, private var tvSendOTPbool: Boolean) : TextWatcher {
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

            /* Detect paste event and set first char */if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
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
                for (editText in editTexts) if (editText.text.toString().trim { it <= ' ' }.isEmpty()) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity.currentFocus != null) {
                val inputMethodManager: InputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst = true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    class PinOnKeyListener internal constructor(private val currentIndex: Int) : View.OnKeyListener {
        val act = AuthOtpActivity()
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action === KeyEvent.ACTION_DOWN) {
                if (act.editTexts[currentIndex].text.toString().isEmpty() && currentIndex != 0) act.editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }

    override fun onBackPressed() {
        if (signupFlag.equals("1", ignoreCase = true)) {
            val i = Intent(activity, SignUpActivity::class.java)
            i.putExtra("mobileNo", mobileNo)
            i.putExtra("countryCode", countryCode)
            i.putExtra("name", name)
            i.putExtra("email", email)
            i.putExtra("countryShortName", countryShortName)
            startActivity(i)
            finish()
        } else {
            val i = Intent(activity, SignInActivity::class.java)
            i.putExtra("mobileNo", mobileNo)
            i.putExtra("countryCode", countryCode)
            i.putExtra("name", name)
            i.putExtra("email", email)
            i.putExtra("countryShortName", countryShortName)
            startActivity(i)
            finish()
        }
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