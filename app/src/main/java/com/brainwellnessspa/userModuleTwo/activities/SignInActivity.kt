package com.brainwellnessspa.userModuleTwo.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySignInBinding
import com.brainwellnessspa.userModuleTwo.models.SignInModel
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    var fcmId: String = ""
    lateinit var activity: Activity

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val email: String = binding.etEmail.text.toString().trim()
            val pass: String = binding.etPassword.text.toString().trim()
            when {
                email.equals("", ignoreCase = true) -> {
                    binding.btnLoginAc.isEnabled = false
                    binding.btnLoginAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnLoginAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                pass.equals("", ignoreCase = true) -> {
                    binding.btnLoginAc.isEnabled = false
                    binding.btnLoginAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnLoginAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                else -> {
                    binding.btnLoginAc.isEnabled = true
                    binding.btnLoginAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnLoginAc.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }

            if (pass.equals("", ignoreCase = true)) {
                binding.ivVisible.isClickable = false
                binding.ivVisible.isEnabled = false
                binding.ivInVisible.isClickable = false
                binding.ivInVisible.isEnabled = false
            } else {
                binding.ivVisible.isClickable = true
                binding.ivVisible.isEnabled = true
                binding.ivInVisible.isClickable = true
                binding.ivInVisible.isEnabled = true
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        activity = this@SignInActivity
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.llBack.setOnClickListener {
            val i = Intent(activity, GetStartedActivity::class.java)
            startActivity(i)
            finish()
        }
        val p = Properties()
        BWSApplication.addToSegment("Login Screen Viewed", p, CONSTANTS.screen)
        binding.etEmail.addTextChangedListener(userTextWatcher)
        binding.etPassword.addTextChangedListener(userTextWatcher)

        binding.tvForgotPswd.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPswdActivity::class.java)
            startActivity(i)
        }

        if (binding.etPassword.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivVisible.isClickable = false
            binding.ivVisible.isEnabled = false
            binding.ivInVisible.isClickable = false
            binding.ivInVisible.isEnabled = false
        }

        binding.ivVisible.visibility = View.VISIBLE
        binding.ivInVisible.visibility = View.GONE
        binding.ivVisible.setOnClickListener {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.GONE
            binding.ivInVisible.visibility = View.VISIBLE
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
        }
        binding.ivInVisible.setOnClickListener {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.VISIBLE
            binding.ivInVisible.visibility = View.GONE
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
        }

        binding.btnLoginAc.setOnClickListener {
            prepareData()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val passwordPatterned = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPatterned)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onBackPressed() {
        val i = Intent(activity, GetStartedActivity::class.java)
        startActivity(i)
        finish()
    }

    @SuppressLint("HardwareIds")
    fun prepareData() {
        val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
        fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")!!
        if (TextUtils.isEmpty(fcmId)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(
                this
            ) { task: Task<InstallationTokenResult> ->
                val newToken = task.result!!.token
                Log.e("newToken", newToken)
                val editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
                editor.putString(CONSTANTS.Token, newToken) //Friend
                editor.apply()
                editor.commit()
            }
            val sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcmId = sharedPreferences3.getString(CONSTANTS.Token, "")!!
        }
        if (binding.etEmail.text.toString() == "") {
            binding.txtEmailError.visibility = View.VISIBLE
            binding.txtEmailError.text = "Please provide a email address"
            binding.txtPassowrdError.visibility = View.GONE
        } else if (binding.etEmail.text.toString() != ""
            && !BWSApplication.isEmailValid(binding.etEmail.text.toString())
        ) {
            binding.txtEmailError.visibility = View.VISIBLE
            binding.txtEmailError.text = "Please provide a valid email address"
            binding.txtPassowrdError.visibility = View.GONE
        } else if (binding.etPassword.text.toString() == "") {
            binding.txtEmailError.visibility = View.GONE
            binding.txtPassowrdError.visibility = View.VISIBLE
            binding.txtPassowrdError.text = "Please enter password"
        } else if (binding.etPassword.text.toString().length < 8
            || !isValidPassword(binding.etPassword.text.toString())
        ) {
            binding.txtEmailError.visibility = View.GONE
            binding.txtPassowrdError.visibility = View.VISIBLE
            binding.txtPassowrdError.text = "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
        } else {
            binding.txtEmailError.visibility = View.GONE
            binding.txtPassowrdError.visibility = View.GONE
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    this@SignInActivity
                )
                val listCall: Call<SignInModel> = APINewClient.getClient().getSignIn(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    CONSTANTS.FLAG_ONE,
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                    fcmId
                )
                listCall.enqueue(object : Callback<SignInModel> {
                    override fun onResponse(
                        call: Call<SignInModel>,
                        response: Response<SignInModel>
                    ) {
                        try {
                            binding.txtEmailError.visibility = View.GONE
                            binding.txtPassowrdError.visibility = View.GONE
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                this@SignInActivity
                            )
                            val listModel: SignInModel = response.body()!!
                            if (listModel.getResponseCode().equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                val shared = getSharedPreferences(
                                    CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                                    MODE_PRIVATE
                                )
                                val editor = shared.edit()
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_UserID,
                                    listModel.getResponseData()?.iD
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_NAME,
                                    listModel.getResponseData()?.name
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_USEREMAIL,
                                    listModel.getResponseData()?.email
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_DeviceType,
                                    CONSTANTS.FLAG_ONE
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_DeviceID,
                                    Settings.Secure.getString(
                                        contentResolver,
                                        Settings.Secure.ANDROID_ID
                                    )
                                )
                                editor.apply()
                                val i = Intent(this@SignInActivity, UserListActivity::class.java)
                                startActivity(i)
                                finish()
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)

                                val p = Properties()
                                p.putValue("userId", listModel.getResponseData()!!.iD)
                                p.putValue("name", listModel.getResponseData()!!.name)
                                p.putValue("mobileNo", listModel.getResponseData()!!.mobileNo)
                                p.putValue("email", listModel.getResponseData()!!.email)
                                BWSApplication.addToSegment("User Login", p, CONSTANTS.track)
                            } else {
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SignInModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            this@SignInActivity
                        )
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }
}