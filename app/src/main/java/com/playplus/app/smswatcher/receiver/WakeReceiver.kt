package com.playplus.app.smswatcher.receiver

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.playplus.app.smswatcher.utils.LogUtils

class WakeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (GRAY_WAKE_ACTION == action) {
            LogUtils.i(TAG, "wake !! wake !! ")
            val wakeIntent = Intent(context, WakeNotifyService::class.java)
            context.startService(wakeIntent)
        }
    }

    /**
     * 用于其他进程来唤醒UI进程用的Service
     */
    class WakeNotifyService : Service() {
        override fun onCreate() {
            LogUtils.i(TAG, "WakeNotifyService->onCreate")
            super.onCreate()
        }

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            LogUtils.i(TAG, "WakeNotifyService->onStartCommand")
            if (Build.VERSION.SDK_INT < 18) {
                startForeground(
                    WAKE_SERVICE_ID,
                    Notification()
                ) //API < 18 ，此方法能有效隐藏Notification上的图标
            } else {
                val innerIntent = Intent(this, WakeGrayInnerService::class.java)
                startService(innerIntent)
                startForeground(WAKE_SERVICE_ID, Notification())
            }
            return START_STICKY
        }

        override fun onBind(intent: Intent): IBinder? {
            // TODO: Return the communication channel to the service.
            throw UnsupportedOperationException("Not yet implemented")
        }

        override fun onDestroy() {
            LogUtils.i(TAG, "WakeNotifyService->onDestroy")
            super.onDestroy()
        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    class WakeGrayInnerService : Service() {
        override fun onCreate() {
            LogUtils.i(TAG, "InnerService -> onCreate")
            super.onCreate()
        }

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            LogUtils.i(TAG, "InnerService -> onStartCommand")
            startForeground(WAKE_SERVICE_ID, Notification())
            //stopForeground(true);
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            // TODO: Return the communication channel to the service.
            throw UnsupportedOperationException("Not yet implemented")
        }

        override fun onDestroy() {
            LogUtils.i(TAG, "InnerService -> onDestroy")
            super.onDestroy()
        }
    }

    companion object {
        private val TAG = WakeReceiver::class.java.simpleName
        private const val WAKE_SERVICE_ID = -1111

        /**
         * 灰色保活手段唤醒广播的action
         */
        const val GRAY_WAKE_ACTION = "com.wake.gray"
    }
}