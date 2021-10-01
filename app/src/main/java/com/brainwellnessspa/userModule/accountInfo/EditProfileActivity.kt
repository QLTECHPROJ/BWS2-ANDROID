package com.brainwellnessspa.userModule.accountInfo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.IsLock
import com.brainwellnessspa.BWSApplication.callIdentify
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEditProfileBinding
import com.brainwellnessspa.areaOfFocusModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.EditProfileModel

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var userName: String? = ""
    var userCalendar: String? = ""
    var copyUserCalendar: String? = ""
    var userMobileNumber: String? = ""
    var userEmail: String? = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var birthYear = 0
    private var ageYear: Int = 0
    private var ageMonth: Int = 0
    var ageDate: Int = 0
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val ckName: String = binding.etUser.text.toString().trim()
            val ckCalendar: String = binding.etCalendar.text.toString().trim()
            val ckNumber: String = binding.etMobileNumber.text.toString().trim()
            val ckEmail: String = binding.etEmail.text.toString().trim()
            when {
                ckName.equals(userName, ignoreCase = true) && ckCalendar.equals(userCalendar, ignoreCase = true) && ckNumber.equals(userMobileNumber, ignoreCase = true) && ckEmail.equals(userEmail, ignoreCase = true) -> {
                    checkBtnStatus(0)
                }
                ckName.equals("", ignoreCase = true) -> {
                    checkBtnStatus(0)
                }
                ckCalendar.equals("", ignoreCase = true) -> {
                    checkBtnStatus(0)
                }
                ckNumber.equals("", ignoreCase = true) -> {
                    checkBtnStatus(0)
                }
                ckEmail.equals("", ignoreCase = true) -> {
                    checkBtnStatus(0)
                }
                else -> {
                    checkBtnStatus(1)
                }
            }

            if (ckNumber.equals("", ignoreCase = true)) {
                binding.ivCheckNumber.visibility = View.GONE
            } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 || binding.etMobileNumber.text.toString().length > 10) {
                binding.ivCheckNumber.visibility = View.GONE
            } else {
                binding.ivCheckNumber.visibility = View.VISIBLE
            }

        /*    if (ckEmail.equals("", ignoreCase = true)) {
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!ckEmail.equals("", ignoreCase = true) && !ckEmail.isEmailValid()) {
                binding.ivCheckEmail.visibility = View.GONE
            } else {
                binding.ivCheckEmail.visibility = View.VISIBLE
            }*/
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        activity = this@EditProfileActivity
        val p = Properties()
        BWSApplication.addToSegment("Edit Profile Screen View", p, CONSTANTS.screen)

        binding.ivCheckNumber.visibility = View.VISIBLE
