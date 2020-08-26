package com.playplus.app.smswatcher.utils

import android.annotation.SuppressLint
import android.util.Log
import com.blankj.utilcode.util.PhoneUtils

object MyPhoneUtils {

    @SuppressLint("MissingPermission")
    fun getIMEI():String{
        return PhoneUtils.getIMEI()
    }

    @SuppressLint("MissingPermission")
    fun getMEID():String{
        return PhoneUtils.getMEID()
    }

    @SuppressLint("MissingPermission")
    fun getIMSI():String{
        return PhoneUtils.getIMSI()
    }

    @SuppressLint("MissingPermission")
    fun getDeviceId():String{
        return PhoneUtils.getDeviceId()
    }

    @SuppressLint("MissingPermission")
    fun getSerial():String{
        return PhoneUtils.getSerial()
    }

    fun logAll(){
        Log.d(javaClass.name,"IMEI:"+ getIMEI())
        Log.d(javaClass.name,"MEID:"+ getMEID())
        Log.d(javaClass.name,"IMSI:"+ getIMSI())
        Log.d(javaClass.name,"DeviceId:"+ getDeviceId())
        Log.d(javaClass.name,"Serial:"+ getSerial())
    }
}