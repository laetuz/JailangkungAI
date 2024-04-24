package id.neotica.smartreply.data

data class Message(
    val text: String,
    val isLocalUser: Boolean,
    val timeStamp: Long
)
