package com.playplus.app.smswatcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.playplus.app.smswatcher.R

class WhiteService : Service() {
    override fun onCreate() {
        Log.i(TAG, "WhiteService->onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "WhiteService->onStartCommand")
        val builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Foreground")
        builder.setContentText("I am a foreground service")
        builder.setContentInfo("Content Info")
        builder.setWhen(System.currentTimeMillis())
        //        Intent activityIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
        val notification = builder.build()
        startForeground(FOREGROUND_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        Log.i(TAG, "WhiteService->onDestroy")
        super.onDestroy()
    }

    companion object {
        private val TAG = WhiteService::class.java.simpleName
        private const val FOREGROUND_ID = 1000
    }
}