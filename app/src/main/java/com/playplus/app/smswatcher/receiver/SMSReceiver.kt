package com.playplus.app.smswatcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.playplus.app.smswatcher.service.MySMSService
import com.playplus.app.smswatcher.utils.LogUtils


class SMSReceiver : BroadcastReceiver() {

    val TAG = javaClass.name

    override fun onReceive(context: Context?, intent: Intent) {
        LogUtils.i(TAG, "onReceive : ${intent.action}")
        context?.let{
            startServiceByAndroidVersion(it)
            LogUtils.i(TAG,"start service")
        }
    }

    private fun startServiceByAndroidVersion(context:Context){
        val service = Intent(context, MySMSService::class.java)
        service.putExtra("startType", 1)
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(service)
        } else {
            context.startService(service)
        }
    }
}