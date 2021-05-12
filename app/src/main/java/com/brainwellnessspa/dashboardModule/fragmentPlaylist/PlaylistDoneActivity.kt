package com.brainwellnessspa.dashboardModule.fragmentPlaylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityPlaylistDoneBinding

class PlaylistDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_done)

        binding.tvTitle.text = "You playlist is ready"
        binding.tvSubTitle.text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut"

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this@PlaylistDoneActivity, BottomNavigationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}