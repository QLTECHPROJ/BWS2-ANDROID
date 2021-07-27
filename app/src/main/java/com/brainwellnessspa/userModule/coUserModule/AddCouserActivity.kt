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
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityAddCouserBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity
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

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        val p = Properties()
        BWSApplication.addToSegment("Add User Screen Viewed", p, CONSTANTS.screen)
        binding.ivInfo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvAction = dialog.findViewById<TextView>(R.id.tvAction)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvTitle.text = getString(R.string.popup_title)
            tvDesc.text = getString(R.string.popup_subtitle)
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
            if (BWSApplication.isNetworkConnected(this)) {
                if (BWSApplication.isNetworkConnected(this)) {
                    val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(mainAccountID)
                    listCall.enqueue(object : Callback<AuthOtpModel> {
                        override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                            val authOtpModel: AuthOtpModel = response.body()!!
                            if (authOtpModel.ResponseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                isPinSet = authOtpModel.ResponseData.isPinSet
                                directLogin = authOtpModel.ResponseData.directLogin
                                if (authOtpModel.ResponseData.isPinSet.equals("1", ignoreCase = true)) {
                                    if (authOtpModel.ResponseData.MainAccountID.equals(authOtpModel.ResponseData.UserId, ignoreCase = true)) {
                                        val i = Intent(applicationContext, UserDetailActivity::class.java)
                                        startActivity(i)
                                    } else {
                                        if (authOtpModel.ResponseData.isAssessmentCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, AssProcessActivity::class.java)
                                            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("0", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.AvgSleepTime.equals("", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else if (authOtpModel.ResponseData.isProfileCompleted.equals("1", ignoreCase = true) && authOtpModel.ResponseData.isAssessmentCompleted.equals("1", ignoreCase = true)) {
                                            val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                                            intent.putExtra("IsFirst", "0")
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                } else if (authOtpModel.ResponseData.isPinSet.equals("0", ignoreCase = true) || authOtpModel.ResponseData.isPinSet.equals("", ignoreCase = true)) {
                                    val i = Intent(applicationContext, CouserSetupPinActivity::class.java)
                                    i.putExtra("subUserId", authOtpModel.ResponseData.UserId)
                                    startActivity(i)
                                }
                                finish()
                            } else if (authOtpModel.ResponseCode.equals(activity.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                BWSApplication.deleteCall(activity)
                                BWSApplication.showToast(authOtpModel.ResponseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                activity.startActivity(i)
                                activity.finish()
                            }
                        }

                        override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                        }
                    })
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), applicationContext as Activity?)
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
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
                if (BWSApplication.isNetworkConnected(this)) {
                    val i = Intent(applicationContext, ContactBookActivity::class.java)
                    startActivity(i)
                    finish()
                    dialog.dismiss()
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), this)
                }
            }

            dialog.show()
            dialog.setCancelable(true)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}