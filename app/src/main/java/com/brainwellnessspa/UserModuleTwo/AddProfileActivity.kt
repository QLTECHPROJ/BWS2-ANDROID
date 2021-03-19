package com.brainwellnessspa.UserModuleTwo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityAddProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class AddProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProfileBinding

/*
    var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val User: String = binding.etUser.getText().toString().trim()
            val MobileNumber: String = binding.etMobileNumber.getText().toString().trim()
            val Email: String = binding.etEmail.getText().toString().trim()
             if (User.equals(UserName, ignoreCase = true) && MobileNumber.equals(UserMobileNumber, ignoreCase = true) && Email.equals(UserEmail, ignoreCase = true)) {
                 binding.btnSendPin.setEnabled(false)
                 binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                 binding.btnSendPin.setBackgroundResource(R.drawable.gray_round_cornor)
             } else if (!User.equals(UserName, ignoreCase = true)) {
                 binding.btnSendPin.setEnabled(true)
                 binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                 binding.btnSendPin.setBackgroundResource(R.drawable.extra_round_cornor)
             } else if (!Calendar.equals(UserCalendar, ignoreCase = true)) {
                 binding.btnSendPin.setEnabled(true)
                 binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                 binding.btnSendPin.setBackgroundResource(R.drawable.extra_round_cornor)
             } else if (!MobileNumber.equals(UserMobileNumber, ignoreCase = true)) {
                 binding.btnSendPin.setEnabled(true)
                 binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                 binding.btnSendPin.setBackgroundResource(R.drawable.extra_round_cornor)
             } else if (!Email.equals(UserEmail, ignoreCase = true)) {
                 binding.btnSendPin.setEnabled(true)
                 binding.btnSendPin.setTextColor(resources.getColor(R.color.white))
                 binding.btnSendPin.setBackgroundResource(R.drawable.extra_round_cornor)
             } else {
                 binding.btnSave.setEnabled(true)
                 binding.btnSave.setTextColor(resources.getColor(R.color.white))
                 binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor)
             }
        }

        override fun afterTextChanged(s: Editable) {}
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_profile)

        binding.llBack.setOnClickListener {
            val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
            i.putExtra(CONSTANTS.PopUp, "0")
            startActivity(i)
            finish()
        }
        val measureRatio = BWSApplication.measureRatio(this, 0f, 1f, 1f, 0.32f, 0f)
        binding.civProfile.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civProfile.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.rlImageUpload.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.civLetter.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.civLetter.scaleType = ImageView.ScaleType.FIT_XY
        binding.civProfile.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(applicationContext).load(R.drawable.ic_default_profile_img)
                .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                .into(binding.civLetter)
//        binding.etUser.addTextChangedListener(userTextWatcher)
//        binding.etMobileNumber.addTextChangedListener(userTextWatcher)
//        binding.etEmail.addTextChangedListener(userTextWatcher)

//        TODO MANSI Gentle reminder Send New PIN Btn name changes when user send new pin
        binding.btnSendPin.setOnClickListener {
            if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = "Name is required"
                binding.flMobileNumber.error = ""
                binding.flEmail.error = ""
            } else if (binding.etMobileNumber.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = "Number is required"
                binding.flEmail.error = ""
            } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flMobileNumber.error = ""
                binding.flEmail.error = "Email address is required"
            } else {
                val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
                i.putExtra(CONSTANTS.PopUp, "1")
                startActivity(i)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this@AddProfileActivity, UserListActivity::class.java)
        i.putExtra(CONSTANTS.PopUp, "0")
        startActivity(i)
        finish()
        super.onBackPressed()
    }
}