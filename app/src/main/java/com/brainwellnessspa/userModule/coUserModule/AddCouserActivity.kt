package com.brainwellnessspa.userModule.coUserModule

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityAddCouserBinding
import com.brainwellnessspa.userModule.models.AuthOtpModel

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCouserActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCouserBinding
    lateinit var activity: Activity
    lateinit var ctx: Context
    var mainAccountID: String? = ""
    var isPinSet: String? = ""
    var directLogin: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_couser)
        activity = this@AddCouserActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        val p = Properties()
        addToSegment("Add User Screen Viewed", p, CONSTANTS.screen)
        binding.ivInfo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvAction = dialog.findViewById<TextView>(R.id.tvAction)
            val llDiscalimer = dialog.findViewById<LinearLayout>(R.id.llDiscalimer)
            llDiscalimer.visibility = View.GONE
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvTitle.text = getString(R.string.add_couser_popup_title)
            tvDesc.text = "Happiness is sweeter when shared. That's why we're eager for you to share your subscription with someone you care for.\nThe first person invited gets the same benefits as you at no additional cost."
            tvAction.text = getString(R.string.ok)
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

        binding.btnSameMobileNo.setOnClickListener {
            if (isNetworkConnected(this)) {
                val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(mainAccountID)
                listCall.enqueue(object : Callback<AuthOtpModel> {
                    override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                        val authOtpModel: AuthOtpModel = response.body()!!
                        if (authOtpModel.ResponseCode.equals(activity.getString(R.string.ResponseCodesuccess))) {
                            isPinSet = authOtpModel.ResponseData.isPinSet
                            directLogin = authOtpModel.ResponseData.directLogin

                            IsLock = authOtpModel.ResponseData.Islock
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, authOtpModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, authOtpModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, authOtpModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, authOtpModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, authOtpModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, authOtpModel.ResponseData.CountryCode)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, authOtpModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, authOtpModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, authOtpModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, authOtpModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, authOtpModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, authOtpModel.ResponseData.isAssessmentCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, authOtpModel.ResponseData.directLogin)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, authOtpModel.ResponseData.isPinSet)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, authOtpModel.ResponseData.isEmailVerified)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, authOtpModel.ResponseData.isMainAccount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, authOtpModel.ResponseData.CoUserCount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, authOtpModel.ResponseData.IsInCouser)
                            try {
                                if (authOtpModel.ResponseData.planDetails.isNotEmpty()) {
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, authOtpModel.ResponseData.planDetails[0].PlanId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, authOtpModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, authOtpModel.ResponseData.planDetails[0].PlanExpireDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, authOtpModel.ResponseData.planDetails[0].TransactionId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, authOtpModel.ResponseData.planDetails[0].TrialPeriodStart)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, authOtpModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, authOtpModel.ResponseData.planDetails[0].PlanStatus)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, authOtpModel.ResponseData.planDetails[0].PlanContent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            editor.apply()
                            if (authOtpModel.ResponseData.isPinSet.equals("1")) {
                                if (authOtpModel.ResponseData.MainAccountID == authOtpModel.ResponseData.UserId) {
                                    val i = Intent(applicationContext, UserDetailActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    startActivity(i)
                                } /* else {
                                       if (authOtpModel.ResponseData.isAssessmentCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.AvgSleepTime.equals("", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("1", ignoreCase = true) && authOtpModel.ResponseData.isAssessmentCompleted.equals("1", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            intent.putExtra("IsFirst", "0")
                                            startActivity(intent)
                                            finish()
                                        }
                                    }*/
                            } else if (authOtpModel.ResponseData.isPinSet.equals("0", ignoreCase = true) || authOtpModel.ResponseData.isPinSet.equals("", ignoreCase = true)) {
                                comeHomeScreen = "0"
                                val i = Intent(applicationContext, CouserSetupPinActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                i.putExtra("subUserId", authOtpModel.ResponseData.UserId)
                                startActivity(i)
                            }
                            finish()
                        } else if (authOtpModel.ResponseCode.equals(activity.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                            callDelete403(activity, authOtpModel.ResponseMessage)
                        }
                    }

                    override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                    }
                })
            } else {
                showToast(getString(R.string.no_server_found), applicationContext as Activity?)
            }
        }

        binding.btnDiffMobileNo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.add_couser_continue_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val mainLayout = dialog.findViewById<ConstraintLayout>(R.id.mainLayout)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }

            mainLayout.setOnClickListener {
                if (isNetworkConnected(this)) {
                    val i = Intent(applicationContext, ContactBookActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(i)
                    finish()
                    dialog.dismiss()
                } else {
                    showToast(getString(R.string.no_server_found), this)
                }
            }

            dialog.show()
            dialog.setCancelable(true)
        }
    }

    override fun onBackPressed() {
        if (IsFirstClick.equals("0")) {
            finish()
        } else {
            finishAffinity()
        }
    }
}