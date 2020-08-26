package com.playplus.app.smswatcher

import android.app.Application
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SMSWatcherApplication:Application() {

    companion object{
        val instance = SMSWatcherApplication()
    }

    override fun onCreate() {
        super.onCreate()
    }
}