package com.brainwellnessspa.DassAssSliderTwo.Activity

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
    var ASSPROCESS: String = ""
    var CoUSERID: String? = null
    var USERID: String? = null
    var IndexScore: Int = 0
    var ScoreLevel: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ass_process)
        val shared1 =
            getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")

        if (intent.extras != null) {
            ASSPROCESS = intent.getStringExtra(CONSTANTS.ASSPROCESS).toString()
        }

        if (ASSPROCESS.equals("0", ignoreCase = true)) {
            binding.rlDoAss.visibility = View.VISIBLE
            binding.rlDoneAss.visibility = View.GONE
        } else if (ASSPROCESS.equals("1", ignoreCase = true)) {
            IndexScore = Integer.parseInt(intent.getStringExtra(CONSTANTS.IndexScore).toString())
            ScoreLevel = intent.getStringExtra(CONSTANTS.ScoreLevel)
            binding.rlDoAss.visibility = View.GONE
            binding.rlDoneAss.visibility = View.VISIBLE
            binding.tvIndexScore.text = IndexScore.toString()

            val p = Properties()
            p.putValue("userId", USERID)
            p.putValue("coUserId", CoUSERID)
            p.putValue("indexScore", IndexScore)
            p.putValue("scoreLevel", ScoreLevel)
            BWSApplication.addToSegment("Index Score Screen Viewed", p, CONSTANTS.screen)

            if (IndexScore == 0) {
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
            } else if (IndexScore <= 10) {
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
            } else if (IndexScore in 11..20) {
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
            } else if (IndexScore in 21..30) {
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
            } else if (IndexScore in 31..40) {
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
            } else if (IndexScore in 41..50) {
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
            } else if (IndexScore in 51..60) {
                binding.ivFirst.visibility = View.INVISIBLE
                binding.ivSecond.visibility = View.INVISIBLE
                binding.ivThird.visibility = View.INVISIBLE
                binding.ivForth.visibility = View.INVISIBLE
                binding.ivFifth.visibility = View.INVISIBLE
                binding.ivSixth.visibility = View.INVISIBLE
                binding.ivSeventh.visibility = View.VISIBLE
                binding.ivEighth.visibility = View.INVISIBLE
                binding.ivNineth.visibility = View.INVISIBLE
                binding.ivTenth.visibility = View.INVISIBLE
                binding.ivEleventh.visibility = View.INVISIBLE
                binding.ivTwelveth.visibility = View.INVISIBLE
            } else if (IndexScore in 61..70) {
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
            } else if (IndexScore in 71..80) {
                binding.ivFirst.visibility = View.INVISIBLE
                binding.ivSecond.visibility = View.INVISIBLE
                binding.ivThird.visibility = View.INVISIBLE
                binding.ivForth.visibility = View.INVISIBLE
                binding.ivFifth.visibility = View.INVISIBLE
                binding.ivSixth.visibility = View.INVISIBLE
                binding.ivSeventh.visibility = View.INVISIBLE
                binding.ivEighth.visibility = View.INVISIBLE
                binding.ivNineth.visibility = View.VISIBLE
                binding.ivTenth.visibility = View.INVISIBLE
                binding.ivEleventh.visibility = View.INVISIBLE
                binding.ivTwelveth.visibility = View.INVISIBLE
            } else if (IndexScore in 81..90) {
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
            } else if (IndexScore in 91..99) {/*remain*/
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
            } else if (IndexScore == 100) {
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
            }

        }

        binding.btnDoAss.setOnClickListener {
            val i = Intent(this@AssProcessActivity, DassAssSliderActivity::class.java)
            startActivity(i)
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