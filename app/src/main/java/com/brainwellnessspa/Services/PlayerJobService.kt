package com.brainwellnessspa.Services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.brainwellnessspa.BWSApplication

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class PlayerJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        val service = Intent(applicationContext, GlobalInitExoPlayer::class.java)
        applicationContext.startService(service)
        try {
            val playbackServiceIntent = Intent(this, GlobalInitExoPlayer::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playbackServiceIntent)
            } else {
                startService(playbackServiceIntent)
            }
            //            bindService(playbackServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        BWSApplication.scheduleJob(applicationContext) // reschedule the job
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        private const val TAG = "SyncService"
    }
}