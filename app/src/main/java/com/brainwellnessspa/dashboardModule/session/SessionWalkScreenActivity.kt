package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionWalkScreenBinding

class SessionWalkScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionWalkScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_walk_screen)
    }
}