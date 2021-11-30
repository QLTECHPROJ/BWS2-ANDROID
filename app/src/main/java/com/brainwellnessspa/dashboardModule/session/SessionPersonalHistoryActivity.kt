package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionPersonalHistoryBinding

class SessionPersonalHistoryActivity : AppCompatActivity() {
	lateinit var binding: ActivitySessionPersonalHistoryBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_session_personal_history)

		// mixed qus 2 textBox ,2 radio
	}
}