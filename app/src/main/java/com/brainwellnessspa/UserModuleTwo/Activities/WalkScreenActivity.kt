package com.brainwellnessspa.UserModuleTwo.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.DassAssSliderTwo.Activity.DassAssSliderActivity
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


        binding.btnContinue.setOnClickListener {
            if (ScreenView.equals("ProfileView")) {
                val intent = Intent(this@WalkScreenActivity, ProfileProgressActivity::class.java)
                startActivity(intent)
            } else if (ScreenView.equals("DassView")) {
                val intent = Intent(this@WalkScreenActivity, DassAssSliderActivity::class.java)
                startActivity(intent)
            }
        }

        binding.rlStepOne.setOnClickListener {

        }

        binding.rlStepTwo.setOnClickListener {

        }

        /* binding.rlStepThree.setOnClickListener {
             val i = Intent(this@WalkScreenActivity, GetStartedActivity::class.java)
             i.putExtra(CONSTANTS.ScreenVisible, "2")
             startActivity(i)
         }*/
    }
}