package com.stanley.test.smswatcher

import android.content.Context
import android.content.SharedPreferences

class KeyWordPreference(context: Context) {
    private val KEY = "KeyMap"
    private val keyWordPreference = context.getSharedPreferences("test",Context.MODE_PRIVATE)

    fun addKeyWord( word:String){
        var keySet: HashSet<String>? = keyWordPreference.getStringSet(KEY,null) as HashSet<String>?

        if(keySet == null) {
            keySet = HashSet<String>()
        }
        keySet.add(word)

        val editor = keyWordPreference.edit()
        editor.putStringSet(KEY,keySet)
        editor.apply()
    }

    fun deleteKeyWord(key:String){
        val keySet: HashSet<String>? = keyWordPreference.getStringSet(KEY,null) as HashSet<String>

        if(keySet != null) {
            keySet.remove(key)
            val editor = keyWordPreference.edit()
            editor.putStringSet(KEY,keySet)
            editor.apply()
        }
    }

    fun getKeyWords():HashSet<String>{
        val keySet: HashSet<String>? = keyWordPreference.getStringSet(KEY,null) as HashSet<String>?

        return if(keySet == null){
            HashSet<String>()
        }else{
            keySet
        }
    }

}