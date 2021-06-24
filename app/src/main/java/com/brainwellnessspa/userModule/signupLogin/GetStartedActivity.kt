package com.brainwellnessspa.userModule.signupLogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityGetStartedBinding
import com.segment.analytics.Properties

class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding
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
            if (BWSApplication.isNetworkConnected(this)) {
                val i = Intent(ctx, SignUpActivity::class.java)
                i.putExtra("mobileNo", "")
                i.putExtra("countryCode", "")
                i.putExtra("name", "")
                i.putExtra("email", "")
                i.putExtra("countryShortName", "")
                startActivity(i)
                finish()
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }

        binding.btnAlreadyAc.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this)) {
                val i = Intent(ctx, SignInActivity::class.java)
                i.putExtra("mobileNo", "")
                i.putExtra("countryCode", "")
                i.putExtra("name", "")
                i.putExtra("email", "")
                i.putExtra("countryShortName", "")
                startActivity(i)
                finish()
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}