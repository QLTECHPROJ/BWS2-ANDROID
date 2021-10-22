package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionDetailContinueBinding

class SimilarContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionDetailContinueBinding
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_similar_continue)
        activity = this@SimilarContinueActivity
    }
}