package com.brainwellnessspa.ProfileTwoModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityChangePinBinding

class ChangePinActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pin)

        binding.llBack.setOnClickListener {
            finish()
        }
    }
}