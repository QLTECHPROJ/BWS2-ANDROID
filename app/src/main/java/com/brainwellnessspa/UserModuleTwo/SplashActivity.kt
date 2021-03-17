package com.brainwellnessspa.UserModuleTwo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, GetStartedActivity::class.java)
            intent.putExtra(CONSTANTS.ScreenVisible, "1")
            startActivity(intent)
            finish()
        }, (2 * 800).toLong())
    }
}