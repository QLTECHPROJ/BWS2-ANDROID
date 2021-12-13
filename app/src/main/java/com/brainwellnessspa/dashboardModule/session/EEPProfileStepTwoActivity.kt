package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SessionsProfileSaveDataModel
import com.brainwellnessspa.databinding.ActivityEepProfileStepTwoBinding

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EEPProfileStepTwoActivity : AppCompatActivity() {
    lateinit var binding: ActivityEepProfileStepTwoBinding
    lateinit var activity: Activity
    lateinit var ctx: Context
    private var doubleBackToExitPressedOnce = false
    private var drugsChallenges: String = ""
    private var fearAnswer: String = ""
    var electric_shock_last_treatment: String = ""
    var electricShockTreatment: String = "0"
    var drugPrescription: String = "0"
    var userId: String? = ""
    var coUserId: String? = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var ageYear: Int = 0
    var ageMonth: Int = 0
    var ageDate: Int = 0
    private lateinit var exitDialog: Dialog

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            drugsChallenges = binding.edtDrugsBox.text.toString().trim()
            fearAnswer = binding.edtFearAnswer.text.toString().trim()

            if (binding.llSecond.visibility == View.VISIBLE || binding.llSecond.visibility == View.VISIBLE) {
                if (binding.cbDrugYes.isChecked && drugsChallenges == "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("0")
                } else if (binding.cbDrugYes.isChecked && drugsChallenges != "") {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnContinue.visibility = View.GONE
                    checkNextVisibleGone("1")
                }
            }

            if (binding.llThird.visibility == View.VISIBLE || binding.llThird.visibility == View.VISIBLE) {
                if (fearAnswer == "") {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                    binding.btnContinue.isClickable = false
                    binding.btnContinue.isEnabled = false
                    binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
                } else if (fearAnswer != "") {
                    binding.btnNext.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                    binding.btnContinue.isClickable = true
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_eep_profile_step_two)
        ctx = this@EEPProfileStepTwoActivity
        activity = this@EEPProfileStepTwoActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        binding.edtDrugsBox.addTextChangedListener(userTextWatcher)
        binding.edtFearAnswer.addTextChangedListener(userTextWatcher)

        binding.llFirst.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.btnPrev.visibility = View.GONE
        binding.btnContinue.visibility = View.GONE
        checkNextVisibleGone("1")

      /*  val lineCount: Int = binding.tvSubDec.length()
        if (lineCount > 280) {
            binding.tvReadMore.visibility = View.VISIBLE
        } else {
            binding.tvReadMore.visibility = View.GONE
        }
        binding.tvReadMore.setOnClickListener {
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvDesc.text = ctx.getString(R.string.for_example_being_terrified_of_the_boogeyman_as_a_child_bullying_in_school_relationship_breakdown_loss_of_a_loved_one_red_more)

            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }

            tvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }*/
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.cbElectricYes.setOnClickListener {
            binding.cbElectricYes.isChecked = true
            binding.cbElectricNo.isChecked = false
            binding.llElectricYes.visibility = View.VISIBLE
            stepFirstMainCheck("0")
            electricShockTreatment = "1"
        }

        binding.cbElectricNo.setOnClickListener {
            binding.cbElectricYes.isChecked = false
            binding.cbElectricNo.isChecked = true
            binding.llElectricYes.visibility = View.GONE
            electric_shock_last_treatment = ""
            electricShockTreatment = "0"
            stepFirstMainCheck("0")
        }

        binding.llDatePicker.setOnClickListener {
            setDate()
        }

        binding.cbDrugYes.setOnClickListener {
            binding.cbDrugYes.isChecked = true
            binding.cbDrugNo.isChecked = false
            binding.llDrugsYes.visibility = View.VISIBLE
            drugPrescription = "1"
            stepTwoMainCheck("2")
        }

        binding.cbDrugNo.setOnClickListener {
            binding.cbDrugYes.isChecked = false
            binding.cbDrugNo.isChecked = true
            binding.llDrugsYes.visibility = View.GONE
            drugsChallenges = ""
            binding.edtDrugsBox.text.clear()
            drugPrescription = "0"
            stepTwoMainCheck("2")
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

    private fun stepTwoMainCheck(check: String) {
        if (check == "2") {
            binding.llIndicate.progress = 1
            binding.btnPrev.visibility = View.VISIBLE
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.VISIBLE
            binding.llThird.visibility = View.GONE
        } else if (check == "3") {
            binding.llIndicate.progress = 2
            binding.btnPrev.visibility = View.VISIBLE
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.VISIBLE
        }

        when {
            binding.cbDrugYes.isChecked && !drugsChallenges.equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
            }
            binding.cbDrugYes.isChecked && drugsChallenges.equals("", true) -> {
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
            binding.btnPrev.visibility = View.GONE
            binding.llFirst.visibility = View.VISIBLE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.GONE

        } else if (check == "1") {
            binding.llIndicate.progress = 1
            binding.btnPrev.visibility = View.VISIBLE
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.VISIBLE
            binding.llThird.visibility = View.GONE
        }

        when {
            binding.cbElectricYes.isChecked && !electric_shock_last_treatment.equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
                binding.tvElectricDate.text = electric_shock_last_treatment
                binding.tvElectricDate.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
            }
            binding.cbElectricYes.isChecked && electric_shock_last_treatment.equals("", true) -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("0")
                binding.tvElectricDate.text = getString(R.string.dd_mm_yyyy)
                binding.tvElectricDate.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
            }
            else -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.GONE
                checkNextVisibleGone("1")
                binding.tvElectricDate.text = getString(R.string.dd_mm_yyyy)
                binding.tvElectricDate.setTextColor(ContextCompat.getColor(activity, R.color.light_gray))
            }
        }
    }

    override fun onBackPressed() {
        callBack()
    }

    private fun sendSessionsFeelingsData() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SessionsProfileSaveDataModel> = APINewClient.client.getEEPStepTwoProfileSaveData(CONSTANTS.FLAG_TWO, coUserId, electricShockTreatment,electric_shock_last_treatment, drugPrescription,drugsChallenges, binding.edtFearAnswer.text.toString())
            listCall.enqueue(object : Callback<SessionsProfileSaveDataModel> {
                override fun onResponse(call: Call<SessionsProfileSaveDataModel>, response: Response<SessionsProfileSaveDataModel>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SessionsProfileSaveDataModel = response.body()!!
                        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString(CONSTANTS.PREFE_ACCESS_DOB, electric_shock_last_treatment)
                        editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "1")
                        editor.apply()
                        callIdentify(ctx)
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                /*  val p = Properties()
                                p.putValue("gender", gender)
                                p.putValue("genderX", genderX)
                                p.putValue("dob", age)
                                p.putValue("prevDrugUse", prevDrugUse)
                                p.putValue("medication", medication)
                                addToSegment("Profile Form Submitted", p, CONSTANTS.track)*/
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

    private fun callBack() {
        when {
            binding.llFirst.visibility == View.VISIBLE -> {
                binding.llIndicate.progress = 0
                binding.btnPrev.visibility = View.GONE
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                finish()
               /* if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                showToast("Press again to exit", activity)

                Handler(Looper.myLooper()!!).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)*/
            }

            binding.llSecond.visibility == View.VISIBLE -> {
                binding.llIndicate.progress = 0
                binding.llFirst.visibility = View.VISIBLE
                binding.btnPrev.visibility = View.GONE
                binding.llSecond.visibility = View.GONE
                binding.llThird.visibility = View.GONE
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                if (binding.cbElectricYes.isChecked && electric_shock_last_treatment.equals("", ignoreCase = true)) {
                    checkNextVisibleGone("0")
                } else {
                    checkNextVisibleGone("1")
                }
            }

            binding.llThird.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                stepTwoMainCheck("2")
                if (binding.cbDrugYes.isChecked && drugsChallenges.equals("", ignoreCase = true)) {
                    checkNextVisibleGone("0")
                } else {
                    checkNextVisibleGone("1")
                }
            }

            else -> {
                finish()
             /*   exitDialog = Dialog(activity)
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
                exitDialog.setCancelable(true)*/
            }
        }
    }

    private fun callNext() {
        when {
            binding.llFirst.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                stepFirstMainCheck("1")
                if (binding.cbDrugYes.isChecked && drugsChallenges.equals("", ignoreCase = true)) {
                    checkNextVisibleGone("0")
                } else {
                    checkNextVisibleGone("1")
                }
            }
            binding.llSecond.visibility == View.VISIBLE -> {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                stepTwoMainCheck("3")
                binding.btnNext.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
                if (fearAnswer.equals("", ignoreCase = true)) {
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

    private fun setDate() {
        if (electric_shock_last_treatment == "") {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val ageArray = electric_shock_last_treatment.split("-")
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
            binding.tvElectricDate.text = userCalendar
            binding.tvElectricDate.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
            if (birthYear < 0) {
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                checkNextVisibleGone("0")
            } else {
                electric_shock_last_treatment = age1
                binding.btnContinue.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                checkNextVisibleGone("1")
                stepFirstMainCheck("1")
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

    fun checkNextVisibleGone(check: String) {
        Log.e("electricShockTreatment", electricShockTreatment)
        Log.e("drugPrescription", drugPrescription)
        Log.e("age", electric_shock_last_treatment)
        Log.e("drugsChallenges", drugsChallenges)
        Log.e("fearAnswer", fearAnswer)
        if (check == "0") {
            binding.btnNext.isClickable = false
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.SRC_ATOP)
        } else if (check == "1") {
            binding.btnNext.isClickable = true
            binding.btnNext.setColorFilter(ContextCompat.getColor(activity, R.color.black), PorterDuff.Mode.SRC_ATOP)
        }
    }
}