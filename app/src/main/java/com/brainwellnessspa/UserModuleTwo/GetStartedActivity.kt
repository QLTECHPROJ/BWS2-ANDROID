package com.brainwellnessspa.UserModuleTwo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity
import com.brainwellnessspa.DassAssSliderTwo.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityGetStartedBinding


class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding
    var ScreenVisible: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_started)

        if (intent.extras != null) {
            ScreenVisible = intent.getStringExtra(CONSTANTS.ScreenVisible).toString()
        }

        if (ScreenVisible.equals("1", ignoreCase = true)) {
            binding.llGetStarted.visibility = View.VISIBLE
            binding.llContinue.visibility = View.GONE
        } else if (ScreenVisible.equals("2", ignoreCase = true)) {
            binding.llGetStarted.visibility = View.GONE
            binding.llContinue.visibility = View.VISIBLE
        }

        binding.btnGetStarted.setOnClickListener {
            val i = Intent(this@GetStartedActivity, CreateAccountActivity::class.java)
            startActivity(i)
        }

        binding.btnAlreadyAc.setOnClickListener {
            val i = Intent(this@GetStartedActivity, SignInActivity::class.java)
            startActivity(i)
        }

        binding.btnContinue.setOnClickListener {
            val i = Intent(this@GetStartedActivity, ProfileProgressActivity::class.java)
            startActivity(i)
        }
        prepareData()
    }

    fun prepareData() {
        binding.tvTitle.text = "Lorem ipsum dolor"
        binding.tvSubTitle.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Consectetur adipiscing elit duis tristique sollicitudin nibh sit amet commodo."
        binding.tvCETitle.text = "You are Doing Good"
        binding.tvCESubTitle.text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut"
    }
}