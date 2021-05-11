package com.brainwellnessspa.DashboardTwoModule.fragmentPlaylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityPreparePlaylistBinding

class PreparePlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityPreparePlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prepare_playlist)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@PreparePlaylistActivity, PlaylistDoneActivity::class.java)
            startActivity(intent)
            finish()
        }, (2*1000).toLong())
    }
}