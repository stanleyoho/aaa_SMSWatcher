package com.playplus.app.smswatcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SMSReceiver : BroadcastReceiver() {

    val TAG = javaClass.name

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let {
            Log.d(TAG,"onReceive")
//            val intent = Intent(it,MySMSService::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            it.startService(intent)

        }
    }
}