package com.brainwellnessspa.userModule.coUserModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityThankYouBinding
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.utility.CONSTANTS

class ThankYouActivity : AppCompatActivity() {
    lateinit var binding: ActivityThankYouBinding
    var userId: String? = null
    private var mainAccountID: String? = null
    var userName: String? = null
    var isProfileCompleted: String? = null
    var isAssessmentCompleted: String? = null
    var avgSleepTime: String? = null
    var coUserCount: String? = null
    var planContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you)

        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        isProfileCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        avgSleepTime = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        coUserCount = shared1.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "")
        planContent = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanContent, "")

        binding.tvName.text = userName
        binding.tvSubTitle.text = planContent
        binding.btnExplore.setOnClickListener {
            if (isProfileCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
                startActivity(intent)
                finish()
            } else if (avgSleepTime.equals("", ignoreCase = true)) {
                val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("1", ignoreCase = true) && isAssessmentCompleted.equals("1", ignoreCase = true)) {
                if (coUserCount.toString() > "1") {
                    val intent = Intent(applicationContext, UserListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                    intent.putExtra("IsFirst", "1")
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}