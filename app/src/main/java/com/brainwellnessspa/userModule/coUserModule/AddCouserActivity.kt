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
                        val listModel: AuthOtpModel = response.body()!!
                        if (listModel.ResponseCode.equals(activity.getString(R.string.ResponseCodesuccess))) {
                            isPinSet = listModel.ResponseData.isPinSet
                            directLogin = listModel.ResponseData.directLogin
                            IsLock = listModel.ResponseData.Islock
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
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
                            if (listModel.ResponseData.isPinSet.equals("1")) {
                                if (listModel.ResponseData.MainAccountID == listModel.ResponseData.UserId) {
                                    val i = Intent(applicationContext, UserDetailActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                    startActivity(i)
                                } /* else {
                                       if (authOtpModel.ResponseData.isAssessmentCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.AvgSleepTime.equals("", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("1", ignoreCase = true) && authOtpModel.ResponseData.isAssessmentCompleted.equals("1", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                            intent.putExtra("IsFirst", "0")
                                            startActivity(intent)
                                            finish()
                                        }
                                    }*/
                            } else if (listModel.ResponseData.isPinSet.equals("0", ignoreCase = true) || listModel.ResponseData.isPinSet.equals("", ignoreCase = true)) {
                                comeHomeScreen = "0"
                                val i = Intent(applicationContext, CouserSetupPinActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                                i.putExtra("subUserId", listModel.ResponseData.UserId)
                                startActivity(i)
                            }
                            finish()
                        } else if (listModel.ResponseCode.equals(activity.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                            callDelete403(activity, listModel.ResponseMessage)
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
                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
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