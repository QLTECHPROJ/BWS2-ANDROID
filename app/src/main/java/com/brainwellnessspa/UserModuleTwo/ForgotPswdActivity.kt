package com.brainwellnessspa.UserModuleTwo

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityForgotPswdBinding

class ForgotPswdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPswdBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pswd)

        binding.llBack.setOnClickListener {
            finish()
        }
        binding.btnResetPswd.setOnClickListener {
            if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.flEmail.error = "Email address is required"
            } else {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.alert_popup_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                val tvGoBack: TextView = dialog.findViewById(R.id.tvGoBack)
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                    }
                    false
                }

                tvGoBack.setOnClickListener {
                    dialog.dismiss()
                    finish()
                }
                dialog.show()
                dialog.setCancelable(false)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}