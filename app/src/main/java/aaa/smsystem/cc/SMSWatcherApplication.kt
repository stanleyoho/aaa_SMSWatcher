package aaa.smsystem.cc

import android.app.Application

class SMSWatcherApplication:Application() {

    companion object{
        val instance = SMSWatcherApplication()
    }

    override fun onCreate() {
        super.onCreate()
    }
}