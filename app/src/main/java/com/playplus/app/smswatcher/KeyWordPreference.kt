package com.playplus.app.smswatcher

import android.content.Context

class KeyWordPreference(context: Context) {
    private val KEY = "KeyMap"
    private val keyWordPreference = context.getSharedPreferences("test",Context.MODE_PRIVATE)

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