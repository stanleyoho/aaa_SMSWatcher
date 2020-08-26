package com.playplus.app.smswatcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.playplus.app.smswatcher.KeyWordPreference
import com.playplus.app.smswatcher.smsObserverLib.SmsObserver
import com.playplus.app.smswatcher.smsObserverLib.SmsResponseCallback

class MySMSService: Service() ,SmsResponseCallback {

    val TAG = "SMSService"
    private var smsObserver : SmsObserver? = null
    private var keyWordPreference : KeyWordPreference? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        smsObserver = SmsObserver(this@MySMSService, this)
        keyWordPreference = KeyWordPreference(this)
        smsObserver?.registerSMSObserver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if(smsObserver == null){
            smsObserver = SmsObserver(this@MySMSService, this)
            smsObserver?.registerSMSObserver()
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        smsObserver?.unregisterSMSObserver()
        super.onDestroy()
    }

    override fun onCallbackSmsContent(address: String?, smsContent: String?) {
        Log.d(TAG, "service receive message, address:$address,message:$smsContent")
    }
}