package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionComparisonStatusBinding

class SessionComparisonStatusActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionComparisonStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_comparison_status)
    }
}