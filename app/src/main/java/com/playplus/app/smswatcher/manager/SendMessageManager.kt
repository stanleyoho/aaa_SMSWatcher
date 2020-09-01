package com.playplus.app.smswatcher.manager

import android.content.Context
import android.os.Handler
import com.playplus.app.smswatcher.ApiCallBackInterface
import com.playplus.app.smswatcher.DevicePreference
import com.playplus.app.smswatcher.net.NetManager
import com.playplus.app.smswatcher.utils.LogUtils

object SendMessageManager {
    private var messagesMap = HashMap<String,MessageModel>()
    private val sendMessageHandler = Handler()
    private val TAG = javaClass.name
    private lateinit var devicePreference : DevicePreference
    private var isSending = false

    fun initMessage(context: Context){
        this.devicePreference = DevicePreference(context)
    }

    fun addMessage(messageModel: MessageModel){
        if(this.messagesMap.containsKey(messageModel.messageId)){
            LogUtils.i(TAG,"此訊息已存在")
            return
        }
        messagesMap[messageModel.messageId] = messageModel
        //test model 測試用
//        for(i in messageModel.messageId.toInt()+10 .. messageModel.messageId.toInt()+12){
//            messagesMap[i.toString()] = MessageModel(
//                i.toString(),
//                messageModel.sendAddress+i,
//                messageModel.messageContent+i,
//                messageModel.receiveAt+i,
//                false
//            )
//        }
        LogUtils.i(TAG,"收到一封簡訊 :\n$messageModel")

        sendMessage()
    }

    fun updateSendMessage(messageId:String){
        messagesMap[messageId]?.apply {
            this.isSend = true
        }
    }

    fun isAllMessageSend():Boolean{
        var result = true
        for (item in messagesMap.keys){
            val message = messagesMap[item]?.apply {
                if (!this.isSend){
                    result = false
                }
            }
        }
        LogUtils.i(TAG,"簡訊是否都已發出:$result")
        return result
    }

    fun getFirstUnSendMessage():MessageModel?{
        var messageModel : MessageModel? = null
        for (item in messagesMap.keys){
            val message = messagesMap[item]
            message?.let {
                if (!it.isSend){
                    if(messageModel == null){
                        messageModel = it
                        LogUtils.i(TAG,"此未發出簡訊ID:${it.messageId}")
                    }
                }
            }
        }
        return messageModel
    }

    private fun sendMessage(){
        if (isSending) return
        sendMessageHandler.post(sendMessageRunnable)
    }

    private val sendMessageRunnable = Runnable {
        if (!isAllMessageSend()){
            val message = getFirstUnSendMessage()
            LogUtils.i(TAG,"第一封未發出簡訊 : \n$message")
            LogUtils.i(TAG,"發出record api")
            message?.let {
                isSending = true
                NetManager.sendMessage(
                    devicePreference.getToken(),
                    devicePreference.getUID(),
                    devicePreference.getID(),
                    it.sendAddress,
                    it.messageContent,
                    it.receiveAt,
                    it.messageId,
                    object : ApiCallBackInterface{
                        override fun onSuccess(tag: Int, responseString: String?) {
                            updateSendMessage(it.messageId)
                            isSending = false
                            sendMessage()
                        }

                        override fun onFail(tag: Int, errorMessage: String?) {
                            isSending = false
                            LogUtils.d(TAG,"message record: $errorMessage")
                        }
                    }
                )
            }
        }
    }
}