//        binding.ivCheckEmail.visibility = View.VISIBLE
        binding.etUser.addTextChangedListener(userTextWatcher)
        binding.etCalendar.addTextChangedListener(userTextWatcher)
        binding.etMobileNumber.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)
        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
            finish()
        }

        profileViewData(applicationContext)
        binding.etCalendar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (binding.etCalendar.text.toString().isNotEmpty()) {
                    val dialog = Dialog(this@EditProfileActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.cancel_membership)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@EditProfileActivity, R.color.transparent_white)))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                    val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                    val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                    val btn = dialog.findViewById<Button>(R.id.Btn)
                    tvTitle.text = ""
                    tvSubTitle.text = "Changing Date of Birth may cause change in your age category and if this happens, you will be redirected to select your sleep time and area of focus again. Do you wish to continue ?"
                    tvGoBack.text = "No"
                    tvTitle.visibility = View.GONE
                    tvGoBack.visibility = View.GONE
                    btn.text = "Ok"
                    dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss()
                            return@setOnKeyListener true
                        }
                        false
                    }

                    btn.setOnClickListener {
                        dialog.dismiss()
                        setDate()
                    }
                    /* This click event is called when not cancelling subscription */
                    tvGoBack.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    dialog.setCancelable(false)
                }
            }
        }
        binding.btnSave.setOnClickListener {
            profileUpdate()
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    @SuppressLint("SetTextI18n")
    fun profileUpdate() {
        try {
            if (BWSApplication.isNetworkConnected(applicationContext)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                var dob = ""
                if (binding.etCalendar.text.toString().isNotEmpty()) {
                    dob = binding.etCalendar.text.toString()
                    var spf = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT)
                    var newDate = Date()
                    try {
                        newDate = spf.parse(dob)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    spf = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
                    dob = spf.format(newDate)
                }

                if (binding.etUser.text.toString().equals("")) {
                    binding.txtNameError.text = getString(R.string.pls_provide_a_name)
                    binding.txtNameError.visibility = View.VISIBLE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etCalendar.text.toString().equals("")) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.VISIBLE
                    binding.txtDobError.text = "Please provide a dob"
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etMobileNumber.text.toString().equals("")) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.VISIBLE
                    binding.txtNumberError.text = "Please provide a mobile number"
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 || binding.etMobileNumber.text.toString().length > 10) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.VISIBLE
                    binding.txtNumberError.text = getString(R.string.valid_mobile_number)
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etEmail.text.toString().equals("")) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.VISIBLE
                    binding.txtEmailError.text = "Please provide a email address"
                } else if (!binding.etEmail.text.toString().isEmailValid()) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.VISIBLE
                    binding.txtEmailError.text = "Please provide a valid email address"
                } else {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.GONE
                    val listCall = APINewClient.client.getEditProfile(userId, coUserId, binding.etUser.text.toString(), dob, binding.etMobileNumber.text.toString(), binding.etEmail.text.toString())
                    listCall.enqueue(object : Callback<EditProfileModel> {
                        override fun onResponse(call: Call<EditProfileModel>, response: Response<EditProfileModel>) {
                            val viewModel = response.body()
                            when {
                                viewModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    when {
                                        viewModel.responseData!!.ageSlabChange.equals("0") -> {
                                            BWSApplication.showToast(viewModel.responseMessage, activity)
                                            profileViewData(this@EditProfileActivity)
                                            val shared = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                            val editor = shared.edit()
                                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, viewModel.responseData!!.name)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_DOB, viewModel.responseData!!.dob)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, viewModel.responseData!!.email)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, viewModel.responseData!!.phoneNumber)
                                            editor.apply()

                                            callIdentify(applicationContext)
                                            val p = Properties()
                                            p.putValue("name", viewModel.responseData!!.name)
                                            p.putValue("dob", viewModel.responseData!!.dob)
                                            p.putValue("mobileNo", viewModel.responseData!!.phoneNumber)
                                            p.putValue("email", viewModel.responseData!!.email)
                                            BWSApplication.addToSegment("Profile Changes Saved", p, CONSTANTS.track)
                                            activity.finish()
                                        }
                                        viewModel.responseData!!.ageSlabChange.equals("1") -> {
                                            val intent = Intent(activity, SleepTimeActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            activity.startActivity(intent)
                                        }
                                        else -> {
                                            activity.finish()
                                        }
                                    }
                                }
                                viewModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                    BWSApplication.callDelete403(activity, viewModel.responseMessage)
                                }
                                else -> {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    BWSApplication.showToast(viewModel.responseMessage, activity)
                                }
                            }
                        }

                        override fun onFailure(call: Call<EditProfileModel>, t: Throwable) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun profileViewData(ctx: Context) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<AuthOtpModel> {
                override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                    try {
                        val listModel = response.body()
                        if (listModel != null) {
                            when {
                                listModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    if (listModel.ResponseData.Name.equals("", ignoreCase = true) || listModel.ResponseData.Name.equals(" ", ignoreCase = true)) {
                                        binding.etUser.setText(R.string.Guest)
                                    } else {
                                        binding.etUser.setText(listModel.ResponseData.Name)
                                    }
                                    userName = listModel.ResponseData.Name
                                    userCalendar = listModel.ResponseData.DOB
                                    copyUserCalendar = listModel.ResponseData.DOB
                                    Log.e("old Date", userCalendar.toString())

                                    binding.etCalendar.setText(userCalendar.toString())
                                    if (userCalendar != "") {
                                        val parser = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT)
                                        val formatter = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
                                        userCalendar = formatter.format(parser.parse(userCalendar))
                                    }

                                    if (listModel.ResponseData.isEmailVerified.equals("1")) {
                                        binding.ivCheckEmail.visibility = View.VISIBLE
                                    } else {
                                        binding.ivCheckEmail.visibility = View.GONE
                                    }

                                    userMobileNumber = listModel.ResponseData.Mobile
                                    userEmail = listModel.ResponseData.Email
                                    if (listModel.ResponseData.Mobile == "") {
                                        binding.etMobileNumber.isEnabled = true
                                        binding.etMobileNumber.isClickable = true
                                    } else {
                                        binding.etMobileNumber.isEnabled = false
                                        binding.etMobileNumber.isClickable = false
                                        binding.etMobileNumber.setTextColor(ContextCompat.getColor(ctx, R.color.light_gray))
                                    }
                                    binding.etMobileNumber.setText(listModel.ResponseData.Mobile)
                                    binding.etEmail.setText(listModel.ResponseData.Email)

                                    IsLock = listModel.ResponseData.Islock
                                    val shared = activity.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, listModel.ResponseData.CountryCode)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, listModel.ResponseData.directLogin)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, listModel.ResponseData.isPinSet)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, listModel.ResponseData.isEmailVerified)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, listModel.ResponseData.isMainAccount)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, listModel.ResponseData.CoUserCount)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, listModel.ResponseData.IsInCouser)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_paymentType, listModel.ResponseData.paymentType)
                                    if(listModel.ResponseData.paymentType == "0"){
                                        // Stripe
                                        try {
                                            if (listModel.ResponseData.oldPaymentDetails.isNotEmpty()) {
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.oldPaymentDetails[0].PlanId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.oldPaymentDetails[0].purchaseDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.oldPaymentDetails[0].expireDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "")
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "")
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStr, listModel.ResponseData.oldPaymentDetails[0].PlanStr)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_OrderTotal, listModel.ResponseData.oldPaymentDetails[0].OrderTotal)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.oldPaymentDetails[0].PlanStatus)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_CardId, listModel.ResponseData.oldPaymentDetails[0].CardId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.oldPaymentDetails[0].PlanContent)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }else if(listModel.ResponseData.paymentType == "1"){
                                        // IAP
                                        try {
                                            if (listModel.ResponseData.planDetails.isNotEmpty()) {
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.planDetails[0].PlanId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.planDetails[0].PlanExpireDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, listModel.ResponseData.planDetails[0].TransactionId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, listModel.ResponseData.planDetails[0].TrialPeriodStart)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, listModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.planDetails[0].PlanStatus)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.planDetails[0].PlanContent)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    editor.apply()
                                    val p = Properties()
                                    p.putValue("name", userName)
                                    p.putValue("dob", userCalendar)
                                    p.putValue("mobileNo", userMobileNumber)
                                    p.putValue("email", userEmail)
                                    BWSApplication.addToSegment("Edit Profile Screen Viewed", p, CONSTANTS.screen)
                                }
                                listModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                    BWSApplication.callDelete403(activity, listModel.ResponseMessage)
                                }
                                else -> {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun setDate() {
        if (userCalendar == "") {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        } else {
            /* var spf = SimpleDateFormat()
             var newDate = Date()
             try {
                 newDate = spf.parse(userCalendar)
             } catch (e: ParseException) {
                 e.printStackTrace()
             }
             spf = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
             var dob = spf.format(newDate)*/

            val ageArray = userCalendar!!.split("-")
            mYear = Integer.parseInt(ageArray[0])
            mMonth = Integer.parseInt(ageArray[1]) - 1
            mDay = Integer.parseInt(ageArray[2])
        }

        val datePickerDialog = DatePickerDialog(this, R.style.DialogTheme, { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            view.minDate = System.currentTimeMillis() - 1000
            val cal = Calendar.getInstance()
            cal.timeInMillis
            cal[year, monthOfYear] = dayOfMonth
            val date = cal.time
            val sdf = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT)
            val strDate = sdf.format(date)
            ageYear = year
            ageMonth = monthOfYear
            ageDate = dayOfMonth
            birthYear = getAge(ageYear, ageMonth, ageDate)
            if (birthYear < 0) {
                binding.txtDobError.visibility = View.VISIBLE
                binding.txtDobError.text = getString(R.string.check_dob)
                binding.btnSave.isEnabled = false
                binding.btnSave.isClickable = false
            } else {
                binding.txtDobError.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
            }
            binding.etCalendar.setText(strDate)
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age1 = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (dob == today) {
            age1--
        } else {
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age1--
            }
        }
        return age1
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(i)
        finish()
    }

    private fun checkBtnStatus(check: Int) {
        if (check == 0) {
            binding.btnSave.isEnabled = false
            binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
        } else if (check == 1) {
            binding.btnSave.isEnabled = true
            binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
        }
    }

}