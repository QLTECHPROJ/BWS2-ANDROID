package com.brainwellnessspa.userModule.activities

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.databinding.ActivityProfileProgressBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.models.ProfileSaveDataModel
import com.segment.analytics.Properties
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
    var userId: String? = ""
    var coUserId: String? = ""
    var emailUser: String? = ""
    var doubleBackToExitPressedOnce = false
    lateinit var activity: Activity
    private lateinit var exitDialog: Dialog
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_progress)
        ctx = this@ProfileProgressActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        activity = this@ProfileProgressActivity
        val p = Properties()
        p.putValue("userId", userId)
        p.putValue("coUserId", coUserId)
        BWSApplication.addToSegment("Profile Form Screen Viewed", p, CONSTANTS.screen)
        callSecondPrev()

        binding.btnMySelf.setOnClickListener {
            profileType = "Myself"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnMySelf.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callFirstNext()
        }
        binding.btnOthers.setOnClickListener {
            profileType = "Others"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOthers.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callFirstNext()
        }
        binding.btnMale.setOnClickListener {
            gender = "Male"
            genderX = ""
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("2")
        }

        binding.btnGenX.setOnClickListener {
            gender = "Gender X"
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.gray),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnMaleGX.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnFemaleGX.setOnClickListener {
            genderX = "Female"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            callSecondNext("3")
        }
        binding.btnOpn1.setOnClickListener {
            age = "0 - 4"
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
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
            binding.btnNext.visibility = View.GONE
            binding.btnContinue.isClickable = true
            binding.btnContinue.isEnabled = true
            binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnSixYes.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
            binding.btnSixYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }

        binding.btnSixNo.setOnClickListener {
            medication = "No"
            binding.llIndicate.progress = 5
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.visibility = View.GONE
            binding.btnContinue.isClickable = true
            binding.btnContinue.isEnabled = true
            binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(
                ContextCompat.getColor(activity, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
            binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnSixNo.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_blue_theme
                )
            )
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

        binding.btnContinue.setOnClickListener {
            sendProfileData()
        }
    }

    @SuppressLint("SetTextI18n")
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
                BWSApplication.showToast("Press again to exit", activity)

                Handler(Looper.myLooper()!!).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
//                exitDialog = Dialog(activity)
//                exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                exitDialog.setContentView(R.layout.logout_layout)
//                exitDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                exitDialog.window!!.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                val tvGoBack: TextView = exitDialog.findViewById(R.id.tvGoBack)
//                val tvTitle: TextView = exitDialog.findViewById(R.id.tvTitle)
//                val tvHeader: TextView = exitDialog.findViewById(R.id.tvHeader)
//                val btn: Button = exitDialog.findViewById(R.id.Btn)
//                tvTitle.text = "Brain Wellness App"
//                tvHeader.text = "Are you sure you want to exit the app?"
//                exitDialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        exitDialog.hide()
//                        return@setOnKeyListener true
//                    }
//                    false
//                }
//
//                btn.setOnClickListener {
//                    exitDialog.hide()
//                    finishAffinity()
//                }
//
//                tvGoBack.setOnClickListener { exitDialog.hide() }
//                exitDialog.show()
//                exitDialog.setCancelable(true)
            }
            binding.llSecond.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                callSecondPrev()
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
                callFifthPrev()
            }
            else -> {
                exitDialog = Dialog(activity)
                exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                exitDialog.setContentView(R.layout.logout_layout)
                exitDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                exitDialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
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

    private fun callFifthPrev() {
        binding.llIndicate.progress = 4
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.VISIBLE
        binding.llSixth.visibility = View.GONE
        binding.btnPrev.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE

        when {
            prevDrugUse.equals("Yes", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnYes.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            prevDrugUse.equals("No", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnNo.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
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

    private fun sendProfileData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<ProfileSaveDataModel> = APINewClient.getClient().getProfileSaveData(
                coUserId,
                profileType,
                gender,
                genderX,
                age,
                prevDrugUse,
                medication
            )
            listCall.enqueue(object : Callback<ProfileSaveDataModel> {
                override fun onResponse(
                    call: Call<ProfileSaveDataModel>,
                    response: Response<ProfileSaveDataModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: ProfileSaveDataModel = response.body()!!
                        if (listModel.getResponseCode()
                                .equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)
                        ) {
                            val p = Properties()
                            p.putValue("userId", userId)
                            p.putValue("coUserId", coUserId)
                            p.putValue("profileType", profileType)
                            p.putValue("gender", gender)
                            p.putValue("genderX", genderX)
                            p.putValue("age", age)
                            p.putValue("prevDrugUse", prevDrugUse)
                            p.putValue("medication", medication)
                            BWSApplication.addToSegment(
                                "Profile Form Submitted",
                                p,
                                CONSTANTS.track
                            )
                            val i =
                                Intent(this@ProfileProgressActivity, SleepTimeActivity::class.java)
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
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
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
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        when {
            prevDrugUse.equals("Yes", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnYes.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            prevDrugUse.equals("No", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnNo.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
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
        binding.btnNext.visibility = View.GONE
        binding.btnContinue.visibility = View.VISIBLE
        binding.btnContinue.isClickable = false
        binding.btnContinue.isEnabled = false
        binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
        when {
            medication.equals("Yes", true) -> {
                binding.llIndicate.progress = 5
                binding.btnContinue.isClickable = true
                binding.btnContinue.isEnabled = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnSixYes.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnSixYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            medication.equals("No", true) -> {
                binding.llIndicate.progress = 5
                binding.btnContinue.isClickable = true
                binding.btnContinue.isEnabled = true
                binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnSixNo.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnSixNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnSixYes.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnSixNo.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnSixNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    private fun callSecondNext(s: String) {
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
        binding.llIndicate.progress = 2
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.VISIBLE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        when {
            genderX.equals("Male", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMaleGX.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            genderX.equals("Female", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemaleGX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    private fun callSecondNextElseBlock() {
        binding.llIndicate.progress = 2
        binding.llFirst.visibility = View.GONE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.VISIBLE
        binding.llFifth.visibility = View.GONE
        when {
            age.equals("0 - 4", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnOpn1.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            age.equals("5 - 12", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn2.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnOpn2.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            age.equals("13 - 17", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn3.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnOpn3.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOpn4.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            age.equals("> 18", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnOpn1.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn2.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn3.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOpn4.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnOpn4.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
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
    }

    private fun callSecondPrev() {
        binding.llIndicate.progress = 0
        binding.llFirst.visibility = View.VISIBLE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        when {
            profileType.equals("Myself", true) -> {
                profileType = "Myself"
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMySelf.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            profileType.equals("Others", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOthers.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMySelf.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnOthers.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
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
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        when {
            gender.equals("Male", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMale.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            gender.equals("Female", true) -> {
                binding.btnNext.isClickable = true
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
            gender.equals("Gender X", true) -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.light_blue_theme
                    )
                )
                binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            }
            else -> {
                binding.btnNext.isClickable = false
                binding.btnNext.setColorFilter(
                    ContextCompat.getColor(activity, R.color.gray),
                    PorterDuff.Mode.SRC_ATOP
                )
                binding.llIndicate.progress = 1
                binding.btnMale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnFemale.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
                binding.btnGenX.setTextColor(ContextCompat.getColor(activity, R.color.black))
                binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            }
        }
    }

    override fun onBackPressed() {
        callBack()
    }
}