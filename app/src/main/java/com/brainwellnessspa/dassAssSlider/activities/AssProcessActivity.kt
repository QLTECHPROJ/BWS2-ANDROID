package com.brainwellnessspa.dassAssSlider.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.userModuleTwo.activities.WalkScreenActivity
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAssProcessBinding
import com.segment.analytics.Properties

class AssProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssProcessBinding
    var assProcess: String = ""
    var coUserId: String? = null
    var userId: String? = null
    var indexScore: Int = 0
    var scoreLevel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ass_process)
        val shared1 =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        if (intent.extras != null) {
            assProcess = intent.getStringExtra(CONSTANTS.ASSPROCESS).toString()
        }

        if (assProcess.equals("0", ignoreCase = true)) {
            binding.rlDoAss.visibility = View.VISIBLE
            binding.rlDoneAss.visibility = View.GONE
        } else if (assProcess.equals("1", ignoreCase = true)) {
            indexScore = Integer.parseInt(intent.getStringExtra(CONSTANTS.IndexScore).toString())
            scoreLevel = intent.getStringExtra(CONSTANTS.ScoreLevel)
            binding.rlDoAss.visibility = View.GONE
            binding.rlDoneAss.visibility = View.VISIBLE
            binding.tvIndexScore.text = indexScore.toString()
            binding.tvTag.text = scoreLevel.toString()

            val p = Properties()
            p.putValue("userId", userId)
            p.putValue("coUserId", coUserId)
            p.putValue("indexScore", indexScore)
            p.putValue("scoreLevel", scoreLevel)
            BWSApplication.addToSegment("Index Score Screen Viewed", p, CONSTANTS.screen)

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

        binding.btnDoAss.setOnClickListener {
            val intent = Intent(this@AssProcessActivity, WalkScreenActivity::class.java)
            intent.putExtra(CONSTANTS.ScreenView, "2")
            startActivity(intent)
            finish()
        }

        binding.btnDoneAss.setOnClickListener {
            val i = Intent(this@AssProcessActivity, WalkScreenActivity::class.java)
            i.putExtra(CONSTANTS.ScreenView, "3")
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}