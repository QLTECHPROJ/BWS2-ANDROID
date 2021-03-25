package com.brainwellnessspa.ManageModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityManageDashboardBinding

class ManageDashboardActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_dashboard)

    }
}