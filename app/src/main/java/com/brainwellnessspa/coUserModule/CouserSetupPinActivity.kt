package com.brainwellnessspa.coUserModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityCouserSetupPinBinding

class CouserSetupPinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCouserSetupPinBinding

//    ic_add_couser_bg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_couser_setup_pin)
    }
}