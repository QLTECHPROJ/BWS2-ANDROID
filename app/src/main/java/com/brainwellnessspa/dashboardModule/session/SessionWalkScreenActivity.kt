package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionWalkScreenBinding

class SessionWalkScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionWalkScreenBinding
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_walk_screen)
        activity = this@SessionWalkScreenActivity

        binding.tvSecFourReadMore.setOnClickListener {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dass_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
//            tvDesc.setText(listModel.responseData.get(0).audioDescription)

            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }

            tvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }
    }
}