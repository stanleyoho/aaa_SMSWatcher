package com.playplus.app.smswatcher.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.playplus.app.smswatcher.utils.LogUtils
import com.playplus.app.smswatcher.receiver.WakeReceiver


class GrayService : Service() {
    override fun onCreate() {
        LogUtils.i(TAG, "GrayService->onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.i(TAG, "GrayService->onStartCommand")
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, Notification()) //API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            val innerIntent = Intent(this, GrayInnerService::class.java)
            startService(innerIntent)
            startForeground(GRAY_SERVICE_ID, Notification())
        }

        //发送唤醒广播来促使挂掉的UI进程重新启动起来
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val alarmIntent = Intent()
        alarmIntent.action = WakeReceiver.GRAY_WAKE_ACTION
        val operation = PendingIntent.getBroadcast(
            this,
            WAKE_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager!!.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            ALARM_INTERVAL.toLong(),
            operation
        )
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        LogUtils.i(TAG, "GrayService->onDestroy")
        super.onDestroy()
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    class GrayInnerService : Service() {
        override fun onCreate() {
            LogUtils.i(TAG, "InnerService -> onCreate")
            super.onCreate()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            LogUtils.i(TAG, "InnerService -> onStartCommand")
            startForeground(GRAY_SERVICE_ID, Notification())
            //stopForeground(true);
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent?): IBinder {
            // TODO: Return the communication channel to the service.
            throw UnsupportedOperationException("Not yet implemented")
        }

        override fun onDestroy() {
            LogUtils.i(TAG, "InnerService -> onDestroy")
            super.onDestroy()
        }
    }

    companion object {
        private val TAG = GrayService::class.java.simpleName

        /**
         * 定时唤醒的时间间隔，30s
         */
        private const val ALARM_INTERVAL = 1 * 60 * 1000 / 2
        private const val WAKE_REQUEST_CODE = 6666
        private const val GRAY_SERVICE_ID = -1001
    }
}