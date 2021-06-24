package com.brainwellnessspa.assessmentProgressModule.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityDoingGoodBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity

/* This is the assessment complete go to next screen activity */
class DoingGoodActivity : AppCompatActivity() {
    lateinit var binding: ActivityDoingGoodBinding

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doing_good) /* This is the doing good layout showing */

        /* This click event is going to other process */
        binding.btnContinue.setOnClickListener {
            val i = Intent(this@DoingGoodActivity, SleepTimeActivity::class.java)
            startActivity(i)
        }
    }

    /* This is the device back button click */
    override fun onBackPressed() {
        finish()
    }
}