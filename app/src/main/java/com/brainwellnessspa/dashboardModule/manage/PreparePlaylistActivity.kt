package com.brainwellnessspa.dashboardModule.manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityPreparePlaylistBinding
import com.segment.analytics.Properties

class PreparePlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityPreparePlaylistBinding
    var CoUserID: String? = ""
    var USERID: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prepare_playlist)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")

        val p = Properties()
        p.putValue("coUserId", CoUserID)
        BWSApplication.addToSegment("Preparing Playlist Screen Viewed", p, CONSTANTS.screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this@PreparePlaylistActivity, PlaylistDoneActivity::class.java)
            i.putExtra("BackClick", intent.getStringExtra("BackClick"))
            startActivity(i)
            finish()
        }, (2 * 1000).toLong())
    }
}