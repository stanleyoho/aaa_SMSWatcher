package com.playplus.app.smswatcher.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.playplus.app.smswatcher.DevicePreference
import com.playplus.app.smswatcher.manager.MessageModel
import com.playplus.app.smswatcher.manager.SendMessageManager
import com.playplus.app.smswatcher.smsObserverLib.SmsObserver
import com.playplus.app.smswatcher.smsObserverLib.SmsResponseCallback
import com.playplus.app.smswatcher.utils.LogUtils


class MySMSService: Service() ,SmsResponseCallback {

    val TAG = javaClass.name
    private var smsObserver : SmsObserver? = null
    private var devicePreference : DevicePreference? = null
    private var isSMSListening = false

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        devicePreference = DevicePreference(this)
        if(checkDeviceIsRegistered() && !isSMSListening){
            initSMSObserve()
            SendMessageManager.initMessage(this)
            startSMSListener()
            startForegroundNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        if(checkDeviceIsRegistered() && !isSMSListening){
            initSMSObserve()
            SendMessageManager.initMessage(this)
            startSMSListener()
            startForegroundNotification()
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "onBind")
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        stopSMSListener()
        super.onDestroy()
    }

    override fun onCallbackSmsContent(messageId:String,address: String, smsContent: String,createTime:String) {
        Log.i(TAG, "service receive message, address:$address,message:$smsContent")
        SendMessageManager.addMessage(MessageModel(messageId,address,smsContent,createTime,false))
    }

    /**
     * 初始化SMS監聽
     */
    private fun initSMSObserve(){
        if (smsObserver == null){
            smsObserver = SmsObserver(this, this)
        }
    }

    /**
     * 啟用SMS監聽
     */
    private fun startSMSListener(){
        smsObserver?.registerSMSObserver()
        isSMSListening = true
        LogUtils.i(TAG, "開始監聽SMS")
    }

    /**
     * 停止SMS監聽
     */
    private fun stopSMSListener(){
        smsObserver?.unregisterSMSObserver()
        isSMSListening = false
        LogUtils.i(TAG, "停止監聽SMS")
    }

    private fun checkDeviceIsRegistered():Boolean{
        var result = false
        devicePreference?.let {
            val phone = it.getPhone()
            val id = it.getID()
            val token = it.getToken()
            val uid = it.getToken()
            result = phone.isNotEmpty() && id != 0 && token.isNotEmpty() && uid.isNotEmpty()
        }
        if(result){
            LogUtils.i(TAG, "裝置已註冊")
        }else{
            LogUtils.i(TAG, "裝置尚未註冊")
        }
        return result
    }

    private fun startForegroundNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("smsWatch", "smsWatch", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                ?: return
            manager.createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, "xxx")
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setChannelId("smsWatch")
                .build()
            startForeground(1, notification)
        }
    }
}