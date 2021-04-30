package com.brainwellnessspa.ProfileTwoModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.ChangePasswordModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    var USERID: String? = null
    var CoUserID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        binding.llBack.setOnClickListener {
            val i = Intent(this, AccountInfoActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnSave.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        if (binding.etCurrentPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = "Current Login PIN is required"
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
        } else if (binding.etNewPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = "New Login PIN is required"
            binding.flConfirmPIN.error = ""
        } else if (binding.etConfirmPIN.text.toString().equals("")) {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = "Confirm new Login PIN is required"
        } else {
            binding.flCurrentPIN.error = ""
            binding.flNewPIN.error = ""
            binding.flConfirmPIN.error = ""
            if (BWSApplication.isNetworkConnected(this)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePasswordActivity)
                val listCall: Call<ChangePasswordModel> = APINewClient.getClient().getChangePassword(USERID,
                        binding.etCurrentPIN.text.toString(),
                        binding.etConfirmPIN.text.toString())
                listCall.enqueue(object : Callback<ChangePasswordModel> {
                    override fun onResponse(call: Call<ChangePasswordModel>, response: Response<ChangePasswordModel>) {
                        try {
                            binding.flCurrentPIN.error = ""
                            binding.flNewPIN.error = ""
                            binding.flConfirmPIN.error = ""
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePasswordActivity)
                            val listModel: ChangePasswordModel = response.body()!!
                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                finish()
                                BWSApplication.showToast(listModel.responseMessage, applicationContext)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, applicationContext)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ChangePasswordModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@ChangePasswordActivity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this, AccountInfoActivity::class.java)
        startActivity(i)
        finish()
    }
}