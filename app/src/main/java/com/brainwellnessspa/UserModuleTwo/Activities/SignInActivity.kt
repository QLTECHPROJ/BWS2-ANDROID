package com.brainwellnessspa.UserModuleTwo.Activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.SignInModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySignInBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    var fcm_id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.llBack.setOnClickListener {
            finish()
        }
        binding.tvForgotPswd.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPswdActivity::class.java)
            startActivity(i)
        }

        binding.ivVisible.visibility = View.VISIBLE
        binding.ivInVisible.visibility = View.GONE
        binding.ivVisible.setOnClickListener {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.GONE
            binding.ivInVisible.visibility = View.VISIBLE
        }
        binding.ivInVisible.setOnClickListener {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.VISIBLE
            binding.ivInVisible.visibility = View.GONE
        }

        binding.btnLoginAc.setOnClickListener {
            prepareData()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun prepareData() {
        val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
        fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "")!!
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this, OnCompleteListener { task: Task<InstallationTokenResult> ->
                val newToken = task.result!!.token
                Log.e("newToken", newToken)
                val editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
                editor.putString(CONSTANTS.Token, newToken) //Friend
                editor.apply()
                editor.commit()
            })
            val sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "")!!
        }
        if (binding.etEmail.text.toString().equals("")) {
            binding.flEmail.error = "Email address is required"
            binding.flPassword.error = ""
        } else if (!binding.etEmail.text.toString().equals("")
                && !BWSApplication.isEmailValid(binding.etEmail.text.toString())) {
            binding.flEmail.error = "Valid Email address is required"
            binding.flPassword.error = ""
        } else if (binding.etPassword.text.toString().equals("")) {
            binding.flEmail.error = ""
            binding.flPassword.error = "Password is required"
        } else {
            binding.flEmail.error = ""
            binding.flPassword.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                val listCall: Call<SignInModel> = APINewClient.getClient().getSignIn(binding.etEmail.text.toString(), binding.etPassword.text.toString(), CONSTANTS.FLAG_ONE, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID), fcm_id)
                listCall.enqueue(object : Callback<SignInModel> {
                    override fun onResponse(call: Call<SignInModel>, response: Response<SignInModel>) {
                        try {
                            binding.flEmail.error = ""
                            binding.flPassword.error = ""
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                            val listModel: SignInModel = response.body()!!
                            if (listModel.getResponseCode().equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN, MODE_PRIVATE)
                                val editor = shared.edit()
                                editor.putString(CONSTANTS.PREFE_ACCESS_UserID, listModel.getResponseData()?.iD)
                                editor.commit()
                                val i = Intent(this@SignInActivity, UserListActivity::class.java)
                                i.putExtra(CONSTANTS.PopUp, "0")
                                startActivity(i)
                                BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                            } else {
                                BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SignInModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}