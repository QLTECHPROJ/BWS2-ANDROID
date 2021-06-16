package com.brainwellnessspa.Services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class LocalService : Service() {
    // Binder given to clients
    val binder: IBinder = LocalBinder()

    // Random number generator
    val mGenerator = Random()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        val service: LocalService
            get() =// Return this instance of LocalService so clients can call public methods
                this@LocalService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /** method for clients  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)
}