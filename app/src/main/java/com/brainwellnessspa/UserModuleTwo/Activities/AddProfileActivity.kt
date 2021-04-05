package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.AddUserModel
import com.brainwellnessspa.UserModuleTwo.Models.ForgotPasswordModel
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
    private lateinit var binding: ActivityAddProfileBinding

    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val User: String = binding.etUser.getText().toString().trim()
            val MobileNumber: String = binding.etMobileNumber.getText().toString().trim()
            val Email: String = binding.etEmail.getText().toString().trim()
            if (User.equals("", ignoreCase = true) && MobileNumber.equals("", ignoreCase = true)
                    && Email.equals("", ignoreCase = true)) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (User.equals("", ignoreCase = true)) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (MobileNumber.equals("", ignoreCase = true)) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (Email.equals("", ignoreCase = true)) {
                binding.btnSendPin.setEnabled(false)
                binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnSendPin.setEnabled(true)
                binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                binding.btnSendPin.setBackgroundResource(R.drawable.extra_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_profile)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
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
        binding.rlImageUpload.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
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
                binding.flMobileNumber.error = "Number is required"
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
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@AddProfileActivity)
                    val listCall: Call<AddUserModel> = APINewClient.getClient().getAddUser(UserID, binding.etUser.text.toString(),
                            binding.etEmail.text.toString(), binding.etMobileNumber.text.toString())
                    listCall.enqueue(object : Callback<AddUserModel> {
                        override fun onResponse(call: Call<AddUserModel>, response: Response<AddUserModel>) {
                            try {
                                binding.flEmail.error = ""
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@AddProfileActivity)
                                val listModel: AddUserModel = response.body()!!
                                if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                                    finish()
                                } else {
                                    BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<AddUserModel>, t: Throwable) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@AddProfileActivity)
                        }
                    })
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), this)
                }
            }
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onBackPressed() {
       /* val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
        i.putExtra(CONSTANTS.PopUp, "0")
        startActivity(i)*/
        finish()
    }
}