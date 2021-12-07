package com.brainwellnessspa.assessmentProgressModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.models.AssesmentGetDetailsModel
import com.brainwellnessspa.databinding.ActivityAssProcessBinding
import com.brainwellnessspa.membershipModule.activities.MembershipActivity
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.coUserModule.ThankYouActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* This is to Assessment started act and ended act */
class AssProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssProcessBinding
    var assProcess: String = ""
    var navigation: String = ""
    var userId: String? = ""
    var mainAccountId: String? = ""
    var indexScore: Int = 0
    var scoreLevel: String? = ""
    var isProfileCompleted: String? = ""
    var isAssessmentCompleted: String? = ""
    var avgSleepTime: String? = ""
    private var assesmentContent: String? = ""
    lateinit var act: Activity

    /* This is the first lunched function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ass_process)
        act = this@AssProcessActivity
        /* This is the get string userid & couserid */
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        isProfileCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        assesmentContent = shared1.getString(CONSTANTS.PREFE_ACCESS_assesmentContent, "")
        avgSleepTime = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        /* This condition is get string access */
        if (intent.extras != null) {
            assProcess = intent.getStringExtra(CONSTANTS.ASSPROCESS).toString()
        }

        /* This condition is string access */
        if (assProcess == "0") {
            val p = Properties()
            addToSegment(CONSTANTS.Assessment_Start_Screen_Viewed, p, CONSTANTS.screen)
            navigation = intent.getStringExtra("Navigation").toString()
            binding.rlDoAss.visibility = View.VISIBLE
            binding.rlDoneAss.visibility = View.GONE
        } else if (assProcess == "1") {
            navigation = intent.getStringExtra("Navigation").toString()
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
                }
                indexScore in 61..69 -> {
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
                    binding.ivEighteen.visibility = View.INVISIBLE
                }
                indexScore == 70 -> {
                    binding.ivFirst.visibility = View.INVISIBLE
                    binding.ivSecond.visibility = View.INVISIBLE
                    binding.ivThird.visibility = View.INVISIBLE
                    binding.ivForth.visibility = View.INVISIBLE
                    binding.ivFifth.visibility = View.INVISIBLE
                    binding.ivThirTeen.visibility = View.INVISIBLE
                    binding.ivSixth.visibility = View.INVISIBLE
                    binding.ivSeventh.visibility = View.INVISIBLE
                    binding.ivEighteen.visibility = View.VISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
                }
                indexScore == 80 -> {
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
                    binding.ivSixteen.visibility = View.VISIBLE
                    binding.ivSeventeen.visibility = View.INVISIBLE
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
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
                    binding.ivEighteen.visibility = View.INVISIBLE
                }
            }
        }

        /* This is the do the asessement click */
        binding.btnDoAss.setOnClickListener {
            Log.e("navigation", navigation)
            val intent = Intent(this@AssProcessActivity, WellnessAssessmentActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("Navigation", navigation)
            startActivity(intent)
            finish()
        }

        /* This is the assessment done click */
        binding.btnDoneAss.setOnClickListener {
            if (mainAccountId.equals(userId)) {
                //                 TODO when add plan in user flow comment open
                if (navigation == "Home") {
                    finish()
                } else {
                    val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                    val paymentType = shared.getString(CONSTANTS.PREFE_ACCESS_paymentType, "")
                    val planId = shared.getString(CONSTANTS.PREFE_ACCESS_PlanId, "")
                    //                    if(paymentType == "0"){
                    if (planId == "") {
                        val i = Intent(this@AssProcessActivity, MembershipActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        i.putExtra("plan", "0")
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(this@AssProcessActivity, ProfileProgressActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        i.putExtra("plan", "0")
                        startActivity(i)
                        finish()
                    }
                    //                    }else {
                    //                        isEnhanceBack = "1"
                    //                        val i = Intent(this@AssProcessActivity, EnhanceActivity::class.java)
                    //                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    //                        i.putExtra("plan", "0")
                    //                        startActivity(i)
                    //                        finish()
                    //                    }

                    /*val i = Intent(this@AssProcessActivity, EnhanceDoneActivity::class.java)
                    startActivity(i)
                    finish()*/
                }
            } else {
                if (navigation == "Home") {
                    finish()
                } else {
                    val intent = Intent(applicationContext, ThankYouActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NO_HISTORY
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        finish()
    }

    private fun prepareData(userID: String?) {
        if (isNetworkConnected(act)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getAssesmentGetDetails(userID) /*Flag = 0 Staging Flag = 1 Live*/
            listCall.enqueue(object : Callback<AssesmentGetDetailsModel?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<AssesmentGetDetailsModel?>, response: Response<AssesmentGetDetailsModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel != null) {
                            when {
                                listModel.responseCode.equals(getString(R.string.ResponseCodesuccess)) -> {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                                    binding.assesmentTitle.text = listModel.responseData?.assesmentTitle
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        binding.assesmentContent.text = Html.fromHtml(listModel.responseData?.assesmentContent, Html.FROM_HTML_MODE_COMPACT);
                                    } else {
                                        binding.assesmentContent.text = Html.fromHtml(listModel.responseData?.assesmentContent);
                                    }
                                    //                                    binding.assesmentContent.text = listModel.responseData?.assesmentContent
                                    binding.tvScreenTitle.text = listModel.responseData?.mainTitle
                                    binding.tvWellnessTitle.text = listModel.responseData?.subTitle
                                    binding.tvWellnessTitle.setTextColor(Color.parseColor(listModel.responseData?.colorcode))
                                }
                                listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted)) -> {
                                    callDelete403(act, listModel.responseMessage)
                                }
                                else -> {
                                    showToast(listModel.responseMessage, act)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AssesmentGetDetailsModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), act)
        }
    }
}