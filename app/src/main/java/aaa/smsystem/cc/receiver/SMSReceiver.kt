package aaa.smsystem.cc.receiver

import aaa.smsystem.cc.service.MySMSService
import aaa.smsystem.cc.utils.LogUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


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