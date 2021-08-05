package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
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
    lateinit var activity: Activity
    var userId: String? = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_done)
        activity = this@PlaylistDoneActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        addToSegment(" Playlist  Ready Screen Viewed", p, CONSTANTS.screen)

        binding.tvTitle.text = getString(R.string.your_playlist_is_ready)
        binding.tvSubTitle.text = getString(R.string.playlist_ready_subtitle)

        binding.btnContinue.setOnClickListener {
            if (intent.extras != null) {
                backClick = intent.getStringExtra("BackClick")
            }
            if (backClick.equals("0", true)) {
                val intent = Intent(activity, BottomNavigationActivity::class.java)
                intent.putExtra("IsFirst", "1")
                startActivity(intent)
                finish()
            } else if (backClick.equals("1", true)) {
                finish()
            }
        }
    }
}