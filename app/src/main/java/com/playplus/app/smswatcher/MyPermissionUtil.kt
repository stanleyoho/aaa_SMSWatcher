package com.playplus.app.smswatcher

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object MyPermissionUtil {

    const val PERMISSIION_EXTERNAAL_SMS_READ = Manifest.permission.READ_SMS
    const val PERMISSIION_EXTERNAAL_SMS_SEND = Manifest.permission.SEND_SMS
    const val PERMISSIION_EXTERNAAL_SMS_RECEIVE = Manifest.permission.RECEIVE_SMS

    fun getPermissionArray(vararg permissions : String) : Array<String>{
        val permissionList = ArrayList<String>()
        for(i in 0 until permissions.size){
            permissionList.add(permissions[i])
        }
        return permissionList.toTypedArray()
    }

    fun getPermissionDeniedArray(context: Context, permissionArray : Array<String>) : Array<String>{
        val permissionDeniedList = ArrayList<String>()

        for(i in 0 until  permissionArray.size){
            if(ActivityCompat.checkSelfPermission(context,permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                permissionDeniedList.add(permissionArray[i])
            }
        }
        return permissionDeniedList.toTypedArray()
    }

    fun askPermission(activity: Activity, permissionArray : Array<String>, requestCode : Int){
        ActivityCompat.requestPermissions(activity,permissionArray,requestCode)
    }
}