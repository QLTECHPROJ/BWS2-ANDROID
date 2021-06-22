package com.brainwellnessspa.userModule.signupLogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEmailVerifyBinding

class EmailVerifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_verify)

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}