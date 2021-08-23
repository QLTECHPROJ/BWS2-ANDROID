package com.brainwellnessspa.userModule.signupLogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityEmailVerifyBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class EmailVerifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerifyBinding
    var mainAccountId: String? = ""
    var userId: String? = ""
    private var emailUser: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var indexScore: String? = ""
    var avgSleepTime: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_verify)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        avgSleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val p = Properties()
        BWSApplication.addToSegment(CONSTANTS.Email_Sent_Screen_Viewed, p, CONSTANTS.screen)

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (isAssessmentCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, AssProcessActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else if (avgSleepTime.equals("", ignoreCase = true)) {
                val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("1", ignoreCase = true) && isAssessmentCompleted.equals("1", ignoreCase = true)) {
                val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
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