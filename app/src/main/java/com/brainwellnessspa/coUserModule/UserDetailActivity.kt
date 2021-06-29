package com.brainwellnessspa.coUserModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityUserDetailBinding
import com.brainwellnessspa.userModule.activities.UserListActivity

class UserDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnProceed.setOnClickListener {
            val intent = Intent(applicationContext, UserListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}