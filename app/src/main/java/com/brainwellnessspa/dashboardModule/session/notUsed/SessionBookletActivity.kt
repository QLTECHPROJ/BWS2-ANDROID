package com.brainwellnessspa.dashboardModule.session.notUsed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionBookletBinding

class SessionBookletActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionBookletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_booklet)
    }
}