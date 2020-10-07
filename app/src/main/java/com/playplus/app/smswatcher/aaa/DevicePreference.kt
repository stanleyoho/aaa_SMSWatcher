package com.playplus.app.smswatcher.aaa

import android.content.Context
import android.content.SharedPreferences

class DevicePreference(context: Context) {
    private val KEY = "KeyMap"
    private val PREFERENCE_KEY = "SMS_WATCHER"
    private val PREFERENCE_KEY_TOKEN = "TOKEN"
    private val PREFERENCE_KEY_ID = "ID"
    private val PREFERENCE_KEY_UID = "UID"
    private val PREFERENCE_KEY_PHONE = "PHONE"
    private val keyWordPreference : SharedPreferences = context.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE)

    fun setToken(token:String){
        val editor = keyWordPreference.edit()
        editor.putString(PREFERENCE_KEY_TOKEN,token)
        editor.apply()
    }

    fun getToken():String{
        return keyWordPreference.getString(PREFERENCE_KEY_TOKEN,"")?:""
    }

    fun setID(id:Int){
        val editor = keyWordPreference.edit()
        editor.putInt(PREFERENCE_KEY_ID,id)
        editor.apply()
    }

    fun getID():Int{
        return keyWordPreference.getInt(PREFERENCE_KEY_ID,0)
    }

    fun setUID(uid:String){
        val editor = keyWordPreference.edit()
        editor.putString(PREFERENCE_KEY_UID,uid)
        editor.apply()
    }

    fun getUID():String{
        return keyWordPreference.getString(PREFERENCE_KEY_UID,"")?:""
    }

    fun setPhone(phone:String){
        val editor = keyWordPreference.edit()
        editor.putString(PREFERENCE_KEY_PHONE,phone)
        editor.apply()
    }

    fun getPhone():String{
        return keyWordPreference.getString(PREFERENCE_KEY_PHONE,"")?:""
    }

    fun addKeyWord( word:String){
        val editor = keyWordPreference.edit()
        val keySet: HashSet<String> = keyWordPreference.getStringSet(KEY,HashSet<String>()) as HashSet<String>

        val tempHashSet = HashSet<String>()
        val iterator = keySet.iterator()

        while(iterator.hasNext()){
            tempHashSet.add(iterator.next())
        }
        tempHashSet.add(word)
        editor.putStringSet(KEY,tempHashSet)
        editor.apply()
    }

    fun deleteKeyWord(key:String){
        val editor = keyWordPreference.edit()
        val keySet: HashSet<String> = keyWordPreference.getStringSet(KEY,HashSet<String>()) as HashSet<String>

        val tempHashSet = HashSet<String>()
        val iterator = keySet.iterator()

        while(iterator.hasNext()){
            tempHashSet.add(iterator.next())
        }

        tempHashSet.remove(key)
        editor.putStringSet(KEY,tempHashSet)
        editor.apply()
    }

    fun getKeyWords():HashSet<String> {
        return keyWordPreference.getStringSet(KEY, HashSet<String>()) as HashSet<String>
    }
}