package com.brainwellnessspa.ProfileTwoModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        binding.llBack.setOnClickListener {
            finish()
        }
    }
}