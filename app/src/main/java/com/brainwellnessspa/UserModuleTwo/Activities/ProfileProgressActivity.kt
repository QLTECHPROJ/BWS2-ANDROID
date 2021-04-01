package com.brainwellnessspa.UserModuleTwo.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityProfileProgressBinding

class ProfileProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_progress)

        binding.llFirst.visibility = View.VISIBLE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE

        binding.btnFirstDone.setOnClickListener {
            binding.llIndicate.progress = 4
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.VISIBLE
            binding.llThird.visibility = View.GONE
            binding.llForth.visibility = View.GONE
            binding.llFifth.visibility = View.GONE
        }

        binding.btnSecondDone.setOnClickListener {
            binding.llIndicate.progress = 8
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.VISIBLE
            binding.llForth.visibility = View.GONE
            binding.llFifth.visibility = View.GONE
        }

        binding.btnThirdDone.setOnClickListener {
            binding.llIndicate.progress = 8
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.GONE
            binding.llForth.visibility = View.VISIBLE
            binding.llFifth.visibility = View.GONE
        }

        binding.btnForthDone.setOnClickListener {
            binding.llIndicate.progress = 14
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.GONE
            binding.llForth.visibility = View.GONE
            binding.llFifth.visibility = View.VISIBLE
        }

        binding.btnFifthDone.setOnClickListener {
            binding.llIndicate.progress = 18
            binding.llFirst.visibility = View.GONE
            binding.llSecond.visibility = View.GONE
            binding.llThird.visibility = View.GONE
            binding.llForth.visibility = View.GONE
            binding.llFifth.visibility = View.VISIBLE
            val i = Intent(this@ProfileProgressActivity, AssProcessActivity::class.java)
            i.putExtra(CONSTANTS.ASSPROCESS,"0")
            startActivity(i)
            finish()
        }
    }
}