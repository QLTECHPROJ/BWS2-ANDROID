package com.brainwellnessspa.userModuleTwo.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.dassAssSlider.activities.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityWalkScreenBinding

class WalkScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkScreenBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var email: String? = ""
    var name: String? = ""
    var screenView: String? = ""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_screen)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
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
            screenView.equals("1") -> {
                binding.rlWelcome.visibility = View.VISIBLE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
            }
            screenView.equals("2") -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.VISIBLE
                binding.rlStepThree.visibility = View.GONE
            }
            screenView.equals("3") -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.VISIBLE
            }
        }

        binding.btnContinue.setOnClickListener {
            binding.rlWelcome.visibility = View.GONE
            binding.rlStepOne.visibility = View.VISIBLE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.GONE
        }

        binding.rlStepOne.setOnClickListener {
            val intent = Intent(this@WalkScreenActivity, ProfileProgressActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rlStepTwo.setOnClickListener {
            val intent = Intent(this@WalkScreenActivity, AssProcessActivity::class.java)
            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
            startActivity(intent)
            finish()
        }

        binding.rlStepThree.setOnClickListener {
            val intent = Intent(this@WalkScreenActivity, DoingGoodActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}