package com.brainwellnessspa.UserModuleTwo.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.ManageModule.ManageActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityGetStartedBinding


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

        prepareData()
    }

    fun prepareData() {

    }

    override fun onBackPressed() {
        finishAffinity()
    }
}