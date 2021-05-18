package com.brainwellnessspa.userModuleTwo.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.BWSApplication.analytics
import com.brainwellnessspa.userModuleTwo.models.CoUserDetailsModel
import com.brainwellnessspa.userModuleTwo.models.EditProfileModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityEditProfileBinding
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
    lateinit var ctx: Context
    lateinit var activity: Activity
    lateinit var profileUpdate: String
    var userId: String? = null
    var coUserId: String? = null
    var UserName: String? = null
    var UserCalendar: String? = null
    var UserMobileNumber: String? = null
    var UserEmail: String? = null
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
            val user = binding.etUser.text.toString().trim()
            val calendar = binding.etCalendar.text.toString().trim()
            if (user.equals(UserName, ignoreCase = true) &&
                calendar.equals(UserCalendar, ignoreCase = true)
            ) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (user.equals("", ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (!user.equals(UserName, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!calendar.equals(UserCalendar, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        val shared1: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        ctx = this@EditProfileActivity
        activity = this@EditProfileActivity


        val p = Properties()
        p.putValue("coUserId", coUserId)
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
        profileViewData(ctx)
        binding.etCalendar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setDate()
            }
        }
        binding.btnSave.setOnClickListener {
            profileUpdate()
        }
    }

    fun profileUpdate() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
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
            val listCall = APINewClient.getClient().getEditProfile(
                coUserId, binding.etUser.text.toString(), dob,
                binding.etMobileNumber.text.toString(), binding.etEmail.text.toString()
            )
            listCall.enqueue(object : Callback<EditProfileModel> {
                override fun onResponse(
                    call: Call<EditProfileModel>,
                    response: Response<EditProfileModel>
                ) {
                    val viewModel = response.body()
                    if (viewModel!!.responseCode.equals(
                            getString(R.string.ResponseCodesuccess),
                            ignoreCase = true
                        )
                    ) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        profileViewData(ctx)
                        val shared =
                            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString(CONSTANTS.PREFE_ACCESS_NAME, viewModel.responseData!!.name)
                        editor.apply()

                        analytics.identify(
                            Traits()
                                .putEmail(viewModel.responseData!!.email)
                                .putName(viewModel.responseData!!.name)
                                .putPhone(viewModel.responseData!!.phoneNumber)
                                .putValue("coUserId", coUserId)
                                .putValue("userId", userId)
                                .putValue("name", viewModel.responseData!!.name)
                                .putValue("phone", viewModel.responseData!!.phoneNumber)
                                .putValue("email", viewModel.responseData!!.email)
                        )
                        val p = Properties()
                        p.putValue("coUserId", coUserId)
                        p.putValue("name", viewModel.responseData!!.name)
                        p.putValue("dob", viewModel.responseData!!.dob)
                        p.putValue("mobileNo", viewModel.responseData!!.phoneNumber)
                        p.putValue("email", viewModel.responseData!!.email)
                        BWSApplication.addToSegment("Edit Profile Saved", p, CONSTANTS.track)
                        finish()
                    } else {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                    }
                }

                override fun onFailure(call: Call<EditProfileModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        }

    }

    fun profileViewData(ctx: Context) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().getCoUserDetails(userId, coUserId)
            listCall.enqueue(object : Callback<CoUserDetailsModel> {
                override fun onResponse(
                    call: Call<CoUserDetailsModel>,
                    response: Response<CoUserDetailsModel>
                ) {
                    try {
                        val viewModel = response.body()
                        if (viewModel!!.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            if (viewModel.responseData!!.name.equals("", ignoreCase = true) ||
                                viewModel.responseData!!.name.equals(
                                    " ",
                                    ignoreCase = true
                                ) || viewModel.responseData!!.name == null
                            ) {
                                binding.etUser.setText(R.string.Guest)
                            } else {
                                binding.etUser.setText(viewModel.responseData!!.name)
                            }
                            UserName = viewModel.responseData!!.name
                            UserCalendar = viewModel.responseData!!.dob
                            UserMobileNumber = viewModel.responseData!!.mobile
                            UserEmail = viewModel.responseData!!.email
                            binding.etMobileNumber.setText(viewModel.responseData!!.mobile)
                            binding.etEmail.setText(viewModel.responseData!!.email)
                            binding.etCalendar.setText(viewModel.responseData!!.dob)

                            if (!viewModel.responseData!!.email.equals("", ignoreCase = true)) {
                                binding.etEmail.isEnabled = false
                                binding.etEmail.isClickable = false
                                binding.ivCheckEmail.isEnabled = false
                                binding.ivCheckEmail.isClickable = false

                            } else {
                                binding.etEmail.isEnabled = true
                                binding.etEmail.isClickable = true
                            }

                            if (!viewModel.responseData!!.mobile.equals("", ignoreCase = true)) {
                                binding.etMobileNumber.isEnabled = false
                                binding.etMobileNumber.isClickable = false
                            } else {
                                binding.etMobileNumber.isEnabled = true
                                binding.etMobileNumber.isClickable = true
                            }

                            if (!viewModel.responseData!!.dob.equals("", ignoreCase = true)) {
                                binding.etCalendar.isEnabled = false
                                binding.etCalendar.isClickable = false
                            } else {
                                binding.etCalendar.isEnabled = true
                                binding.etCalendar.isClickable = true
                            }
                        } else {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CoUserDetailsModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun setDate() {
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this, R.style.DialogTheme,
            { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
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
                    binding.tlCalendar.error = "You must be 18 years of age to register"
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false
                } else {
                    binding.tlCalendar.error = ""
                    binding.btnSave.isEnabled = true
                    binding.btnSave.isClickable = true
                }
                binding.etCalendar.setText(strDate)
            }, mYear, mMonth, mDay
        )
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