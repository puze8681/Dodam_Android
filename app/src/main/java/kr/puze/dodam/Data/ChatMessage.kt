package kr.puze.dodam.Data

data class ChatMessage(val type: String, val messageType: String, val messageContent: String, val messageOwner: String){

    fun type(): String{
        return type
    }

    fun messageType(): String {
        return messageType
    }
    fun messageOwner(): String {
        return messageOwner
    }
    fun messageContent(): String {
        return messageContent
    }
}