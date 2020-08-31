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
            LogUtils.d(TAG,"此訊息已存在")
            return
        }
        messagesMap[messageModel.messageId] = messageModel
        LogUtils.d(TAG,"new message : $messageModel")

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
        LogUtils.d(TAG,"isAllMessageSend : ${isAllMessageSend()}")
        if (!isAllMessageSend()){
            val message = getFirstUnSendMessage()
            LogUtils.d(TAG,"getFirstUnSendMessage : $message")
            message?.let {
                isSending = true
                NetManager.sendMessage(
                    devicePreference.getToken(),
                    devicePreference.getUID(),
                    devicePreference.getID(),
                    it.sendAddress,
                    it.messageContent,
                    it.receiveAt,
                    object : ApiCallBackInterface{
                        override fun onSuccess(tag: Int, responseString: String?) {
                            updateSendMessage(it.messageId)
                            isSending = false
                            sendMessage()
                        }

                        override fun onFail(tag: Int, errorMessage: String?) {
                            isSending = false
                            LogUtils.d(TAG,"send message fail : $errorMessage")
                        }
                    }
                )
            }
        }
    }
}

