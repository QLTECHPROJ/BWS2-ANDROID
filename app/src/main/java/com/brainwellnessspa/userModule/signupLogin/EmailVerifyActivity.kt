package com.brainwellnessspa.userModule.signupLogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityEmailVerifyBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.utility.CONSTANTS

class EmailVerifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerifyBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var emailUser: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var indexScore: String? = ""
    var avgSleepTime: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_verify)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        avgSleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, AssProcessActivity::class.java)
                intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, WalkScreenActivity::class.java)
                intent.putExtra(CONSTANTS.ScreenView, "2")
                startActivity(intent)
                finish()
            } else if (avgSleepTime.equals("", ignoreCase = true)) {
                val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("1", ignoreCase = true) && isAssessmentCompleted.equals("1", ignoreCase = true)) {
                val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "0")
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}