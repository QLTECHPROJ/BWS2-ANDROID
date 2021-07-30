package com.brainwellnessspa.userModule.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.ActivityAddProfileBinding
import com.brainwellnessspa.userModule.models.AddUserModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProfileActivity : AppCompatActivity() {
    var userID: String? = ""
    private var coUserID: String? = ""
    var coEMAIL: String? = ""
    var coName: String? = ""
    var coNumber: String? = ""
    private var addProfile: String? = ""
    private lateinit var binding: ActivityAddProfileBinding
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val user: String = binding.etUser.text.toString().trim()
            val mobileNumber: String = binding.etMobileNumber.text.toString().trim()
            val email: String = binding.etEmail.text.toString().trim()
            if (user.equals("", ignoreCase = true) && mobileNumber.equals("", ignoreCase = true) && email.equals("", ignoreCase = true)) {
                checkBtnStatus(0)
                binding.ivCheckNumber.visibility = View.GONE
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!user.equals("", ignoreCase = true) && !mobileNumber.equals("", ignoreCase = true) && email.equals("", ignoreCase = true)) {
                checkBtnStatus(0)
            } else if (!user.equals("", ignoreCase = true) && mobileNumber.equals("", ignoreCase = true) && !email.equals("", ignoreCase = true)) {
                checkBtnStatus(0)
            } else if (user.equals("", ignoreCase = true) && !mobileNumber.equals("", ignoreCase = true) && !email.equals("", ignoreCase = true)) {
                checkBtnStatus(0)
            } else if (!user.equals("", ignoreCase = true) && !mobileNumber.equals("", ignoreCase = true) && !email.equals("", ignoreCase = true)) {
                checkBtnStatus(1)
            }

            if (mobileNumber.equals("", ignoreCase = true)) {
                binding.ivCheckNumber.visibility = View.GONE
            } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 || binding.etMobileNumber.text.toString().length > 10) {
                binding.ivCheckNumber.visibility = View.GONE
            } else {
                binding.ivCheckNumber.visibility = View.VISIBLE
            }

            if (email.equals("", ignoreCase = true)) {
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!email.equals("", ignoreCase = true) && !email.isEmailValid()) {
                binding.ivCheckEmail.visibility = View.GONE
            } else {
                binding.ivCheckEmail.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_profile)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        activity = this@AddProfileActivity

        if (intent.extras != null) {
            addProfile = intent.getStringExtra("AddProfile")
            coUserID = intent.getStringExtra("CoUserID")
            coEMAIL = intent.getStringExtra("CoEMAIL")
            coName = intent.getStringExtra("CoName")
            coNumber = intent.getStringExtra("CoNumber")
        }

        if (addProfile.equals("Add", ignoreCase = true)) {
            binding.btnSendPin.visibility = View.VISIBLE
            binding.btnSendNewPin.visibility = View.GONE
            binding.ivCheckNumber.visibility = View.GONE
            binding.ivCheckEmail.visibility = View.GONE
            binding.etUser.isClickable = true
            binding.etUser.isEnabled = true
            binding.etMobileNumber.isClickable = true
            binding.etMobileNumber.isEnabled = true
            binding.etEmail.isClickable = true
            binding.etEmail.isEnabled = true

            val p = Properties()
            addToSegment("Add Couser Screen Viewed", p, CONSTANTS.screen)
        } else if (addProfile.equals("Forgot", ignoreCase = true)) {
            binding.btnSendPin.visibility = View.GONE
            binding.btnSendNewPin.visibility = View.VISIBLE
            binding.ivCheckNumber.visibility = View.VISIBLE
            binding.ivCheckEmail.visibility = View.VISIBLE
            binding.etUser.setText(coName)
            binding.etMobileNumber.setText(coNumber)
            binding.etEmail.setText(coEMAIL)
            binding.etUser.isClickable = false
            binding.etUser.isEnabled = false
            binding.etMobileNumber.isClickable = false
            binding.etMobileNumber.isEnabled = false
            binding.etEmail.isClickable = false
            binding.etEmail.isEnabled = false

            val p = Properties()
            p.putValue("name", coName)
            p.putValue("mobileNo", coNumber)
            p.putValue("email", coEMAIL)
            addToSegment("Forgot Pin Screen Viewed", p, CONSTANTS.screen)
        }

        binding.llBack.setOnClickListener {
            if (addProfile.equals("Add", ignoreCase = true)) {
                finish()
            } else if (addProfile.equals("Forgot", ignoreCase = true)) {
                val i = Intent(activity, UserListActivity::class.java)
                startActivity(i)
                finish()
            }
        }
        val measureRatio = measureRatio(this, 0f, 1f, 1f, 0.32f, 0f)
        binding.civProfile.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civProfile.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.scaleType = ImageView.ScaleType.FIT_XY
        binding.civProfile.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(applicationContext).load(R.drawable.ic_default_profile_img).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).into(binding.civLetter)
        binding.etUser.addTextChangedListener(userTextWatcher)
        binding.etMobileNumber.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)

        binding.btnSendPin.setOnClickListener {
            if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.text = getString(R.string.pls_provide_name)
                binding.txtNameError.visibility = View.VISIBLE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.GONE
            } else if (binding.etMobileNumber.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = getString(R.string.pls_provide_mobileno)
                binding.txtEmailError.visibility = View.GONE
            } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 || binding.etMobileNumber.text.toString().length > 10) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = getString(R.string.pls_provide_valid_mobileno)
                binding.txtEmailError.visibility = View.GONE
            } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = getString(R.string.pls_provide_email)
            } else if (!binding.etEmail.text.toString().isEmailValid()) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = getString(R.string.pls_provide_valid_email)
            } else {
                if (isNetworkConnected(this)) {
                    showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val listCall: Call<AddUserModel> = APINewClient.client.getAddUser(userID, binding.etUser.text.toString(), binding.etEmail.text.toString())
                    listCall.enqueue(object : Callback<AddUserModel> {
                        override fun onResponse(call: Call<AddUserModel>, response: Response<AddUserModel>) {
                            try {
                                binding.flEmail.error = ""
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                val listModel: AddUserModel = response.body()!!
                                when {
                                    listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                        showToast(listModel.responseMessage, activity)
                                        val p = Properties()
                                        p.putValue("name", listModel.responseData!!.name)
                                        p.putValue("mobileNo", binding.etMobileNumber.text.toString())
                                        p.putValue("email", listModel.responseData!!.email)
                                        addToSegment("Couser Added", p, CONSTANTS.track)
                                        finish()
                                    }
                                    listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                        deleteCall(activity)
                                        showToast(listModel.responseMessage, activity)
                                        val i = Intent(activity, SignInActivity::class.java)
                                        i.putExtra("mobileNo", "")
                                        i.putExtra("countryCode", "")
                                        i.putExtra("name", "")
                                        i.putExtra("email", "")
                                        i.putExtra("countryShortName", "")
                                        startActivity(i)
                                        finish()
                                    }
                                    else -> {
                                        showToast(listModel.responseMessage, activity)
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<AddUserModel>, t: Throwable) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        }
                    })
                } else {
                    showToast(getString(R.string.no_server_found), this)
                }
            }
        }

        binding.btnSendNewPin.setOnClickListener {
            if (isNetworkConnected(activity)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall: Call<SucessModel> = APINewClient.client.getForgotPin(userID, coEMAIL)
                listCall.enqueue(object : Callback<SucessModel> {
                    override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SucessModel = response.body()!!
                        when {
                            listModel.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                val i = Intent(activity, UserListActivity::class.java)
                                startActivity(i)
                                finish()
                                showToast(listModel.responseMessage, activity)
                                val p = Properties()
                                p.putValue("name", coName)
                                p.putValue("mobileNo", coNumber)
                                p.putValue("email", coEMAIL)
                                addToSegment("Forgot Pin Clicked", p, CONSTANTS.track)
                            }
                            listModel.responseCode.equals(activity.getString(R.string.ResponseCodefail), ignoreCase = true) -> {
                                showToast(listModel.responseMessage, activity)
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }
                    }

                    override fun onFailure(call: Call<SucessModel>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                showToast(activity.getString(R.string.no_server_found), activity)
            }
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onBackPressed() {
        if (addProfile.equals("Add", ignoreCase = true)) {
            finish()
        } else if (addProfile.equals("Forgot", ignoreCase = true)) {
            val i = Intent(activity, UserListActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun checkBtnStatus(check: Int) {
        if (check == 0) {
            binding.btnSendPin.isEnabled = false
            binding.btnSendPin.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
        } else if (check == 1) {
            binding.btnSendPin.isEnabled = true
            binding.btnSendPin.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.btnSendPin.setBackgroundResource(R.drawable.light_green_rounded_filled)
        }
    }
}