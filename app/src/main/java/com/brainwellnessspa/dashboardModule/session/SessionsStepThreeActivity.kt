package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionsProfileSaveDataModel
import com.brainwellnessspa.databinding.ActivitySessionsStepThreeBinding

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class  SessionsStepThreeActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionsStepThreeBinding
    lateinit var activity: Activity
    lateinit var ctx: Context
    private var traumaHistory: String = ""
    private var suicidalEpisodeStatus: String = ""
    private var pychoticEpisodeStatus: String = ""
    private var suicidalStatus: String = ""
    private var pychoticEpisode: String = ""
    private var doubleBackToExitPressedOnce = false
    var userId: String? = ""
    var coUserId: String? = ""
    private lateinit var exitDialog: Dialog

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            traumaHistory = binding.edtTraumaHistory.text.toString().trim()
            pychoticEpisode = binding.edtPsychoticBox.text.toString().trim()

            if (binding.llFirst.isVisible || binding.llFirst.visibility == View.VISIBLE) {
                if (traumaHistory == "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("0")
                } else if (traumaHistory != "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("1")
                }
            }

            if (binding.llSecond.isVisible || binding.llSecond.visibility == View.VISIBLE) {
                if (binding.cbPsychoticYes.isChecked && pychoticEpisode == "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("0")
                } else if (binding.cbPsychoticYes.isChecked && pychoticEpisode != "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("1")
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sessions_step_three)
        ctx = this@SessionsStepThreeActivity
        activity = this@SessionsStepThreeActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        binding.edtTraumaHistory.addTextChangedListener(userTextWatcher)
        binding.edtPsychoticBox.addTextChangedListener(userTextWatcher)
        binding.edtTraumaHistory.text.clear()
        binding.llFirst.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.GONE
        checkNextVisibleGone("0")
        traumaHistory = ""
        suicidalEpisodeStatus = "0"
        pychoticEpisodeStatus = "0"

        binding.cbPsychoticYes.setOnClickListener {
            binding.cbPsychoticYes.isChecked = true
            binding.cbPsychoticNo.isChecked = false
            binding.llPsychoticYes.visibility = View.VISIBLE
            pychoticEpisodeStatus = "1"
            stepTwoMainCheck("2")
        }

        binding.cbPsychoticNo.setOnClickListener {
            binding.cbPsychoticYes.isChecked = false
            binding.cbPsychoticNo.isChecked = true
            binding.llPsychoticYes.visibility = View.GONE
            binding.edtPsychoticBox.text.clear()
            pychoticEpisodeStatus = "0"
            stepTwoMainCheck("2")
        }

        binding.cbSuicidalYes.setOnClickListener {
            binding.cbSuicidalYes.isChecked = true
            binding.cbSuicidalNo.isChecked = false
            binding.llSuicidalYes.visibility = View.VISIBLE
            suicidalEpisodeStatus = "1"
        }

        binding.cbSuicidalNo.setOnClickListener {
            binding.cbSuicidalYes.isChecked = false
            binding.cbSuicidalNo.isChecked = true
            binding.llSuicidalYes.visibility = View.GONE
            binding.cbMildYes.isChecked = true
            binding.cbModerateYes.isChecked = false
            binding.cbSevereYes.isChecked = false
            suicidalEpisodeStatus = "0"
            suicidalStatus = ""
        }

        binding.cbMildYes.setOnClickListener {
            binding.cbMildYes.isChecked = true
            binding.cbModerateYes.isChecked = false
            binding.cbSevereYes.isChecked = false
            suicidalStatus = getString(R.string.mild)
        }

        binding.cbModerateYes.setOnClickListener {
            binding.cbMildYes.isChecked = false
            binding.cbModerateYes.isChecked = true
            binding.cbSevereYes.isChecked = false
            suicidalStatus = getString(R.string.moderate)
        }

        binding.cbSevereYes.setOnClickListener {
            binding.cbMildYes.isChecked = false
            binding.cbModerateYes.isChecked = false
            binding.cbSevereYes.isChecked = true
            suicidalStatus = getString(R.string.severe)
        }

        binding.btnNext.setOnClickListener {
            callNext()
        }

        binding.btnPrev.setOnClickListener {
            callBack()
        }

        binding.btnContinue.setOnClickListener {
            binding.llIndicate.progress = 3
            sendSessionsFeelingsData()
        }
    }

    private fun callBack() {
        when {
            binding.llFirst.visibility == View.VISIBLE -> {
                binding.llIndicate.progress = 0
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                showToast("Press again to exit", activity)

                Handler(Looper.myLooper()!!).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }

            binding.llSecond.visibility == View.VISIBLE -> {
                binding.llIndicate.progress = 0
                binding.llFirst.visibility = View.VISIBLE
                binding.llSecond.visibility = View.GONE
                binding.llThird.visibility = View.GONE
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                if (!binding.edtTraumaHistory.text.toString().equals("", ignoreCase = true)) {
                    checkNextVisibleGone("1")
                } else {
                    checkNextVisibleGone("0")
                }
            }

            binding.llThird.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                stepTwoMainCheck("2")
                if (binding.cbPsychoticYes.isChecked && pychoticEpisode.equals("", ignoreCase = true)) {
                    checkNextVisibleGone("0")
                } else {
                    checkNextVisibleGone("1")
                }
            }

            else -> {
                exitDialog = Dialog(activity)
                exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                exitDialog.setContentView(R.layout.logout_layout)
                exitDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                exitDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val tvGoBack: TextView = exitDialog.findViewById(R.id.tvGoBack)
                val tvTitle: TextView = exitDialog.findViewById(R.id.tvTitle)
                val tvHeader: TextView = exitDialog.findViewById(R.id.tvHeader)
                val btn: Button = exitDialog.findViewById(R.id.Btn)
                tvTitle.text = getString(R.string.brain_wellness_app)
                tvHeader.text = getString(R.string.close_app_text)
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
                stepFirstMainCheck("1")
                if (!binding.edtTraumaHistory.text.toString().equals("", ignoreCase = true)) {
                    checkNextVisibleGone("1")
                } else {
                    checkNextVisibleGone("0")
                }
            }
            binding.llSecond.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                stepTwoMainCheck("3")
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
                if (binding.cbPsychoticYes.isChecked && pychoticEpisode.equals("", ignoreCase = true)) {
                    binding.btnContinue.isClickable = false
                    binding.btnContinue.isEnabled = false
                    binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
                } else {
                    binding.btnContinue.isClickable = true
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }
            binding.llThird.visibility == View.VISIBLE -> {
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
            }
        }
    }

    private fun stepTwoMainCheck(check: String) {
        if (check == "2") {
            binding.llIndicate.progress = 1
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.VISIBLE
            binding.llThird.visibility = View.GONE
        } else if (check == "3") {
            binding.llIndicate.progress = 2
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.VISIBLE
        }

        when {
            binding.cbPsychoticYes.isChecked && !pychoticEpisode.equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
            }
            binding.cbPsychoticYes.isChecked && pychoticEpisode.equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("0")
            }
            else -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
            }
        }
    }

    private fun stepFirstMainCheck(check: String) {
        if (check == "0") {
            binding.llIndicate.progress = 0
            binding.llFirst.visibility = View.VISIBLE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.GONE

        } else if (check == "1") {
            binding.llIndicate.progress = 1
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.VISIBLE
            binding.llThird.visibility = View.GONE
        }
        when {
            !binding.edtTraumaHistory.text.toString().equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
            }
            else -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("0")
            }
        }
    }

    override fun onBackPressed() {
        callBack()
    }

    private fun sendSessionsFeelingsData() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SessionsProfileSaveDataModel> = APINewClient.client.getEEPStepThreeProfileSaveData(CONSTANTS.FLAG_THREE, coUserId, binding.edtTraumaHistory.text.toString(), pychoticEpisodeStatus, binding.edtPsychoticBox.text.toString(), suicidalEpisodeStatus, suicidalStatus)
            listCall.enqueue(object : Callback<SessionsProfileSaveDataModel> {
                override fun onResponse(call: Call<SessionsProfileSaveDataModel>, response: Response<SessionsProfileSaveDataModel>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SessionsProfileSaveDataModel = response.body()!!
                        callIdentify(ctx)
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                /*  val p = Properties()
                                p.putValue("gender", gender)
                                p.putValue("genderX", genderX)
                                p.putValue("dob", age)
                                p.putValue("prevDrugUse", prevDrugUse)
                                p.putValue("medication", medication)
                                BWSApplication.addToSegment("Profile Form Submitted", p, CONSTANTS.track)*/
                                val i = Intent(ctx, SessionsStepThreeActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(i)
                                finish()
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                callDelete403(activity, listModel.responseMessage)
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SessionsProfileSaveDataModel>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    fun checkNextVisibleGone(check: String) {
        Log.e("traumaHistory", traumaHistory)
        Log.e("pychoticEpisodeStatus", pychoticEpisodeStatus)
        Log.e("pychoticEpisode", pychoticEpisode)
        Log.e("suicidalEpisodeStatus", suicidalEpisodeStatus)
        Log.e("suicidalStatus", suicidalStatus)
        if (check == "0") {
            binding.btnNext.isClickable = false
            binding.btnNext.isEnabled = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
        } else if (check == "1") {
            binding.btnNext.isClickable = true
            binding.btnNext.isEnabled = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
        }
    }
}