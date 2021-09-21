package com.brainwellnessspa.addPaymentStripe

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ReplacementSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.addPaymentStripe.model.AddCardModel
import com.brainwellnessspa.billingOrderModule.activities.PaymentActivity
import com.brainwellnessspa.billingOrderModule.models.PlanListBillingModel
import com.brainwellnessspa.databinding.ActivityAddPaymentBinding
import com.brainwellnessspa.databinding.YeardialogBinding
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddPaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPaymentBinding
    lateinit var context: Context
    lateinit var activity: Activity
    var d: Dialog? = null
    var a = 0
    var TrialPeriod: String? = null
    var comeFrom: String? = ""
    var ComesTrue: String? = null
    var strToken: String? = null
    var ComePayment: String? = ""
    var UserID: String? = null
    var year = 0
    var month = 0
    var position = 0
    lateinit var binding1: YeardialogBinding
    var listModelList2: ArrayList<PlanListBillingModel.ResponseData.Plan>? = null
    private var listModelList: ArrayList<MembershipPlanListModel.Plan>? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_payment)
        context = this@AddPaymentActivity
        activity = this@AddPaymentActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        if (intent != null) {
            ComePayment = intent.getStringExtra("ComePayment")
        }
        if (intent != null) {
            TrialPeriod = intent.getStringExtra("TrialPeriod")
            position = intent.getIntExtra("position", 0)
            if (intent.hasExtra("comeFrom")) {
                comeFrom = intent.getStringExtra("comeFrom")
                listModelList2 = intent.getParcelableArrayListExtra("PlanData")
            } else {
                listModelList = intent.getParcelableArrayListExtra("PlanData")
            }
        }
        if (intent != null) {
            ComesTrue = intent.getStringExtra("ComesTrue")
        }
        binding.llBack.setOnClickListener { view ->
            myBackPress = true
            if (ComePayment.equals("1", ignoreCase = true)) {
                finish()
            } else if (ComePayment.equals("2", ignoreCase = true)) {
                val i = Intent(context, PaymentActivity::class.java)
                i.putExtra("ComesTrue", ComesTrue)
                i.putExtra("comeFrom", "membership")
                i.putParcelableArrayListExtra("PlanData", listModelList2)
                i.putExtra("TrialPeriod", "")
                i.putExtra("position", position)
                startActivity(i)
                finish()
            } else {
                finish()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        year = Calendar.getInstance()[Calendar.YEAR]
        month = Calendar.getInstance()[Calendar.MONTH]
        month += 1
        d = Dialog(context)
        d!!.setTitle("Year Picker")
        binding1 = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.yeardialog, null, false)
        d!!.setContentView(binding1.getRoot())
        binding.etNumber.addTextChangedListener(addCardTextWatcher)
        binding.etName.addTextChangedListener(addCardTextWatcher)
        binding.textMonth.addTextChangedListener(addCardTextWatcher)
        binding.etCvv.addTextChangedListener(addCardTextWatcher)
        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etNumber.text.toString().length == 16) {
                    binding.etName.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        binding.opendilog.setOnClickListener { v ->
            myBackPress = true
            a = 1
            showYearDialog()
        }
        val tv = CreditCardFormatTextWatcher(binding.etNumber)
        binding.etNumber.addTextChangedListener(tv)
        binding.etName.addTextChangedListener(addCardTextWatcher)
        binding.etNumber.addTextChangedListener(addCardTextWatcher)
        binding.etCvv.addTextChangedListener(addCardTextWatcher)
        binding.textMonth.addTextChangedListener(addCardTextWatcher)
        binding.btnSave.setOnClickListener { view ->
            if (binding.etNumber.text.toString().equals("")) {
                binding.tlNumber.error = "Card number is required."
                binding.txtError.text = ""
                binding.tlName.error = ""
            } else if (binding.etNumber.text.toString().length <= 15 || binding.etNumber.text.toString().length > 16) {
                binding.tlName.error = ""
                binding.tlNumber.error = "Please enter a valid card number"
                binding.txtError.text = ""
            } else if (binding.etName.text.toString().equals("")) {
                binding.tlName.error = "Card holder name is required"
                binding.tlNumber.error = ""
                binding.txtError.text = ""
            } else if (binding1.MonthPicker.value < month && binding1.YearPicker.value === year) {
                binding.txtError.text = "Please enter a valid expiry mm/yyyy"
                binding.tlName.error = ""
                binding.tlNumber.error = ""
            } else if (binding.textMonth.text.toString().equals("Expiry Date") || a == 0) {
                binding.txtError.text = "Expiry month is required"
                binding.tlName.error = ""
                binding.tlNumber.error = ""
            } else if (binding.etCvv.text.toString().equals("")) {
                binding.tlName.error = ""
                binding.tlNumber.error = ""
                binding.txtError.text = "CVV is required"
            } else if (binding.etCvv.text.toString().length < 3) {
                binding.tlName.error = ""
                binding.tlNumber.error = ""
                binding.txtError.text = "Please enter a valid CVV number"
            } else {
                binding.tlName.error = ""
                binding.tlNumber.error = ""
                binding.txtError.text = ""
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val strCardNo: String = binding.etNumber.text.toString().trim().replace("\\s+", "")
                val months = binding1.MonthPicker.value
                val Years = binding1.YearPicker.value
                val card = Card(strCardNo, months, Years, binding.etCvv.text.toString())
                Stripe().createToken(card, getString(R.string.stripe_test_key), object : TokenCallback {
                    override fun onError(error: Exception) {
                        Log.e("error.........", "" + error.toString())
                        BWSApplication.showToast("Please enter valid card details", activity)
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }

                    override fun onSuccess(token: Token) {
                        strToken = token.id
                        Log.e("strToken.............", "" + strToken)
                        if (!strToken.equals("", ignoreCase = true)) {
                            if (BWSApplication.isNetworkConnected(context)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listCall: Call<AddCardModel> = APINewClient.client.getAddCard(UserID, strToken)
                                listCall.enqueue(object : Callback<AddCardModel?> {
                                    override fun onResponse(call: Call<AddCardModel?>, response: Response<AddCardModel?>) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                        try {
                                            val cardModel = response.body()
                                            if (cardModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                                keyboard.hideSoftInputFromWindow(view.windowToken, 0)
                                                val p = Properties()
                                                p.putValue("userId", UserID)
                                                BWSApplication.addToSegment("Payment Card Add Clicked", p, CONSTANTS.track)
                                                finish()
                                                BWSApplication.showToast(cardModel.responseMessage, activity)
                                            } else if (cardModel.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
                                                BWSApplication.showToast(cardModel.responseMessage, activity)
                                            } else {
                                                BWSApplication.showToast(cardModel.responseMessage, activity)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<AddCardModel?>, t: Throwable) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    }
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            }
                        }
                    }
                })
            }
        }
    }

    private val addCardTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val CardNo = binding.etNumber.text.toString().trim()
            val CardName = binding.etName.text.toString().trim()
            val Month = binding.textMonth.text.toString().trim()
            val CVV = binding.etCvv.text.toString().trim()
            if (!CardNo.isEmpty() || !CardName.isEmpty() || !Month.isEmpty() || !CVV.isEmpty()) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(resources.getColor(R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(resources.getColor(R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun showYearDialog() {
        binding1.MonthPicker.maxValue = 12
        binding1.MonthPicker.minValue = 1
        binding1.MonthPicker.wrapSelectorWheel = false
        binding1.MonthPicker.value = month
        binding1.MonthPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding1.YearPicker.maxValue = year + 80
        binding1.YearPicker.minValue = year
        binding1.YearPicker.wrapSelectorWheel = false
        binding1.YearPicker.value = year
        binding1.YearPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        myBackPress = true
        binding1.set.setOnClickListener { v ->
            if (binding1.MonthPicker.value < month && binding1.YearPicker.value === year) {
                binding.txtError.text = "Please enter a valid expiry date"
                d!!.dismiss()
            } else {
                binding.textMonth.text = " " + binding1.MonthPicker.value.toString() + " / " + binding1.YearPicker.value
                binding.txtError.text = ""
                d!!.dismiss()
            }
        }
        binding1.cancle.setOnClickListener { v -> d!!.dismiss() }
        d!!.show()
    }

    override fun onBackPressed() {
        myBackPress = true
        if (ComePayment.equals("1", ignoreCase = true)) {
            finish()
        } else if (ComePayment.equals("2", ignoreCase = true)) {
            val i = Intent(context, PaymentActivity::class.java)
            i.putExtra("ComesTrue", ComesTrue)
            i.putExtra("comeFrom", "membership")
            i.putParcelableArrayListExtra("PlanData", listModelList2)
            i.putExtra("TrialPeriod", "")
            i.putExtra("position", position)
            startActivity(i)
            finish()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    class CreditCardFormatTextWatcher(textView: TextView) : TextWatcher {
        private val maxLength = NO_MAX_LENGTH
        private var paddingPx = 0
        private var internalStopFormatFlag = false
        fun setPaddingPx(paddingPx: Int) {
            this.paddingPx = paddingPx
        }

        fun setPaddingEm(textView: TextView, em: Float) {
            val emSize = textView.paint.measureText("x")
            setPaddingPx((em * emSize).toInt())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (internalStopFormatFlag) {
                return
            }
            internalStopFormatFlag = true
            formatCardNumber(s, paddingPx, maxLength)
            internalStopFormatFlag = false
        }

        class PaddingRightSpan(private val mPadding: Int) : ReplacementSpan() {
            override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
                val widths = FloatArray(end - start)
                paint.getTextWidths(text, start, end, widths)
                var sum: Float = mPadding.toFloat()
                for (i in widths.indices) {
                    sum += widths[i]
                }
                return sum.toInt()
            }

            override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
                canvas.drawText(text, start, end, x, y.toFloat(), paint)
            }
        }

        companion object {
            const val NO_MAX_LENGTH = -1
            fun formatCardNumber(ccNumber: Editable, paddingPx: Int, maxLength: Int) {
                val textLength = ccNumber.length
                val spans = ccNumber.getSpans(0, ccNumber.length, PaddingRightSpan::class.java)
                for (i in spans.indices) {
                    ccNumber.removeSpan(spans[i])
                }
                // then truncate to max length
                if (maxLength > 0 && textLength > maxLength - 1) {
                    ccNumber.replace(maxLength, textLength, "")
                }
                // finally add margin spans
                for (i in 1..(textLength - 1) / 4) {
                    val end = i * 4
                    val start = end - 1
                    val marginSPan = PaddingRightSpan(paddingPx)
                    ccNumber.setSpan(marginSPan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        init {
            setPaddingEm(textView, 1f)
        }
    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
            }
            numStarted++
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}