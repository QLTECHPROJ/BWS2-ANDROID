package com.brainwellnessspa.DassAssSliderTwo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityDassAssSliderBinding

class DassAssSliderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDassAssSliderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dass_ass_slider)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dass_ass_slider)


    }
}