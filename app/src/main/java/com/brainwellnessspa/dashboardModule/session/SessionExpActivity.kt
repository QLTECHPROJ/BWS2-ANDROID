package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionExpBinding

class SessionExpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionExpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_exp)
    }
}