package com.brainwellnessspa.coUserModule

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityAddCouserBinding
import com.brainwellnessspa.userModule.activities.AddProfileActivity
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.userModule.signupLogin.SignUpActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class AddCouserActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCouserBinding
    lateinit var activity: Activity
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_couser)

        activity = this@AddCouserActivity
        ctx = this@AddCouserActivity

        val p = Properties()
        BWSApplication.addToSegment("Launch Screen Viewed", p, CONSTANTS.screen)

        binding.ivInfo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(660, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvAction = dialog.findViewById<TextView>(R.id.tvAction)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvTitle.text = "With Same Mobile Number"
            tvDesc.text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
            tvAction.text = "Ok"
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            dialog.setCancelable(true)
        }
        binding.btnSameMobileNo.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this)) {
                val i = Intent(ctx, AddProfileActivity::class.java)
                startActivity(i)
                finish()
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }

        binding.btnDiffMobileNo.setOnClickListener {
            if (BWSApplication.isNetworkConnected(this)) {
                val i = Intent(ctx, CouserSetupPinActivity::class.java)
                startActivity(i)
                finish()
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), this)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}