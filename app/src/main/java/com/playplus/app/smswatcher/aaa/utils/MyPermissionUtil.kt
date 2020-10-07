package com.playplus.app.smswatcher.aaa.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object MyPermissionUtil {

    fun getPermissionArray(vararg permissions : String) : Array<String>{
        val permissionList = ArrayList<String>()
        for(element in permissions){
            permissionList.add(element)
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