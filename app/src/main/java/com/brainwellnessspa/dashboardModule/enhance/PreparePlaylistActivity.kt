package com.brainwellnessspa.dashboardModule.enhance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityPreparePlaylistBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class PreparePlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityPreparePlaylistBinding
    var coUserId: String? = ""
    var userId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prepare_playlist)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        addToSegment("Preparing Playlist Screen Viewed", p, CONSTANTS.screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(applicationContext, PlaylistDoneActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            i.putExtra("BackClick", intent.getStringExtra("BackClick"))
            startActivity(i)
            finish()
        }, (2 * 1000).toLong())
    }
}