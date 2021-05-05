package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
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
    var medication: String = ""
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFirstNext()
        }
        binding.btnOthers.setOnClickListener {
            profileType = "Others"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callFirstNext()
        }
        binding.btnMale.setOnClickListener {
            gender = "Male"
            genderX = ""
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnFemaleGX.setOnClickListener {
            genderX = "Female"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnOpn1.setOnClickListener {
            age = "0 - 4"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
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
            binding.llIndicate.progress = 4
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFifthNext()
        }
        binding.btnNo.setOnClickListener {
            prevDrugUse = "No"
            binding.llIndicate.progress = 4
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callFifthNext()
        }

        binding.btnSixYes.setOnClickListener {
            medication = "Yes"
            binding.llIndicate.progress = 5
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }

        binding.btnSixNo.setOnClickListener {
            medication = "No"
            binding.llIndicate.progress = 5
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
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
    }

    private fun callBack() {
        if (binding.llFirst.visibility == View.VISIBLE) {
            finish()
        } else if (binding.llSecond.visibility == View.VISIBLE) {
            callSecondPrev()
        } else if (binding.llThird.visibility == View.VISIBLE) {
            callFirstNext()
        } else if (binding.llForth.visibility == View.VISIBLE) {
            callFourthPrev()
        } else if (binding.llFifth.visibility == View.VISIBLE) {
            callSecondNext("1")
        }else if (binding.llSixth.visibility == View.VISIBLE) {
            callFifthPrev()
        }
    }

    private fun callNext() {
        if (binding.llFirst.visibility == View.VISIBLE) {
            callFirstNext()
        } else if (binding.llSecond.visibility == View.VISIBLE) {
            binding.btnPrev.visibility = View.VISIBLE
            callSecondNext("2")
        } else if (binding.llThird.visibility == View.VISIBLE) {
            binding.btnPrev.visibility = View.VISIBLE
            callSecondNext("3")
        } else if (binding.llForth.visibility == View.VISIBLE) {
            binding.btnPrev.visibility = View.VISIBLE
            callFourthNext()
        } else if (binding.llFifth.visibility == View.VISIBLE) {
            binding.btnPrev.visibility = View.VISIBLE
            callFifthNext()
        } else if (binding.llSixth.visibility == View.VISIBLE) {
            binding.btnPrev.visibility = View.VISIBLE
            binding.llIndicate.progress = 5
            sendProfileData()
        }
    }

    private fun callFifthPrev(){
        binding.llIndicate.progress = 4
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.VISIBLE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE

        if (prevDrugUse.equals("Yes", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (prevDrugUse.equals("No", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
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
            val listCall: Call<ProfileSaveDataModel> = APINewClient.getClient().getProfileSaveData(USERID, CoUserID, profileType, gender, genderX, age, prevDrugUse, medication)
            listCall.enqueue(object : Callback<ProfileSaveDataModel> {
                override fun onResponse(call: Call<ProfileSaveDataModel>, response: Response<ProfileSaveDataModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: ProfileSaveDataModel = response.body()!!
                        if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            val i = Intent(this@ProfileProgressActivity, WalkScreenActivity::class.java)
                            i.putExtra(CONSTANTS.ScreenView, "2")
                            startActivity(i)
                            finish()
                        } else {
                            BWSApplication.showToast(listModel.getResponseMessage(), activity)
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
        binding.btnPrev.visibility = View.VISIBLE
        if (prevDrugUse.equals("Yes", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (prevDrugUse.equals("No", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callFifthNext() {
        binding.llIndicate.progress = 4
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.llSixth.visibility = View.VISIBLE
        binding.btnPrev.visibility = View.VISIBLE
        if (medication.equals("Yes", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (medication.equals("No", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callSecondNext(s: String) {
        binding.btnPrev.visibility = View.VISIBLE
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
        binding.llIndicate.progress = 2
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.VISIBLE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        if (genderX.equals("Male", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (genderX.equals("Female", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
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
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("5 - 12", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("13 - 17", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (age.equals("> 18", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
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
        binding.llIndicate.progress = 0
        binding.llFirst.visibility = View.VISIBLE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.btnPrev.visibility = View.GONE
        if (profileType.equals("Myself", true)) {
            profileType = "Myself"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (profileType.equals("Others", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
    }

    private fun callFirstNext() {
        binding.llIndicate.progress = 1
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.VISIBLE
        binding.llSecond.isClickable = false
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE
        if (gender.equals("Male", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (gender.equals("Female", true)) {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        } else if (gender.equals("Gender X", true)) {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.light_blue_theme))
            binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        } else {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
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
        callBack()
    }
}