package com.playplus.app.smswatcher.aaa.utils

import android.provider.Settings
import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import com.playplus.app.smswatcher.aaa.SMSWatcherApplication
import java.util.*

object MyDeviceUtils {

    fun getManufacturer():String{
        return DeviceUtils.getManufacturer()
    }

    fun getModel():String{
        return DeviceUtils.getModel()
    }

    fun getUniqueDeviceId():String{
        return DeviceUtils.getUniqueDeviceId()
    }

    fun getUUID() : String{
        var uuid = ""
        try{
            uuid = Settings.Secure.getString(SMSWatcherApplication.instance.contentResolver, Settings.Secure.ANDROID_ID)
        }catch (e:Exception){
            uuid = UUID.randomUUID().toString()
        }
        return uuid
    }

    fun logAll(){
        Log.d(javaClass.name,"Manufacturer:"+ getManufacturer())
        Log.d(javaClass.name,"Model:"+ getModel())
        Log.d(javaClass.name,"UniqueDeviceId:"+ getUniqueDeviceId())

    }
}