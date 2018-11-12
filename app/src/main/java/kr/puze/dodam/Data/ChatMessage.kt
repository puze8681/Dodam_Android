package kr.puze.dodam.Data

data class ChatMessage(val userAction: String, val messageType: String, val messageOwner: String, val messageContent: String){

    fun userAction(): String {
        return userAction
    }
    fun messageType(): String {
        return messageType
    }
    fun messageOwner(): String {
        return messageOwner
    }
    fun messageContent(): String {
        return userAction
    }
}