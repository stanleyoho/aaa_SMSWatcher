package aaa.smsystem.cc.manager

data class MessageModel (
    val messageId :String,
    val sendAddress : String,
    val messageContent:String,
    val receiveAt:String,
    var isSend : Boolean
)