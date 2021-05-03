package com.brainwellnessspa.ProfileTwoModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityAccountInfoBinding

class AccountInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_info)

        binding.llBack.setOnClickListener {
            finish()
        }

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