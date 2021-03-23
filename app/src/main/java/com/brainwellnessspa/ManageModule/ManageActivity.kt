package com.brainwellnessspa.ManageModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.LoginModule.Activities.LoginActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityManageBinding

class ManageActivity : AppCompatActivity() {
    lateinit var binding: ActivityManageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage)

        binding.llBack.setOnClickListener { view ->
            finish()
        }
    }
}