package com.playplus.app.smswatcher.aaa

interface ApiCallBackInterface {

    fun onSuccess(tag:Int,responseString:String?)

    fun onFail(tag: Int,errorMessage:String?)
}