package com.brainwellnessspa.userModule.signupLogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.DassAssSliderActivity
import com.brainwellnessspa.assessmentProgressModule.activities.DoingGoodActivity
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.SessionStepOneModel
import com.brainwellnessspa.dashboardModule.session.SessionAudiosActivity
import com.brainwellnessspa.databinding.ActivityWalkScreenBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.coUserModule.UserDetailActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import java.util.ArrayList

class WalkScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkScreenBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var email: String? = ""
    var name: String? = ""
    var screenView: String? = ""
    var sessionId : String? = ""
    var desc : String? = ""
    var stepId : String? = ""
    var json : String? = ""
    val gson = Gson()
    var listModel = SessionStepOneModel.ResponseData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_screen)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        email = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        name = shared.getString(CONSTANTS.PREFE_ACCESS_NAME, "")

        if (intent.extras != null) {
            screenView = intent.getStringExtra(CONSTANTS.ScreenView)
        }

        binding.tvName.text = "Hi, $name"
        binding.rlWelcome.visibility = View.VISIBLE
        binding.rlStepOne.visibility = View.GONE
        binding.rlStepTwo.visibility = View.GONE
        binding.rlStepThree.visibility = View.GONE

        when {
            screenView.equals("1", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.VISIBLE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.GONE
                binding.rlStepSessionDesc.visibility = View.GONE
            }
            screenView.equals("2", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.VISIBLE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.GONE
                binding.rlStepSessionDesc.visibility = View.GONE
            }
            screenView.equals("3", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.VISIBLE
                binding.rlStepFour.visibility = View.GONE
                binding.rlStepSessionDesc.visibility = View.GONE
            }
            screenView.equals("4", ignoreCase = true) -> {
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.VISIBLE
                binding.rlStepSessionDesc.visibility = View.GONE
            }
            screenView.equals("5", ignoreCase = true) -> {
                sessionId = intent.getStringExtra("sessionId")
                stepId = intent.getStringExtra("stepId")
                desc = intent.getStringExtra("Desc")
                json = intent.getStringExtra("audioData")
                val type = object : TypeToken<SessionStepOneModel.ResponseData?>() {}.type
                listModel = gson.fromJson(json, type)
                binding.rlWelcome.visibility = View.GONE
                binding.rlStepOne.visibility = View.GONE
                binding.rlStepTwo.visibility = View.GONE
                binding.rlStepThree.visibility = View.GONE
                binding.rlStepFour.visibility = View.GONE
                binding.rlStepSessionDesc.visibility = View.VISIBLE
                binding.tvTitleDesc.text = desc
            }
        }

        binding.btnContinue.setOnClickListener {
            binding.rlWelcome.visibility = View.GONE
            binding.rlStepOne.visibility = View.VISIBLE
            binding.rlStepTwo.visibility = View.GONE
            binding.rlStepThree.visibility = View.GONE
        }

        binding.llBack.setOnClickListener {
            callback()
        }
        binding.rlStepOne.setOnClickListener {
            val intent = Intent(applicationContext, DassAssSliderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        }

        binding.rlStepTwo.setOnClickListener {
            val p = Properties()
            addToSegment("Profile Step Start Screen Viewed", p, CONSTANTS.screen)
            val intent = Intent(applicationContext, ProfileProgressActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        }

        binding.rlStepThree.setOnClickListener {
            val intent = Intent(applicationContext, DoingGoodActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        }

        binding.rlStepFour.setOnClickListener {
            val intent = Intent(applicationContext, UserDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
        }

        binding.btnStart.setOnClickListener {

            GlobalInitExoPlayer.callNewPlayerRelease()
            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            val editor = shared.edit()
            val gson = Gson()
            val downloadAudioDetails = ArrayList<SessionStepOneModel.ResponseData.StepAudio>()
//            for (i in listModel.stepAudio) {
                val mainPlayModel = SessionStepOneModel.ResponseData.StepAudio()
                mainPlayModel.id = listModel.stepAudio!!.id!!
                mainPlayModel.name = listModel.stepAudio!!.name!!
                mainPlayModel.audioFile = listModel.stepAudio!!.audioFile!!
                mainPlayModel.audioDirection = listModel.stepAudio!!.audioDirection!!
                mainPlayModel.audiomastercat = listModel.stepAudio!!.audiomastercat!!
                mainPlayModel.audioSubCategory = listModel.stepAudio!!.audioSubCategory!!
                mainPlayModel.imageFile = listModel.stepAudio!!.imageFile!!
                mainPlayModel.audioDuration = listModel.stepAudio!!.audioDuration!!
                downloadAudioDetails.add(mainPlayModel)
//            }
            val json = gson.toJson(downloadAudioDetails)
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId,sessionId)
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, stepId)
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Session")
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SessionAudio")
            editor.apply()
            val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            if (audioPlayerFlag.equals("SessionAudio")) {
                if(player != null){
                    if (!player.playWhenReady) {
                        player.playWhenReady = true
                    }
                }else {
                    audioClick = true
                }
            }else {
                audioClick = true
            }
            val intent = Intent(applicationContext, MyPlayerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
    }

    private fun callback() {
        if( screenView.equals("5", ignoreCase = true)){
            val i = Intent(applicationContext, SessionAudiosActivity::class.java)
            i.putExtra("SessionId", sessionId)
            i.putExtra("StepId", stepId)
            startActivity(i)
            finish()
        }else {
            finish()
        }
    }

    override fun onBackPressed() {
        callback()
    }
}