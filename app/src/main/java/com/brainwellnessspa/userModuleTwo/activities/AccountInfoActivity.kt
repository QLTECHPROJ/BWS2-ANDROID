package com.brainwellnessspa.userModuleTwo.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAccountInfoBinding
import com.segment.analytics.Properties

class AccountInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountInfoBinding
    var userId: String? = null
    var coUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_info)
        val shared1: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        binding.llBack.setOnClickListener {
            finish()
        }

        val p = Properties()
        p.putValue("coUserId", coUserId)
        BWSApplication.addToSegment("Account Info Screen Viewed", p, CONSTANTS.screen)
        binding.llEtProfile.setOnClickListener {
            val i = Intent(this, EditProfileActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.llChangePswd.setOnClickListener {
            val i = Intent(this, ChangePasswordActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.llChangePin.setOnClickListener {
            val i = Intent(this, ChangePinActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}