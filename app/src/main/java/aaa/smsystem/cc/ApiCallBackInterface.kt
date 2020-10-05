package aaa.smsystem.cc

interface ApiCallBackInterface {

    fun onSuccess(tag:Int,responseString:String?)

    fun onFail(tag: Int,errorMessage:String?)
}