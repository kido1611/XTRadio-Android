package com.xtradio.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RadioService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
