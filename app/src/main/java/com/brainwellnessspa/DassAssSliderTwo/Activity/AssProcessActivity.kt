package com.brainwellnessspa.DassAssSliderTwo.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAssProcessBinding

class AssProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssProcessBinding
    var ASSPROCESS: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ass_process)

        if (intent.extras != null) {
            ASSPROCESS = intent.getStringExtra(CONSTANTS.ASSPROCESS).toString()
        }

        if (ASSPROCESS.equals("0", ignoreCase = true)) {
            binding.rlDoAss.visibility = View.VISIBLE
            binding.rlDoneAss.visibility = View.GONE
        } else if (ASSPROCESS.equals("1", ignoreCase = true)) {
            binding.rlDoAss.visibility = View.GONE
            binding.rlDoneAss.visibility = View.VISIBLE
        }

        binding.btnDoAss.setOnClickListener {
            val i = Intent(this@AssProcessActivity, DassAssSliderActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnDoneAss.setOnClickListener {
            val i = Intent(this@AssProcessActivity, BottomNavigationActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}