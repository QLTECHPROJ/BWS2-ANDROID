package com.brainwellnessspa.userModule.accountInfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityAccountFaqDetailBinding

class AccountFaqDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountFaqDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_faq_detail)
    }
}