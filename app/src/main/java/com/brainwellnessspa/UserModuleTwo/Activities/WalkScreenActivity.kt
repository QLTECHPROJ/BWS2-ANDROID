package com.brainwellnessspa.UserModuleTwo.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityWalkScreenBinding

class WalkScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkScreenBinding
    var USERID: String? = ""
    var CoUserID: String? = ""
    var EMAIL: String? = ""
    var NAME: String? = ""
    var ScreenView: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_screen)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        EMAIL = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        NAME = shared.getString(CONSTANTS.PREFE_ACCESS_NAME, "")

        if (intent.extras != null) {
            ScreenView = intent.getStringExtra(CONSTANTS.ScreenView)
        }

        binding.tvName.text = "Hi, $NAME"
        binding.rlWelcome.visibility = View.VISIBLE
        binding.rlStepOne.visibility = View.GONE
        binding.rlStepTwo.visibility = View.GONE
        binding.rlStepThree.visibility = View.GONE
        if (ScreenView.equals("1")) {
            binding.rlWelcome.visibility = View.VISIBLE
            binding.rlStepOne.visibility = View.GONE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.GONE
        } else if (ScreenView.equals("2")) {
            binding.rlWelcome.visibility = View.GONE
            binding.rlStepOne.visibility = View.GONE
            binding.rlStepTwo.visibility = View.VISIBLE
            binding.rlStepThree.visibility = View.GONE
        }else if (ScreenView.equals("3")) {
            binding.rlWelcome.visibility = View.GONE
            binding.rlStepOne.visibility = View.GONE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.VISIBLE
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
             val intent = Intent(this@WalkScreenActivity, BottomNavigationActivity::class.java)
             startActivity(intent)
             finish()
         }
    }

    override fun onBackPressed() {
        finish()
    }
}