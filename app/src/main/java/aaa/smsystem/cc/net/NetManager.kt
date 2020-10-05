package aaa.smsystem.cc.net

import aaa.smsystem.cc.ApiCallBackInterface
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody

object NetManager {

    fun getToken(apiCallback: ApiCallBackInterface){

        val request = Request.Builder()
            .url(UrlManager.API_GET_TOKEN)
            .post(RequestBody.create(null, ByteArray(0)))
            .build()
        MySSLSocketClient
        OkHttpUtils.getInstance().post(1001, UrlManager.API_GET_TOKEN,request,apiCallback)
    }

    fun sendMessage(token:String,uid:String,id:Int,address:String,message:String,time:String,clientMessageId:String,apiCallback: ApiCallBackInterface){
        val requestBody = FormBody.Builder()
            .add("sms_record[content]",message)
            .add("sms_record[phone]",address)
            .add("sms_record[created_at]",time)
            .add("sms_record[client_message_id]",clientMessageId)
            .build()

        val request = Request.Builder()
            .url(UrlManager.API_SEND_MESSAGE)
            .addHeader("Authorization","Token token=$token, uid=$uid, id=$id")
            .post(requestBody)
            .build()

        OkHttpUtils.getInstance().post(1001, UrlManager.API_SEND_MESSAGE,request,apiCallback)
    }

    fun registerDevice(token:String,device:String,uid:String,phone:String,apiCallback: ApiCallBackInterface){
        val requestBody = FormBody.Builder()
            .add("device[name]",device)
            .add("device[uid]",uid)
            .add("device[phone]",phone)
            .build()

        val request = Request.Builder()
            .url(UrlManager.API_REGISTER_DEVICE)
            .addHeader("Authorization","Token token=$token")
            .addHeader("Content-Type","application/x-www-form-urlencoded")
            .post(requestBody)
            .build()

        OkHttpUtils.getInstance().post(1001, UrlManager.API_REGISTER_DEVICE,request,apiCallback)
    }
}