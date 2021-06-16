package com.brainwellnessspa.dashboardModule.manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityPlaylistDoneBinding
import com.segment.analytics.Properties

class PlaylistDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDoneBinding
    var BackClick : String?=""
    var CoUserID: String? = ""
    var USERID: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_done)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        p.putValue("coUserId", CoUserID)
        BWSApplication.addToSegment("Suggested Playlist Created", p, CONSTANTS.screen)

        binding.tvTitle.text = "You playlist is ready"
        binding.tvSubTitle.text = "We recommend that you listen to the audios while going to sleep to experience to get the maximum benefits from the program."

        binding.btnContinue.setOnClickListener {
            if (intent.extras != null) {
                BackClick = intent.getStringExtra("BackClick")
            }
            if(BackClick.equals("0",true)){
                val intent = Intent(this@PlaylistDoneActivity, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst","1")
                startActivity(intent)
                finish()
            }else if(BackClick.equals("1",true)){
                finish()
            }

        }
    }
}