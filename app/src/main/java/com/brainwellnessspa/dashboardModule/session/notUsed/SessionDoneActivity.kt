package com.brainwellnessspa.dashboardModule.session.notUsed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionDoneBinding

class SessionDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_done)
    }
}