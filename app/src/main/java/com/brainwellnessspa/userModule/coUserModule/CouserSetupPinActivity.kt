package com.brainwellnessspa.userModule.coUserModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityCouserSetupPinBinding

class CouserSetupPinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCouserSetupPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_couser_setup_pin)

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            val intent = Intent(applicationContext, UserDetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}