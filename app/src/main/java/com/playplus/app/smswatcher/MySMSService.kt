package com.playplus.app.smswatcher

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.playplus.app.smswatcher.smsObserverLib.SmsHandler
import com.playplus.app.smswatcher.smsObserverLib.SmsObserver
import com.playplus.app.smswatcher.smsObserverLib.SmsResponseCallback

class MySMSService: Service() , SmsResponseCallback {

    val TAG = "SMSService"
    private var smsObserver : SmsObserver? = null
    private val smsHandler = SmsHandler(object : SmsResponseCallback{
        override fun onCallbackSmsContent(smsContent: String?) {
            Log.d(TAG,smsContent?:"")
        }
    })

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
        smsObserver = SmsObserver(smsHandler)
        smsObserver?.registerSMSObserver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand")
        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG,"onBind")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        smsObserver?.unregisterSMSObserver()
        super.onDestroy()
    }

    override fun onCallbackSmsContent(smsContent: String?) {
        Log.d(TAG,smsContent?:"")
    }
}