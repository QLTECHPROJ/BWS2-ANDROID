package com.brainwellnessspa.utility

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {
    private var mythread: MyThread? = null
    var isRunning = false
    val delay: Long = 3000
    override fun onCreate() {
        super.onCreate() ////Log.d("", "onCreate");
        mythread = MyThread()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            mythread!!.start()
            isRunning = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isRunning) {
            mythread!!.interrupt()
            mythread!!.stop()
        }
    }

    internal inner class MyThread : Thread() {
        override fun run() {
            while (isRunning) {
                try { //                    YupITApplication.getToken();
                    sleep(delay)
                } catch (e: InterruptedException) {
                    isRunning = false
                    e.printStackTrace()
                }
            }
        }
    }
}