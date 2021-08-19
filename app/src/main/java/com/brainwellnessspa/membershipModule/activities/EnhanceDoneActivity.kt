package com.brainwellnessspa.membershipModule.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityEnhanceDoneBinding
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.coUserModule.AddCouserActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class EnhanceDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceDoneBinding
    var p: Properties? = null
    var userId: String? = ""
    var coUserId: String? = ""
    private var emailUser: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    private var coUserCount: String? = ""
    var indexScore: String? = ""
    var avgSleepTime: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_done)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        emailUser = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        isProfileCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        coUserCount = shared.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val measureRatio = measureRatio(applicationContext, 0f, 5f, 6f, 0.4f, 0f)
        binding.ivLogo.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivLogo.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivLogo.scaleType = ImageView.ScaleType.FIT_XY
        binding.ivLogo.setImageResource(R.drawable.ic_thank_you_bg_two)
        p = Properties()
        addToSegment("Thank You Screen Viewed", p, CONSTANTS.screen)
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
                addToSegment("Explore App Clicked", p, CONSTANTS.track)
                val intent = Intent(applicationContext, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "0")
                startActivity(intent)
                finish()
            }
        }

        binding.btnAddCouser.setOnClickListener {
            IsFirstClick = "1"
            val i = Intent(applicationContext, AddCouserActivity::class.java)
            i.putExtra("IsFirstClick", "1")
            startActivity(i)
            finish()
        }
    }
}