package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionAssessmentBinding

class SessionAssessmentActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionAssessmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_assessment)

        /*TODO note radio button use this drawable radio_btn_session_background*/
    }
}