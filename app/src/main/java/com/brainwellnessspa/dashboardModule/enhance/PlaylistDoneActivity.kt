package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityPlaylistDoneBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class PlaylistDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDoneBinding
    private var backClick: String? = ""
    var coUserId: String? = ""
    var userId: String? = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_done)

        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        p.putValue("coUserId", coUserId)
        addToSegment("Suggested Playlist Created", p, CONSTANTS.screen)

        binding.tvTitle.text = "You playlist is ready"
        binding.tvSubTitle.text = "We recommend that you listen to the audios while going to sleep to experience to get the maximum benefits from the program."

        binding.btnContinue.setOnClickListener {
            if (intent.extras != null) {
                backClick = intent.getStringExtra("BackClick")
            }
            if (backClick.equals("0", true)) {
                val intent = Intent(this@PlaylistDoneActivity, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "1")
                startActivity(intent)
                finish()
            } else if (backClick.equals("1", true)) {
                finish()
            }
        }
    }
}