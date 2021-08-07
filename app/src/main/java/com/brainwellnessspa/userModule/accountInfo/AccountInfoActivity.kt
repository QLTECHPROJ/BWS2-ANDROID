package com.brainwellnessspa.userModule.accountInfo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.databinding.ActivityAccountInfoBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class AccountInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountInfoBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var isPinSet: String? = ""
    var isMainAccount: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_info)
        val shared1: SharedPreferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        isPinSet = shared1.getString(CONSTANTS.PREFE_ACCESS_isPinSet, "")
        isMainAccount = shared1.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "")
        binding.llBack.setOnClickListener {
            finish()
        }

        val p = Properties()
        addToSegment("Account Info Screen Viewed", p, CONSTANTS.screen)

        if (isPinSet.equals("1", ignoreCase = true)) {
            binding.llChangePin.visibility = View.VISIBLE
        } else {
            binding.llChangePin.visibility = View.GONE
        }


        binding.llEtProfile.setOnClickListener {
            if (isNetworkConnected(this)) {
                val i = Intent(this, EditProfileActivity::class.java)
                startActivity(i)
                finish()
            } else {
                showToast(getString(R.string.no_server_found), this)
            }
        }

        binding.llDeleteAc.setOnClickListener {
            if (isNetworkConnected(this)) {
                val i = Intent(this, CancelMembershipActivity::class.java)
                i.putExtra("screenView", "0")
                startActivity(i)
                finish()
            } else {
                showToast(getString(R.string.no_server_found), this)
            }
        }

        binding.llChangePin.setOnClickListener {
            if (isNetworkConnected(this)) {
                val i = Intent(this, ChangePinActivity::class.java)
                startActivity(i)
                finish()
            } else {
                showToast(getString(R.string.no_server_found), this)
            }
        }
    }
}