package com.brainwellnessspa.dashboardModule.session.notUsed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionBookletContinueBinding

class SessionBookletContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionBookletContinueBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_booklet_continue)
    }
}