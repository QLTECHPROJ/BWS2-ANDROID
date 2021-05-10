package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.AddUserModel
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAddProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProfileActivity : AppCompatActivity() {
    var UserID: String? = null
    var CoUserID: String? = null
    var CoEMAIL: String? = null
    var CoName: String? = null
    var CoNumber: String? = null
    var AddProfile: String? = null
    private lateinit var binding: ActivityAddProfileBinding
    lateinit var activity: Activity


    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val User: String = binding.etUser.getText().toString().trim()
            val MobileNumber: String = binding.etMobileNumber.getText().toString().trim()
            val Email: String = binding.etEmail.getText().toString().trim()
            if (User.equals("", ignoreCase = true) && MobileNumber.equals("", ignoreCase = true)
                && Email.equals("", ignoreCase = true)
            ) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
                binding.ivCheckNumber.visibility = View.GONE
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!User.equals("", ignoreCase = true) && !MobileNumber.equals(
                    "",
                    ignoreCase = true
                )
                && Email.equals("", ignoreCase = true)
            ) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (!User.equals("", ignoreCase = true) && MobileNumber.equals(
                    "",
                    ignoreCase = true
                )
                && !Email.equals("", ignoreCase = true)
            ) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (User.equals("", ignoreCase = true) && !MobileNumber.equals(
                    "",
                    ignoreCase = true
                )
                && !Email.equals("", ignoreCase = true)
            ) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (!User.equals("", ignoreCase = true) && !MobileNumber.equals(
                    "",
                    ignoreCase = true
                )
                && !Email.equals("", ignoreCase = true)
            ) {
                binding.btnSendPin.setEnabled(true)
                binding.btnSendPin.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }

            if (MobileNumber.equals("", ignoreCase = true)) {
                binding.ivCheckNumber.visibility = View.GONE
            } else if (binding.etMobileNumber.getText().toString().length == 1
                || binding.etMobileNumber.getText().toString().length < 8
                || binding.etMobileNumber.getText().toString().length > 10
            ) {
                binding.ivCheckNumber.visibility = View.GONE
            } else {
                binding.ivCheckNumber.visibility = View.VISIBLE
            }

            if (Email.equals("", ignoreCase = true)) {
                binding.ivCheckEmail.visibility = View.GONE
            } else if (!Email.equals("", ignoreCase = true) && !Email.toString().isEmailValid()) {
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
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        activity = this@AddProfileActivity

        if (intent.extras != null) {
            AddProfile = intent.getStringExtra("AddProfile")
            CoUserID = intent.getStringExtra("CoUserID")
            CoEMAIL = intent.getStringExtra("CoEMAIL")
            CoName = intent.getStringExtra("CoName")
            CoNumber = intent.getStringExtra("CoNumber")
        }

        if (AddProfile.equals("Add", ignoreCase = true)) {
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
        } else if (AddProfile.equals("Forgot", ignoreCase = true)) {
            binding.btnSendPin.visibility = View.GONE
            binding.btnSendNewPin.visibility = View.VISIBLE
            binding.ivCheckNumber.visibility = View.VISIBLE
            binding.ivCheckEmail.visibility = View.VISIBLE
            binding.etUser.setText(CoName)
            binding.etMobileNumber.setText(CoNumber)
            binding.etEmail.setText(CoEMAIL)
            binding.etUser.isClickable = false
            binding.etUser.isEnabled = false
            binding.etMobileNumber.isClickable = false
            binding.etMobileNumber.isEnabled = false
            binding.etEmail.isClickable = false
            binding.etEmail.isEnabled = false

        }

        binding.llBack.setOnClickListener {
            /*  val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
              i.putExtra(CONSTANTS.PopUp, "0")
              startActivity(i)*/
            finish()
        }
        val measureRatio = BWSApplication.measureRatio(this, 0f, 1f, 1f, 0.32f, 0f)
        binding.civProfile.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civProfile.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.height =
            (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.width =
            (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.scaleType = ImageView.ScaleType.FIT_XY
        binding.civProfile.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(applicationContext).load(R.drawable.ic_default_profile_img)
            .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
            .into(binding.civLetter)
        binding.etUser.addTextChangedListener(userTextWatcher)
        binding.etMobileNumber.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)

//        TODO MANSI Gentle reminder Send New PIN Btn name changes when user send new pin
        binding.btnSendPin.setOnClickListener {
            if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = "Name is required"
                binding.flMobileNumber.error = ""
                binding.flEmail.error = ""
            } else if (binding.etMobileNumber.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = "Mobile number is required"
                binding.flEmail.error = ""
            } else if (binding.etMobileNumber.text.toString().length == 1 || binding.etMobileNumber.text.toString().length < 8 ||
                binding.etMobileNumber.text.toString().length > 10
            ) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = "Valid Mobile number is required"
                binding.flEmail.error = ""
            } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = ""
                binding.flEmail.error = "Email address is required"
            } else if (!binding.etEmail.text.toString().isEmailValid()) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = ""
                binding.flEmail.error = "Valid Email address is required"
            } else {
                if (BWSApplication.isNetworkConnected(this)) {
                    BWSApplication.showProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                    val listCall: Call<AddUserModel> = APINewClient.getClient().getAddUser(
                        UserID, binding.etUser.text.toString(),
                        binding.etEmail.text.toString(), binding.etMobileNumber.text.toString()
                    )
                    listCall.enqueue(object : Callback<AddUserModel> {
                        override fun onResponse(
                            call: Call<AddUserModel>,
                            response: Response<AddUserModel>
                        ) {
                            try {
                                binding.flEmail.error = ""
                                BWSApplication.hideProgressBar(
                                    binding.progressBar,
                                    binding.progressBarHolder,
                                    activity
                                )
                                val listModel: AddUserModel = response.body()!!
                                if (listModel.responseCode.equals(
                                        getString(R.string.ResponseCodesuccess),
                                        ignoreCase = true
                                    )
                                ) {
                                    BWSApplication.showToast(listModel.responseMessage, activity)
                                    finish()
                                } else {
                                    BWSApplication.showToast(listModel.responseMessage, activity)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<AddUserModel>, t: Throwable) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                        }
                    })
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), this)
                }
            }
        }

        binding.btnSendNewPin.setOnClickListener {
            if (BWSApplication.isNetworkConnected(activity)) {
                BWSApplication.showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    activity
                )
                val listCall: Call<ForgotPinModel> =
                    APINewClient.getClient().getForgotPin(UserID, CoUserID, CoEMAIL)
                listCall.enqueue(object : Callback<ForgotPinModel> {
                    override fun onResponse(
                        call: Call<ForgotPinModel>,
                        response: Response<ForgotPinModel>
                    ) {
                        try {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModel: ForgotPinModel = response.body()!!
                            if (listModel.getResponseCode().equals(
                                    activity.getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                BWSApplication.showToast(
                                    listModel.getResponseMessage(),
                                    activity
                                )
                                finish()
                            } else if (listModel.getResponseCode().equals(
                                    activity.getString(R.string.ResponseCodefail),
                                    ignoreCase = true
                                )
                            ) {
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)
                            } else {
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ForgotPinModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                    }
                })
            } else {
                BWSApplication.showToast(activity.getString(R.string.no_server_found), activity)
            }
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    override fun onBackPressed() {
        /* val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
         i.putExtra(CONSTANTS.PopUp, "0")
         startActivity(i)*/
        finish()
    }
}