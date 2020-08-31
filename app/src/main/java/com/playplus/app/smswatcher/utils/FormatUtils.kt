package com.playplus.app.smswatcher.utils

import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private const val TARGET_TIMEZONE = "GMT+:08:00"
    private const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    private val TAG = javaClass.name

    fun parseToTargetTimeFormat(timeMills: Long):String{
        val deviceDate = Date(timeMills)
        var targetDate: Date? = null
        //舊的就是當前的時區，新的就是目標的時區
        val oldZone = TimeZone.getDefault()
        LogUtils.i(TAG,"device_Zone :$oldZone")
        val newZone = TimeZone.getTimeZone("GMT+8")
        LogUtils.i(TAG,"target_Zone :$newZone")
        val timeOffset = oldZone.rawOffset - newZone.rawOffset
        LogUtils.i(TAG,"timeOffset :$timeOffset")
        targetDate = Date(deviceDate.time - timeOffset)
        return try{
        val dateFormat = SimpleDateFormat(TIME_FORMAT,Locale.getDefault())
        LogUtils.d(TAG, "result : ${dateFormat.format(targetDate)}")
            dateFormat.format(targetDate)
        }catch (e: Exception){
            ""
        }
    }
}