package com.stanley.test.smswatcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log


class MySMSReceiver:BroadcastReceiver() {

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {
            val action: String? = it.action ?: ""
            if (action == "android.provider.Telephony.SMS_RECEIVED") {
                //获取短信内容,有可能一次发来多条短信
                Log.i("wk", "短信内容==>" + " 来自==>")
            }
        }
    }
}