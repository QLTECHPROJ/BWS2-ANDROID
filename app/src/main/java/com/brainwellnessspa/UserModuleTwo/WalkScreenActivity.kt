package com.brainwellnessspa.UserModuleTwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityWalkScreenBinding

class WalkScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_screen)

        binding.rlWelcome.visibility = View.VISIBLE
        binding.rlStepOne.visibility = View.GONE
        binding.rlStepTwo.visibility = View.GONE
        binding.rlStepThree.visibility = View.GONE

        binding.btnContinue.setOnClickListener {
            binding.rlStepOne.visibility = View.VISIBLE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.GONE
        }

        binding.rlStepOne.setOnClickListener {
            binding.rlStepOne.visibility = View.GONE
            binding.rlStepTwo.visibility = View.VISIBLE
            binding.rlStepThree.visibility = View.GONE
        }

        binding.rlStepTwo.setOnClickListener {
            binding.rlStepOne.visibility = View.GONE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.VISIBLE
        }

        binding.rlStepThree.setOnClickListener {
            val i = Intent(this@WalkScreenActivity, GetStartedActivity::class.java)
            i.putExtra(CONSTANTS.ScreenVisible, "2")
            startActivity(i)
        }
    }
}