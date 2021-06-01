package com.brainwellnessspa.userModuleTwo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityDoingGoodBinding
import com.brainwellnessspa.manageModule.SleepTimeActivity

class DoingGoodActivity : AppCompatActivity() {
    lateinit var binding: ActivityDoingGoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doing_good)

        binding.btnContinue.setOnClickListener {
            val i = Intent(this@DoingGoodActivity, SleepTimeActivity::class.java)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}