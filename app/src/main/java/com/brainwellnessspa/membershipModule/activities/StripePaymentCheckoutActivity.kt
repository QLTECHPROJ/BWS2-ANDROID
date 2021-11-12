package com.brainwellnessspa.membershipModule.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ReplacementSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.measureRatio
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.ActivityStripePaymentCheckoutBinding
import com.brainwellnessspa.databinding.YeardialogBinding
import com.brainwellnessspa.membershipModule.models.MembershipPlanListModel
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.MeasureRatio
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StripePaymentCheckoutActivity : AppCompatActivity() {

    lateinit var binding: ActivityStripePaymentCheckoutBinding
    var MobileNo: String? = ""
    var Code: String? = ""
    var Name: String? = ""
    var Promocode: String? = ""
    var context: Context? = null
    var activity: Activity? = null
    var d: Dialog? = null
    var a = 0
    var UserId:String = ""
    var planId:String = ""
    var price:String = ""
    var planFlag:String = ""
    var TrialPeriod: String? = null
    var position = 0
    var year = 0
    var month = 0
    val gson = Gson()
    lateinit var binding1: YeardialogBinding
    var strToken: String? = null
    private var listModelList: ArrayList<MembershipPlanListModel.Plan>? = null
    private val mLastClickTime: Long = 0
    private var doubleBackToExitPressedOnce = false
    private val addCardTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //            binding.nestedScroll.smoothScrollTo(0, binding.nestedScroll.getChildAt(0).getHeight());
            val CardNo: String = binding.etNumber.text.toString().trim()
            val CardName: String = binding.etName.text.toString().trim()
            val Month: String = binding.textMonth.text.toString().trim()
            val CVV: String = binding.etCvv.text.toString().trim()
            if (!CardNo.isEmpty() || !CardName.isEmpty() || !Month.isEmpty() || !CVV.isEmpty()) {
                binding.btnPayment.isEnabled = true
                binding.btnPayment.setTextColor(resources.getColor(R.color.white))
                binding.btnPayment.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                binding.btnPayment.isEnabled = false
                binding.btnPayment.setTextColor(resources.getColor(R.color.light_gray))
                binding.btnPayment.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    } 
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) 
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stripe_payment_checkout)
        context = this@StripePaymentCheckoutActivity
        activity = this@StripePaymentCheckoutActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "").toString()
        if (intent.extras != null) {
           /* MobileNo = intent.getStringExtra("MobileNo")
            Code = intent.getStringExtra("Code")
            Name = intent.getStringExtra(CONSTANTS.Name)
            Promocode = intent.getStringExtra(CONSTANTS.Promocode)*/
            TrialPeriod = intent.getStringExtra("TrialPeriod")
            val json4 = intent.getStringExtra("PlanData")
            val type1 = object : TypeToken<ArrayList<MembershipPlanListModel.Plan>?>() {}.type
            listModelList = gson.fromJson(json4, type1)
            position = intent.getIntExtra("position", 0)
        }
        binding.llBack.setOnClickListener { view ->
            callBack()
        }
        year = Calendar.getInstance()[Calendar.YEAR]
        month = Calendar.getInstance()[Calendar.MONTH]
        month += 1
        d = Dialog(this)
        d!!.setTitle("Year Picker")
        binding1 = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.yeardialog, null, false)
        d!!.setContentView(binding1.root)

        /* binding.etNumber.addTextChangedListener(addCardTextWatcher);
        binding.etName.addTextChangedListener(addCardTextWatcher);
        binding.textMonth.addTextChangedListener(addCardTextWatcher);
        binding.etCvv.addTextChangedListener(addCardTextWatcher);*/

        //        DecimalFormat precision = new DecimalFormat("#.##");
        planFlag = listModelList!![position].planFlag!!
        price = listModelList!![position].planAmount!!
        planId = listModelList!![position].planID!!
        binding.tvDoller.text = "$$price"
        //        binding.tvDoller.setText("$" + precision.format(price));
        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.etNumber.text.toString().length === 16) {
                    binding.etName.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        val measureRatio: MeasureRatio = measureRatio(this@StripePaymentCheckoutActivity, 0f, 5f, 3f, 1f, 0f)
        binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
        binding.ivRestaurantImage.setImageResource(R.drawable.ic_checkout_card_logo)
        binding.opendilog.setOnClickListener { v ->
            a = 1
            showYearDialog()
        }
        val tv = CreditCardFormatTextWatcher(binding.etNumber)
        binding.etNumber.addTextChangedListener(tv)
        binding.etName.addTextChangedListener(addCardTextWatcher)
        binding.etNumber.addTextChangedListener(addCardTextWatcher)
        binding.etCvv.addTextChangedListener(addCardTextWatcher)
        binding.textMonth.addTextChangedListener(addCardTextWatcher)
        binding.btnPayment.setOnClickListener { view ->
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
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                binding.tlName.error = ""
                binding.tlNumber.error = ""
                binding.txtError.text = ""
                val strCardNo: String = binding.etNumber.text.toString().trim().replace("\\s+", "")
                val months: Int = binding1.MonthPicker.value
                val Years: Int = binding1.YearPicker.value
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
                                val listCall: Call<SucessModel> = APINewClient.client.getMembershipPayment(UserId, planId, planFlag, strToken)
                                listCall.enqueue(object : Callback<SucessModel> {
                                    override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                        val listModel: SucessModel = response.body()!!
                                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess))) {
                                            callGetCoUserDetails()
                                        }
                                    }

                                    override fun onFailure(call: Call<SucessModel>, t: Throwable) {
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

    private fun callGetCoUserDetails() {

        val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(UserId)
        listCall.enqueue(object : Callback<AuthOtpModel> {
            override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                try {
                    val listModel: AuthOtpModel = response.body()!!
                    if (listModel.ResponseCode == getString(R.string.ResponseCodesuccess)) {
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
                        if (listModel.ResponseData.paymentType == "0") {
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
                        } else if (listModel.ResponseData.paymentType == "1") {
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
                        val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                        val edited = shred.edit()
                        edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                        val selectedCategoriesTitle = arrayListOf<String>()
                        val selectedCategoriesName = arrayListOf<String>()
                        val gson = Gson()
                        for (i in listModel.ResponseData.AreaOfFocus) {
                            selectedCategoriesTitle.add(i.MainCat)
                            selectedCategoriesName.add(i.RecommendedCat)
                        }
                        edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                        edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                        edited.apply()

                        var p = Properties()
                        p.putValue("planId", listModelList!![position].planID)
                        p.putValue("plan", listModelList!![position].subName)
                        p.putValue("planAmount", listModelList!![position].planAmount)
                        p.putValue("planInterval", listModelList!![position].planInterval)
                        p.putValue("totalProfile", "2")
                        BWSApplication.addToSegment("Checkout Completed", p, CONSTANTS.track)
                        val i = Intent(context, EnhanceDoneActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NO_HISTORY
                        i.putExtra("Name", "")
                        i.putExtra("Code", "")
                        i.putExtra("MobileNo", "")
                        i.putExtra("PlanData", gson.toJson(listModelList))
                        i.putExtra("TrialPeriod", "")
                        i.putExtra("position", position)
                        i.putExtra("Promocode", "")
                        startActivity(i)
                        finish()

                    } else if (listModel.ResponseCode == getString(R.string.ResponseCodeDeleted)) {
                        BWSApplication.callDelete403(activity, listModel.ResponseMessage)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
            }
        })
    }

    private fun callBack() {
        val i = Intent(context, OrderSummaryActivity::class.java)
        i.putExtra("PlanData", gson.toJson(listModelList))
        i.putExtra("TrialPeriod", "")
        i.putExtra("position", position)
        i.putExtra("Promocode", "")
        startActivity(i)
        finish()
    }

    override fun onBackPressed() {
       callBack()
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
            override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
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
                // first remove any previous span
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
}