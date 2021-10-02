package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.databinding.ActivityBrainStatusBinding
import com.brainwellnessspa.databinding.ActivityEmpowerManageBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class EmpowerManageActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener  {
    lateinit var binding: ActivityEmpowerManageBinding
    lateinit var activity: Activity
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empower_manage)
        activity = this@EmpowerManageActivity
        ctx = this@EmpowerManageActivity

        binding.youtubeView.initialize(API_KEY, this)

    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(CancelMembershipActivity.VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            BWSApplication.showToast(errorMessage, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            youTubePlayerProvider.initialize(API_KEY, this)
        }
    }

    private val youTubePlayerProvider: YouTubePlayer.Provider
        private get() = binding.youtubeView

    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

}