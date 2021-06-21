package com.brainwellnessspa.membershipModule.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEnhanceDashboardBinding

class EnhanceDashboardActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_dashboard)

    }
}