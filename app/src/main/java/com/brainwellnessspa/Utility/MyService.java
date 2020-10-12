package com.brainwellnessspa.Utility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private MyThread mythread;
    public boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ////Log.d("", "onCreate");
        mythread = new MyThread();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            mythread.start();
            isRunning = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isRunning) {
            mythread.interrupt();
            mythread.stop();
        }
    }

    class MyThread extends Thread {
        static final long DELAY = 3000;
        @Override
        public void run() {
            while (isRunning) {
                try {
//                    YupITApplication.getToken();
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        }
    }
}

