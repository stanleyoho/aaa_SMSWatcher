package com.playplus.app.smswatcher.aaa

import android.app.Application

class SMSWatcherApplication:Application() {

    companion object{
        val instance = SMSWatcherApplication()
    }

    override fun onCreate() {
        super.onCreate()
    }
}