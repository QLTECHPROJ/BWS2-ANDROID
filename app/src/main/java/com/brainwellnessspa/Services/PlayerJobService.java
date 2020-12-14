package com.brainwellnessspa.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.brainwellnessspa.BWSApplication;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PlayerJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), GlobleInItExoPlayer.class);
        getApplicationContext().startService(service);
        try {
            Intent playbackServiceIntent = new Intent(this, GlobleInItExoPlayer.class);
            startService(playbackServiceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playbackServiceIntent);
            }
//            bindService(playbackServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
            e.printStackTrace();
        }
        BWSApplication.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
