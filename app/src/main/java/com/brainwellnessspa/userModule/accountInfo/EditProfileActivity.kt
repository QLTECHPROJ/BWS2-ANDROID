package com.brainwellnessspa.userModule.accountInfo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.analytics
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEditProfileBinding
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.EditProfileModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    lateinit var profileUpdate: String
    var userId: String? = ""
    var coUserId: String? = ""
    var userName: String? = ""
    var userCalendar: String? = ""
    var userMobileNumber: String? = ""
    var userEmail: String? = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var birthYear = 0
    var ageYear: Int = 0
    var ageMonth: Int = 0
    var ageDate: Int = 0

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val ckName: String = binding.etUser.text.toString().trim()
            val ckCalendar: String = binding.etCalendar.text.toString().trim()
            val ckNumber: String = binding.etMobileNumber.text.toString().trim()
            val ckEmail: String = binding.etEmail.text.toString().trim()
            when {
                ckName.equals(userName, ignoreCase = true) && ckCalendar.equals(userCalendar, ignoreCase = true) && ckNumber.equals(userMobileNumber, ignoreCase = true) && ckEmail.equals(userEmail, ignoreCase = true) -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckName.equals("", ignoreCase = true) -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckCalendar.equals("", ignoreCase = true) -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckNumber.equals("", ignoreCase = true) -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckEmail.equals("", ignoreCase = true) -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                else -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                    binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }

            if (ckNumber.equals("", ignoreCase = true)) {
                binding.ivCheckNumber.visibility = View.GONE
            } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 || binding.etMobileNumber.text.toString().length > 10) {
                binding.ivCheckNumber.visibility = View.GONE
            } else {
                binding.ivCheckNumber.visibility = View.VISIBLE
            }

            if (ckEmail.equals("", ignoreCase = true)) {
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!ckEmail.equals("", ignoreCase = true) && !ckEmail.isEmailValid()) {
                binding.ivCheckEmail.visibility = View.GONE
            } else {
                binding.ivCheckEmail.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        BWSApplication.addToSegment("Edit Profile Screen View", p, CONSTANTS.screen)

        binding.ivCheckNumber.visibility = View.VISIBLE
        binding.ivCheckEmail.visibility = View.VISIBLE
        binding.etUser.addTextChangedListener(userTextWatcher)
        binding.etCalendar.addTextChangedListener(userTextWatcher)
        binding.etMobileNumber.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)
        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            startActivity(i)
            finish()
        }

        profileViewData(applicationContext)
        binding.etCalendar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setDate()
            }
        }
        binding.btnSave.setOnClickListener {
            profileUpdate()
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    @SuppressLint("SetTextI18n")
    fun profileUpdate() {
        try {
            if (BWSApplication.isNetworkConnected(applicationContext)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                var dob = ""
                if (binding.etCalendar.text.toString().isNotEmpty()) {
                    dob = binding.etCalendar.text.toString()
                    var spf = SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT)
                    var newDate = Date()
                    try {
                        newDate = spf.parse(dob)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    spf = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
                    dob = spf.format(newDate)
                }

                if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                    binding.txtNameError.text = getString(R.string.pls_provide_a_name)
                    binding.txtNameError.visibility = View.VISIBLE
                    binding.txtDobError.visibility = View.GONE
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etCalendar.text.toString().equals("", ignoreCase = true)) {
                    binding.txtNameError.visibility = View.GONE
                    binding.txtDobError.visibility = View.VISIBLE
                    binding.txtDobError.text = "Please provide a dob"
                    binding.txtNumberError.visibility = View.GONE
                    binding.txtEmailError.visibility = View.GONE
                } else if (binding.etMobileNumber.text.toString().equals("", ignoreCase = true)) {
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
                } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
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
                            if (viewModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                                BWSApplication.showToast(viewModel.responseMessage, applicationContext as Activity?)
                                profileViewData(applicationContext)
                                val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                                val editor = shared.edit()
                                editor.putString(CONSTANTS.PREFE_ACCESS_NAME, viewModel.responseData!!.name)
                                editor.apply()

                                analytics.identify(Traits().putEmail(viewModel.responseData!!.email).putName(viewModel.responseData!!.name).putPhone(viewModel.responseData!!.phoneNumber).putValue("coUserId", coUserId).putValue("userId", userId).putValue("name", viewModel.responseData!!.name).putValue("phone", viewModel.responseData!!.phoneNumber).putValue("email", viewModel.responseData!!.email))
                                val p = Properties()
                                p.putValue("name", viewModel.responseData!!.name)
                                p.putValue("dob", viewModel.responseData!!.dob)
                                p.putValue("mobileNo", viewModel.responseData!!.phoneNumber)
                                p.putValue("email", viewModel.responseData!!.email)
                                BWSApplication.addToSegment("Edit Profile Saved", p, CONSTANTS.track)
                                finish()
                            } else {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                                BWSApplication.showToast(viewModel.responseMessage, applicationContext as Activity?)
                            }
                        }

                        override fun onFailure(call: Call<EditProfileModel>, t: Throwable) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
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
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
            val listCall = APINewClient.client.getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<AuthOtpModel> {
                override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                    try {
                        val viewModel = response.body()
                        if (viewModel != null) {
                            if (viewModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                                if (viewModel.ResponseData.Name.equals("", ignoreCase = true) || viewModel.ResponseData.Name.equals(" ", ignoreCase = true)) {
                                    binding.etUser.setText(R.string.Guest)
                                } else {
                                    binding.etUser.setText(viewModel.ResponseData.Name)
                                }
                                userName = viewModel.ResponseData.Name
                                userCalendar = viewModel.ResponseData.DOB
                                userMobileNumber = viewModel.ResponseData.Mobile
                                userEmail = viewModel.ResponseData.Email
                                binding.etMobileNumber.setText(viewModel.ResponseData.Mobile)
                                binding.etEmail.setText(viewModel.ResponseData.Email)
                                binding.etCalendar.setText(viewModel.ResponseData.DOB)

                            } else {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, applicationContext as Activity?)
                }
            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun setDate() {
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this, R.style.DialogTheme, { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            view.minDate = System.currentTimeMillis() - 1000
            val cal = Calendar.getInstance()
            cal.timeInMillis
            cal[year, monthOfYear] = dayOfMonth
            val date = cal.time
            val sdf = SimpleDateFormat(CONSTANTS.MONTH_DATE_YEAR_FORMAT)
            val strDate = sdf.format(date)
            ageYear = year
            ageMonth = monthOfYear
            ageDate = dayOfMonth
            birthYear = getAge(ageYear, ageMonth, ageDate)
            if (birthYear < 18) {
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

    fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}