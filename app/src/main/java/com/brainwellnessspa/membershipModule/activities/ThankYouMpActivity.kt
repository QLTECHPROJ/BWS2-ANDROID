package com.brainwellnessspa.membershipModule.activities

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityThankYouMembershipBinding
import com.brainwellnessspa.invoiceModule.activities.InvoiceActivity
import com.brainwellnessspa.manageModule.SleepTimeActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class ThankYouMpActivity : AppCompatActivity() {
    lateinit var binding: ActivityThankYouMembershipBinding
    private var mLastClickTime: Long = 0
    var userId: String? = null
    var coUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you_membership)
        val measureRatio =
            BWSApplication.measureRatio(this@ThankYouMpActivity, 0f, 5f, 6f, 0.4f, 0f)
        binding.ivLogo.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivLogo.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivLogo.scaleType = ImageView.ScaleType.FIT_XY
        binding.ivLogo.setImageResource(R.drawable.ic_mp_thanku)
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val p = Properties()
        p.putValue("coUserId", coUserId)
        BWSApplication.addToSegment("Thank You Screen Viewed", p, CONSTANTS.screen)
        binding.btnExplore.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            InvoiceActivity.invoiceToDashboard = 1
            val i = Intent(this@ThankYouMpActivity, SleepTimeActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.tvViewInvoice.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            InvoiceActivity.invoiceToRecepit = 0
            val i = Intent(this@ThankYouMpActivity, InvoiceActivity::class.java)
            i.putExtra("ComeFrom", "1")
            startActivity(i)
            finish()
        }
    }
}