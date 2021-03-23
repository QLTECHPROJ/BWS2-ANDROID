package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.LoginModule.Models.CountryListModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySignInBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
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

        binding.btnCreateAc.setOnClickListener {
            if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.flEmail.error = "Email address is required"
                binding.flPassword.error = ""
            } else if (binding.etPassword.text.toString().equals("", ignoreCase = true)) {
                binding.flEmail.error = ""
                binding.flPassword.error = "Password is required"
            } else {
                val i = Intent(this@SignInActivity, UserListActivity::class.java)
                i.putExtra(CONSTANTS.PopUp,"0")
                startActivity(i)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

/*
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, this@SignInActivity)
            val listCall: Call<CountryListModel> = APINewClient.getClient().getLogins()
            listCall.enqueue(object : Callback<CountryListModel> {
                override fun onResponse(call: Call<CountryListModel>, response: Response<CountryListModel>) {
                    try {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, this@SignInActivity)
                        val listModel: CountryListModel = response.body()!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CountryListModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(progressBar, null, this@SignInActivity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }
*/
}