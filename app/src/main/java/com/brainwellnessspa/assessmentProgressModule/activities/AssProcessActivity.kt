package com.brainwellnessspa.assessmentProgressModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.models.AssesmentGetDetailsModel
import com.brainwellnessspa.databinding.ActivityAssProcessBinding
import com.brainwellnessspa.membershipModule.activities.EnhanceDoneActivity
import com.brainwellnessspa.userModule.coUserModule.ThankYouActivity
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* This is to Assessment started activity and ended activity */
class AssProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssProcessBinding
    var assProcess: String = ""
    var userId: String? = ""
    var mainAccountId: String? = ""
    var indexScore: Int = 0
    var scoreLevel: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var avgSleepTime: String? = ""
    private var assesmentContent: String? = ""
    lateinit var activity: Activity

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ass_process)
        activity = this@AssProcessActivity
        /* This is the get string userid & couserid */
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        isProfileCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        assesmentContent = shared1.getString(CONSTANTS.PREFE_ACCESS_assesmentContent, "")
        val sharpened = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = sharpened.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        /* This condition is get string access */
        if (intent.extras != null) {
            assProcess = intent.getStringExtra(CONSTANTS.ASSPROCESS).toString()
        }

        /* This condition is string access */
        if (assProcess.equals("0", ignoreCase = true)) {
            val p = Properties()
            addToSegment(CONSTANTS.Assessment_Start_Screen_Viewed, p, CONSTANTS.screen)
            binding.rlDoAss.visibility = View.VISIBLE
            binding.rlDoneAss.visibility = View.GONE
        } else if (assProcess.equals("1", ignoreCase = true)) {
            indexScore = Integer.parseInt(intent.getStringExtra(CONSTANTS.IndexScore).toString())
            scoreLevel = intent.getStringExtra(CONSTANTS.ScoreLevel)
            binding.rlDoAss.visibility = View.GONE
            binding.rlDoneAss.visibility = View.VISIBLE
            binding.tvIndexScore.text = indexScore.toString()
            binding.tvTag.text = scoreLevel.toString()

            prepareData(userId)
            /* This is segment tag */
            val p = Properties()
            p.putValue("WellnessScore", indexScore)
            p.putValue("scoreLevel", scoreLevel)
            addToSegment(CONSTANTS.Wellness_Score_Screen_Viewed, p, CONSTANTS.screen)

            /* This condition is indexscore result */
            when {
                indexScore == 0 -> {
                    binding.ivFirst.visibility = View.VISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore <= 10 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.VISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 11..20 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.VISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore == 20 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.VISIBLE
                }
                indexScore in 21..30 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.VISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 31..35 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.VISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 35..39 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.VISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore == 40 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.VISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 41..50 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.VISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 51..59 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.VISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore == 60 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.VISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 61..70 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.VISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 71..79 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.VISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore == 80 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.VISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.VISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 81..90 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.VISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore in 91..99 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.VISIBLE
                    binding.ivTwelveth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
                indexScore == 100 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighth.visibility = View.INVISIBLE
                    binding.ivNineth.visibility = View.INVISIBLE
                    binding.ivTenth.visibility = View.INVISIBLE
                    binding.ivEleventh.visibility = View.INVISIBLE
                    binding.ivTwelveth.visibility = View.VISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivForteen.visibility = View.INVISIBLE
                    binding.ivFifteen.visibility = View.INVISIBLE
                    binding.ivSixteen.visibility = View.INVISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                }
            }
        }

        /* This is the do the asessement click */
        binding.btnDoAss.setOnClickListener {
            val intent = Intent(this@AssProcessActivity, DassAssSliderActivity::class.java)
            startActivity(intent)
            finish()
        }

        /* This is the assessment done click */
        binding.btnDoneAss.setOnClickListener {
            if (mainAccountId.equals(userId, ignoreCase = true)) {
                /* TODO when add plan in user flow comment open */
                /*val i = Intent(this@AssProcessActivity, EnhanceActivity::class.java)
                startActivity(i)
                finish()*/
                val i = Intent(this@AssProcessActivity, EnhanceDoneActivity::class.java)
                startActivity(i)
                finish()
            } else {
                val intent = Intent(applicationContext, ThankYouActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        finish()
    }

    private fun prepareData(userID: String?) {
        if (isNetworkConnected(activity)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getAssesmentGetDetails(userID) /*Flag = 0 Staging Flag = 1 Live*/
            listCall.enqueue(object : Callback<AssesmentGetDetailsModel?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<AssesmentGetDetailsModel?>, response: Response<AssesmentGetDetailsModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel != null) {
                            when {
                                listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    binding.assesmentTitle.text = listModel.responseData?.assesmentTitle
                                    binding.assesmentContent.text = listModel.responseData?.assesmentContent
                                    binding.tvScreenTitle.text = listModel.responseData?.mainTitle
                                    binding.tvWellnessTitle.text = listModel.responseData?.subTitle
                                    binding.tvWellnessTitle.setTextColor(Color.parseColor(listModel.responseData?.colorcode))
                                }
                                listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                    deleteCall(activity)
                                    showToast(listModel.responseMessage, activity)
                                    val i = Intent(activity, SignInActivity::class.java)
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    startActivity(i)
                                    finish()
                                }
                                else -> {
                                    showToast(listModel.responseMessage, activity)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AssesmentGetDetailsModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

}