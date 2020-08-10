package com.playplus.app.smswatcher

import android.util.Log
import com.blankj.utilcode.util.DeviceUtils

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

    fun logAll(){
        Log.d(javaClass.name,"Manufacturer:"+ getManufacturer())
        Log.d(javaClass.name,"Model:"+ getModel())
        Log.d(javaClass.name,"UniqueDeviceId:"+ getUniqueDeviceId())

    }
}