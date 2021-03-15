package com.brainwellnessspa.UserModuleTwo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.brainwellnessspa.LoginModule.Activities.LoginActivity
import com.brainwellnessspa.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, GetStartedActivity::class.java)
            startActivity(intent)
            finish()
        }, (2 * 800).toLong())
    }
}