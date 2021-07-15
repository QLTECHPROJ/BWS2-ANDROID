package com.brainwellnessspa.membershipModule.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityEnhanceDoneBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import com.brainwellnessspa.userModule.coUserModule.AddCouserActivity
import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity

class EnhanceDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceDoneBinding
    var userId: String? = ""
    var coUserId: String? = ""
    private var emailUser: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var coUserCount: String? = ""
    var indexScore: String? = ""
    var avgSleepTime: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_done)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        coUserCount = shared.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val measureRatio = BWSApplication.measureRatio(applicationContext, 0f, 5f, 6f, 0.4f, 0f)
        binding.ivLogo.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivLogo.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivLogo.scaleType = ImageView.ScaleType.FIT_XY
        binding.ivLogo.setImageResource(R.drawable.ic_thank_you_bg_two)
        val p = Properties()
        BWSApplication.addToSegment("Thank You Screen Viewed", p, CONSTANTS.screen)
        binding.btnExplore.setOnClickListener {
            if (isProfileCompleted.equals("0", ignoreCase = true)) {
                val intent = Intent(applicationContext, WalkScreenActivity::class.java)
                intent.putExtra(CONSTANTS.ScreenView, "2")
                startActivity(intent)
                finish()
            } else if (avgSleepTime.equals("", ignoreCase = true)) {
                val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                startActivity(intent)
                finish()
            } else if (isProfileCompleted.equals("1", ignoreCase = true) && isAssessmentCompleted.equals("1", ignoreCase = true)) {
                val p = Properties()
                BWSApplication.addToSegment("Explore App Clicked", p, CONSTANTS.track)
                val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "0")
                startActivity(intent)
                finish()
            }
        }

        binding.btnAddCouser.setOnClickListener {
            val i = Intent(applicationContext, AddCouserActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}