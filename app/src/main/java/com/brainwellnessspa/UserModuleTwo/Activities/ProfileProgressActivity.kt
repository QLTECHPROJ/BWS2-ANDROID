package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.ProfileSaveDataModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityProfileProgressBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileProgressBinding
    var profileType: String = ""
    var gender: String = ""
    var genderX: String = ""
    var age: String = ""
    var prevDrugUse: String = ""
    var USERID: String? = null
    var CoUserID: String? = null
    var EMAIL: String? = null
    lateinit var activity: Activity
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_progress)
        ctx = this@ProfileProgressActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        EMAIL = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")

        activity = this@ProfileProgressActivity
        callSecondPrev()
        binding.btnMySelf.setOnClickListener {
            profileType = "Myself"
            binding.btnFirstDone.isClickable = true
            binding.btnFirstDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFirstNext()
        }
        binding.btnOthers.setOnClickListener {
            profileType = "Others"
            binding.btnFirstDone.isClickable = true
            binding.btnFirstDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callFirstNext()
        }
        binding.btnMale.setOnClickListener {
            gender = "Male"
            genderX = ""
            binding.btnSecondDone.isClickable = true
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("2")
        }
        binding.btnFemale.setOnClickListener {
            gender = "Female"
            genderX = ""
            binding.btnSecondDone.isClickable = true
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("2")
        }

        binding.btnGenX.setOnClickListener {
            gender = "Gender X"
            binding.btnSecondDone.isClickable = false
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callSecondNext("2")
        }
        binding.btnMaleGX.setOnClickListener {
            genderX = "Male"
            binding.btnThirdDone.isClickable = true
            binding.btnThirdDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnFemaleGX.setOnClickListener {
            genderX = "Female"
            binding.btnThirdDone.isClickable = true
            binding.btnThirdDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnOpn1.setOnClickListener {
            age = "0 - 4"
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFourthNext()
        }
        binding.btnOpn2.setOnClickListener {
            age = "5 - 12"
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFourthNext()
        }
        binding.btnOpn3.setOnClickListener {
            age = "13 - 17"
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFourthNext()
        }
        binding.btnOpn4.setOnClickListener {
            age = "> 18"
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callFourthNext()
        }
        binding.btnYes.setOnClickListener {
            prevDrugUse = "Yes"
            binding.btnFifthDone.isClickable = true
            binding.btnFifthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnNo.setOnClickListener {
            prevDrugUse = "No"
            binding.btnFifthDone.isClickable = true
            binding.btnFifthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }

        binding.btnFirstDone.setOnClickListener {
            callFirstNext()
        }
        binding.btnSecondDone.setOnClickListener {
            callSecondNext("2")
        }
        binding.btnPrevSecond.setOnClickListener {
            callSecondPrev()
        }
        binding.btnThirdDone.setOnClickListener {
            callSecondNext("3")
        }
        binding.btnGenXReverse.setOnClickListener {
            callFirstNext()
        }
        binding.btnPrevThird.setOnClickListener {
            callFirstNext()
        }
        binding.btnForthDone.setOnClickListener {
            callFourthNext()
        }
        binding.btnPrevFour.setOnClickListener {
            callFourthPrev()
        }
        binding.btnFifthDone.setOnClickListener {
            binding.llIndicate.progress = 4
            sendProfileData()
        }

        binding.btnPrevFive.setOnClickListener {
            callSecondNext("1")
        }
    }

    private fun callFourthPrev() {
        if (gender.equals("Gender X")) {
            callGenderXSetData()
        } else {
            callFirstNext()
        }
    }

    private fun sendProfileData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<ProfileSaveDataModel> = APINewClient.getClient().getProfileSaveData(USERID, CoUserID, profileType, gender, genderX, age, prevDrugUse)
            listCall.enqueue(object : Callback<ProfileSaveDataModel> {
                override fun onResponse(call: Call<ProfileSaveDataModel>, response: Response<ProfileSaveDataModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: ProfileSaveDataModel = response.body()!!
                        if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            val i = Intent(this@ProfileProgressActivity, AssProcessActivity::class.java)
                            i.putExtra(CONSTANTS.ASSPROCESS, "0")
                            startActivity(i)
                            finish()
                        } else {
                            BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ProfileSaveDataModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun callFourthNext() {
        binding.llIndicate.progress = 3
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.VISIBLE
        if (prevDrugUse.equals("Yes", true)) {
            binding.btnFifthDone.isClickable = true
            binding.btnFifthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (prevDrugUse.equals("No", true)) {
            binding.btnFifthDone.isClickable = true
            binding.btnFifthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnFifthDone.isClickable = false
            binding.btnFifthDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callSecondNext(s: String) {
        if (s.equals("2", true)) {
            if (gender.equals("Gender X")) {
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
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.VISIBLE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        if (genderX.equals("Male", true)) {
            binding.btnThirdDone.isClickable = true
            binding.btnThirdDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (genderX.equals("Female", true)) {
            binding.btnThirdDone.isClickable = true
            binding.btnThirdDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnThirdDone.isClickable = false
            binding.btnThirdDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callSecondNextElseBlock() {
        binding.llIndicate.progress = 2
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.VISIBLE
        binding.llFifth.visibility = View.GONE
        if (age.equals("0 - 4", true)) {
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("5 - 12", true)) {
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("13 - 17", true)) {
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("> 18", true)) {
            binding.btnForthDone.isClickable = true
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnForthDone.isClickable = false
            binding.btnForthDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callSecondPrev() {
        binding.llFirst.visibility = View.VISIBLE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        if (profileType.equals("Myself", true)) {
            profileType = "Myself"
            binding.btnFirstDone.isClickable = true
            binding.btnFirstDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (profileType.equals("Others", true)) {
            binding.btnFirstDone.isClickable = true
            binding.btnFirstDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnFirstDone.isClickable = false
            binding.btnFirstDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callFirstNext() {
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.VISIBLE
        binding.llSecond.isClickable = false
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        if (gender.equals("Male", true)) {
            binding.btnSecondDone.isClickable = true
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (gender.equals("Female", true)) {
            binding.btnSecondDone.isClickable = true
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (gender.equals("Gender X", true)) {
            binding.btnSecondDone.isClickable = false
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnSecondDone.isClickable = false
            binding.btnSecondDone.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.llIndicate.progress = 1
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    override fun onBackPressed() {

    }
}