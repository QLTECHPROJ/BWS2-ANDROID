package com.brainwellnessspa.userModule.signupLogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.DassAssSliderActivity
import com.brainwellnessspa.assessmentProgressModule.activities.DoingGoodActivity
import com.brainwellnessspa.databinding.ActivityWalkScreenBinding
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.coUserModule.UserDetailActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class WalkScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkScreenBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var email: String? = ""
    var name: String? = ""
    var screenView: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_screen)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        email = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        name = shared.getString(CONSTANTS.PREFE_ACCESS_NAME, "")

        if (intent.extras != null) {
            screenView = intent.getStringExtra(CONSTANTS.ScreenView)
        }

        binding.tvName.text = "Hi, $name"
        binding.rlWelcome.visibility = View.VISIBLE
        binding.rlStepOne.visibility = View.GONE
        binding.rlStepTwo.visibility = View.GONE
        binding.rlStepThree.visibility = View.GONE

        when {
            screenView.equals("1", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.VISIBLE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.GONE
            }
            screenView.equals("2", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.VISIBLE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.GONE
            }
            screenView.equals("3", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.VISIBLE
                binding.rlStepFour.visibility = View.GONE
            }
            screenView.equals("4", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.VISIBLE
            }
        }

        binding.btnContinue.setOnClickListener {
            binding.rlWelcome.visibility = View.GONE
            binding.rlStepOne.visibility = View.VISIBLE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.GONE
        }

        binding.rlStepOne.setOnClickListener {
            val intent = Intent(applicationContext, DassAssSliderActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rlStepTwo.setOnClickListener {
            val p = Properties()
            BWSApplication.addToSegment("Profile Step Start Screen Viewed", p, CONSTANTS.screen)
            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rlStepThree.setOnClickListener {
            val intent = Intent(applicationContext, DoingGoodActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rlStepFour.setOnClickListener {
            val intent = Intent(applicationContext, UserDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}