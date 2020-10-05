package aaa.smsystem.cc.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import com.blankj.utilcode.util.PhoneUtils

object MyPhoneUtils {

    val TAG = javaClass.name

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

    @SuppressLint("MissingPermission")
    fun logPhoneInfo(context: Context){
        //test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val sm: SubscriptionManager = SubscriptionManager.from(context)
            val sis: List<SubscriptionInfo> = sm.activeSubscriptionInfoList
            if (sis.size >= 1) {
                val si1: SubscriptionInfo = sis[0]
                val iccId1: String = si1.iccId
                val phoneNum1: String? = si1.number
                LogUtils.d(TAG, "id1:$iccId1,num2:$phoneNum1")
            }
            if (sis.size >= 2) {
                val si2: SubscriptionInfo = sis[1]
                val iccId2: String = si2.iccId
                val phoneNum2: String? = si2.number
                LogUtils.d(TAG, "id1:$iccId2,num2:$phoneNum2")
            }
            // 获取SIM卡数量相关信息：
            val count: Int = sm.activeSubscriptionInfoCount //当前实际插卡数量
            val max: Int = sm.activeSubscriptionInfoCountMax //当前卡槽数量
            LogUtils.d(TAG, "当前实际插卡数量:$count,当前卡槽数量:$max")
        }
    }
}