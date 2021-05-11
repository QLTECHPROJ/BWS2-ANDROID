package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityGetStartedBinding
import com.segment.analytics.Properties

class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding
    var ScreenVisible: String = ""
    lateinit var activity: Activity
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_started)
        activity = this@GetStartedActivity
        ctx = this@GetStartedActivity

        val p = Properties()
        BWSApplication.addToSegment("Launch Screen Viewed", p, CONSTANTS.screen)
        binding.btnGetStarted.setOnClickListener {
            val i = Intent(ctx, CreateAccountActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnAlreadyAc.setOnClickListener {
            val i = Intent(ctx, SignInActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}