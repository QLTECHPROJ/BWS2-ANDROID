package com.brainwellnessspa.membershipModule.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityEnhanceDoneBinding

class EnhanceDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnhanceDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enhance_done)
        val measureRatio = BWSApplication.measureRatio(this@EnhanceDoneActivity, 0f, 5f, 6f, 0.4f, 0f)
        binding.ivLogo.getLayoutParams().height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivLogo.getLayoutParams().width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivLogo.setScaleType(ImageView.ScaleType.FIT_XY)
        binding.ivLogo.setImageResource(R.drawable.ic_thank_you_bg_two)
    }
}