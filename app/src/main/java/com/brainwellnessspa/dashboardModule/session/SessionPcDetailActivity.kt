package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionPcDetailBinding
import com.brainwellnessspa.userModule.models.ProfileSaveDataModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SessionPcDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionPcDetailBinding
    lateinit var ctx:Context
    lateinit var act:Activity
    var titleF: String = ""
    var gender: String = ""
    var genderX: String = ""
    var age: String = ""
    var address: String? = ""
    var suburb: String? = ""
    var postcode: String? = ""
    var ethnicity: String? = ""
    var userId: String? = ""
    var coUserId: String? = ""
    private var emailUser: String? = ""
    private var mentalHealthChallenges: String = ""
    private var mentalHealthTreatments: String = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var ageYear: Int = 0
    var ageMonth: Int = 0
    var ageDate: Int = 0
    private var doubleBackToExitPressedOnce = false
    private lateinit var exitDialog: Dialog
    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            address = binding.etHomeAddress.text.toString().trim()
            suburb = binding.etSuburb.text.toString().trim()
            postcode = binding.etPostcode.text.toString().trim()
            ethnicity = binding.etEthnicity.text.toString().trim()
            mentalHealthChallenges = binding.edtCancelBoxAc1.text.toString().trim()
            mentalHealthTreatments = binding.edtCancelBoxAc2.text.toString().trim()
            if(address == "" && suburb == "" && postcode == "" && ethnicity == "" ) {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            } else if(address != "" && suburb == "" && postcode == "" && ethnicity == "" ) {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            } else if(address != "" && suburb != "" && postcode == "" && ethnicity == "" ) {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            } else if(address != "" && suburb != "" && postcode != "" && ethnicity == "" ) {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            } else {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
            }
            if(binding.llFifth.isVisible || binding.llFifth.visibility == View.VISIBLE) {
                if (binding.cbYes1.isChecked && mentalHealthChallenges == "") {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                } else if (binding.cbYes1.isChecked && mentalHealthChallenges != "") {
                    binding.btnNext.isClickable = true
                    binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                }
            }
            if(binding.llSixth.isVisible || binding.llSixth.visibility == View.VISIBLE) {
                if (binding.cbYes2.isChecked && mentalHealthTreatments == "") {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                } else if (binding.cbYes2.isChecked && mentalHealthTreatments != "") {
                    binding.btnNext.isClickable = true
                    binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_pc_detail)
        ctx = this@SessionPcDetailActivity
        act = this@SessionPcDetailActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")

        callMainStep()

        binding.etHomeAddress.addTextChangedListener(userTextWatcher)
        binding.etSuburb.addTextChangedListener(userTextWatcher)
        binding.etPostcode.addTextChangedListener(userTextWatcher)
        binding.etEthnicity.addTextChangedListener(userTextWatcher)
        binding.edtCancelBoxAc1.addTextChangedListener(userTextWatcher)
        binding.edtCancelBoxAc2.addTextChangedListener(userTextWatcher)

        binding.btnMr.setOnClickListener {
            callFirstCondition("Mr")
        }

        binding.btnMrs.setOnClickListener {
            callFirstCondition("Mrs")
        }

        binding.btnMaster.setOnClickListener {
            callFirstCondition("Master")
        }

        binding.btnMs.setOnClickListener {
            callFirstCondition("Ms")
        }

        binding.btnDr.setOnClickListener {
            callFirstCondition("Dr")
        }

        binding.btnOther.setOnClickListener {
            callFirstCondition("Other")
        }

        binding.btnMale.setOnClickListener {
            callSecondCondition("Male", "", "2")
        }

        binding.btnFemale.setOnClickListener {
            callSecondCondition("Female", "", "2")
        }

        binding.btnGenX.setOnClickListener {
            callSecondCondition("Gender X", "", "2")
        }

        binding.btnMaleGX.setOnClickListener {
            callSecondCondition("Gender X", "Male", "3")
        }

        binding.btnFemaleGX.setOnClickListener {
            callSecondCondition("Gender X", "Female", "3")
        }

        binding.btnOpn1.setOnClickListener {
            setDate()
        }

        binding.cbYes1.setOnClickListener {
            binding.cbYes1.isChecked = true
            binding.cbNo1.isChecked = false
            binding.llfifth2.visibility = View.VISIBLE
        }
        binding.cbNo1.setOnClickListener {
            binding.cbYes1.isChecked = false
            binding.cbNo1.isChecked = true
            mentalHealthChallenges = ""
            binding.edtCancelBoxAc1.setText("")
            binding.llfifth2.visibility = View.GONE
        }

        binding.cbYes2.setOnClickListener {
            binding.cbYes2.isChecked = true
            binding.cbNo2.isChecked = false
            binding.llSixth2.visibility = View.VISIBLE
        }
        binding.cbNo2.setOnClickListener {
            binding.cbYes2.isChecked = false
            binding.cbNo2.isChecked = true
            mentalHealthTreatments = ""
            binding.edtCancelBoxAc2.setText("")
            binding.llSixth2.visibility = View.GONE
        }

        binding.btnNext.setOnClickListener {
            callNext()
        }

        binding.btnGenXReverse.setOnClickListener {
            callBack()
        }

        binding.btnPrev.setOnClickListener {
            callBack()
        }

        binding.btnContinue.setOnClickListener {
            binding.llIndicate.progress = 6
            sendProfileData()
        }
    }
    private fun sendProfileData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall: Call<ProfileSaveDataModel> = APINewClient.client.getEEPStep1ProfileSaveData("1", coUserId, age, titleF, gender, address, suburb, postcode, ethnicity, mentalHealthChallenges, mentalHealthTreatments)
            listCall.enqueue(object : Callback<ProfileSaveDataModel> {
                override fun onResponse(call: Call<ProfileSaveDataModel>, response: Response<ProfileSaveDataModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        val listModel: ProfileSaveDataModel = response.body()!!
                        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString(CONSTANTS.PREFE_ACCESS_DOB, age)
                        editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "1")
                        editor.apply()
                        BWSApplication.callIdentify(ctx)
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                /*  val p = Properties()
                                p.putValue("gender", gender)
                                p.putValue("genderX", genderX)
                                p.putValue("dob", age)
                                p.putValue("prevDrugUse", prevDrugUse)
                                p.putValue("medication", medication)
                                BWSApplication.addToSegment("Profile Form Submitted", p, CONSTANTS.track)
                                val i = Intent(this@ProfileProgressActivity, SleepTimeActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(i)
                                finish()*/
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                BWSApplication.deleteCall(act)
                                BWSApplication.showToast(listModel.responseMessage, act)
                                val i = Intent(act, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                            else -> {
                                BWSApplication.showToast(listModel.responseMessage, act)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ProfileSaveDataModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), act)
        }
    }

    private fun callSecondCondition(genders: String, genderXs: String, pass: String) {
        gender = genders
        genderX = genderXs
        callSecondNext(pass)
    }

    private fun callFirstCondition(titles: String) {
        titleF = titles
        binding.btnNext.isClickable = true
        callMainStepNext()
    }

    private fun callMainStep() {
        binding.llFirst.visibility = View.VISIBLE
        binding.llFirst.isClickable = false
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        val p = Properties()
        p.putValue("screen", 1)
        //        addInSegment(p)
        when {
            titleF.equals("Mr", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMr.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            titleF.equals("Mrs", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMrs.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            titleF.equals("Master", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMaster.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            titleF.equals("Ms", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMs.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            titleF.equals("Dr", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnDr.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            titleF.equals("Other ", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnOther.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMrs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMrs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMaster.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaster.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnMs.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMs.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnDr.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnDr.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOther.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOther.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    private fun callMainStepNext() {
        binding.llIndicate.progress = 1
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.VISIBLE
        binding.llAddress.isClickable = false
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        val p = Properties()
        p.putValue("screen", 2)
        //        addInSegment(p)
        binding.etHomeAddress.setText(address)
        binding.etSuburb.setText(suburb)
        binding.etPostcode.setText(postcode)
        binding.etEthnicity.setText(ethnicity)
    }

    private fun callFirstNext() {
        binding.llIndicate.progress = 2
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.VISIBLE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        val p = Properties()
        p.putValue("screen", 3)
        //        addInSegment(p)
        when {
            gender.equals("Male", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMale.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            gender.equals("Female", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            gender.equals("Gender X", true) -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                //                binding.llIndicate.progress = 1
                binding.btnMale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    private fun callSecondNext(s: String) {
        val p = Properties()
        p.putValue("screen", 3)
        //        addInSegment(p)
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        if (s.equals("2", true)) {
            if (gender == "Gender X") {
                callGenderXSetData()
            } else {
                callSecondNextElseBlock()
            }
        } else if (s.equals("3", true)) {
            callSecondNextElseBlock()
        } else {
            callSecondNextElseBlock()
        }
    }

    private fun callGenderXSetData() {
        binding.llIndicate.progress = 3
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.VISIBLE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.GONE
        when {
            genderX.equals("Male", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMaleGX.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            genderX.equals("Female", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnMaleGX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnMaleGX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    private fun callSecondNextElseBlock() {
        binding.llIndicate.progress = 3
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.VISIBLE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.GONE
        when {
            !age.equals("", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                binding.btnOpn1.setTextColor(ContextCompat.getColor(act, R.color.light_blue_theme))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOpn1.text = age
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnOpn1.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                val outputFmt = SimpleDateFormat("yyyy-MM-dd")
                val dateAsString = outputFmt.format(calendar.time)
                binding.btnOpn1.text = dateAsString
            }
        }
    }

    private fun callFourthPrev() {
        if (gender == "Gender X") {
            callGenderXSetData()
        } else {
            callFirstNext()
        }
    }

    private fun callFourthNext() {
        val p = Properties()
        p.putValue("screen", 5)
        //        addInSegment(p)
        binding.llIndicate.progress = 4
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.VISIBLE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        binding.btnNext.isClickable = true
        binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)

        if(mentalHealthChallenges==""){
            binding.cbYes1.isChecked = false
            binding.cbNo1.isChecked = true
            binding.llfifth2.visibility = View.GONE
        }else if(mentalHealthChallenges!=""){
            binding.cbYes1.isChecked =true
            binding.cbNo1.isChecked = false
            binding.llfifth2.visibility = View.VISIBLE
        }
    }

    private fun callFifthNext() {
        val p = Properties()
        p.putValue("screen", 6)
        //        addInSegment(p)
        binding.llIndicate.progress = 5
        binding.llFirst.visibility = View.GONE
        binding.llAddress.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.VISIBLE
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.GONE
        binding.btnContinue.visibility = View.VISIBLE
        binding.btnContinue.isClickable = true
        binding.btnContinue.isEnabled = true
        binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
        binding.btnNext.isClickable = true
        binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
        if(mentalHealthTreatments == ""){
            binding.cbYes2.isChecked = false
            binding.cbNo2.isChecked = true
            binding.llSixth2.visibility = View.GONE
        }else if(mentalHealthTreatments != ""){
            binding.cbYes2.isChecked =true
            binding.cbNo2.isChecked = false
            binding.llSixth2.visibility = View.VISIBLE
        }
    }

    private fun setDate() {
        if (age == "") {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val ageArray = age.split("-")
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
            val sdf = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
            ageYear = year
            ageMonth = monthOfYear
            ageDate = dayOfMonth
            val birthYear = getAge(ageYear, ageMonth, ageDate)
            val parser = SimpleDateFormat(CONSTANTS.YEAR_TO_DATE_FORMAT)
            val formatter = SimpleDateFormat(CONSTANTS.DATE_MONTH_YEAR_FORMAT)
            val age1 = sdf.format(date)
            val userCalendar = formatter.format(parser.parse(age1))
            binding.btnOpn1.text = userCalendar
            if (birthYear < 0) {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
                binding.btnOpn1.setTextColor(ContextCompat.getColor(act, R.color.black))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            } else {
                age = age1
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
                callFourthNext()
            }
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
        callBack()
    }

    private fun callBack() {
        when {
            binding.llFirst.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                BWSApplication.showToast("Press again to exit", act)

                Handler(Looper.myLooper()!!).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
            binding.llAddress.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callMainStep()
            }
            binding.llSecond.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callMainStepNext()
            }
            binding.llThird.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callFirstNext()
            }
            binding.llForth.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callFourthPrev()
            }
            binding.llFifth.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callSecondNext("1")
            }
            binding.llSixth.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callFourthNext()
            }
            else -> {
                exitDialog = Dialog(act)
                exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                exitDialog.setContentView(R.layout.logout_layout)
                exitDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                exitDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val tvGoBack: TextView = exitDialog.findViewById(R.id.tvGoBack)
                val tvTitle: TextView = exitDialog.findViewById(R.id.tvTitle)
                val tvHeader: TextView = exitDialog.findViewById(R.id.tvHeader)
                val btn: Button = exitDialog.findViewById(R.id.Btn)
                tvTitle.text = "Brain Wellness App"
                tvHeader.text = "Are you sure you want to exit the app?"
                exitDialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        exitDialog.hide()
                        return@setOnKeyListener true
                    }
                    false
                }

                btn.setOnClickListener {
                    exitDialog.hide()
                    finishAffinity()
                }

                tvGoBack.setOnClickListener { exitDialog.hide() }
                exitDialog.show()
                exitDialog.setCancelable(true)
            }
        }
    }

    private fun callNext() {
        when {
            binding.llFirst.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callMainStepNext()
            }
            binding.llAddress.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                callFirstNext()
            }
            binding.llSecond.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                callSecondNext("2")
            }
            binding.llThird.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                callSecondNext("3")
            }
            binding.llForth.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                callFourthNext()
            }
            binding.llFifth.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                callFifthNext()
            }
            binding.llSixth.visibility == View.VISIBLE -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
            }
        }
    }

}