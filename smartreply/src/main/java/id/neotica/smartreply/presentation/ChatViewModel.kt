package id.neotica.smartreply.presentation

import androidx.lifecycle.ViewModel
import id.neotica.smartreply.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel: ViewModel() {
    private val _pretendingAsAnotherUser = MutableStateFlow(false)
    val pretendingAsAnotherUser: StateFlow<Boolean> = _pretendingAsAnotherUser

    private val _chatHistory = MutableStateFlow<ArrayList<Message>?>(null)
    val chatHistory: StateFlow<ArrayList<Message>?> = _chatHistory

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun switchUser() {
        val value = _pretendingAsAnotherUser.value
        _pretendingAsAnotherUser.value = !value
    }

    fun setMessages(messages: ArrayList<Message>) {
        _chatHistory.value = messages
    }

    fun addMessages(message: String) {
        val user = _pretendingAsAnotherUser.value

        var list: ArrayList<Message> = chatHistory.value?: ArrayList()

        list.add(Message(message, !user, System.currentTimeMillis()))

        _chatHistory.value = list
    }
}