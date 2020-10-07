package com.playplus.app.smswatcher.aaa.utils

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
        val newZone = TimeZone.getTimeZone("GMT+8")
        val timeOffset = oldZone.rawOffset - newZone.rawOffset
        targetDate = Date(deviceDate.time - timeOffset)
        return try{
        val dateFormat = SimpleDateFormat(TIME_FORMAT,Locale.getDefault())
            dateFormat.format(targetDate)
        }catch (e: Exception){
            ""
        }
    }
}