package com.brainwellnessspa.PlaylistTwoModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityPreparePlaylistBinding

class PreparePlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityPreparePlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prepare_playlist)
    }
}