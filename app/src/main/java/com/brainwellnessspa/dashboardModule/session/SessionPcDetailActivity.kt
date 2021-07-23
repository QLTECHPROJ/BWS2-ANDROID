package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionPcDetailBinding

class SessionPcDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionPcDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_pc_detail)
    }
}