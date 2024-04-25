package id.neotica.smartreply.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import id.neotica.smartreply.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatViewModel: ViewModel() {
    private val anotherUserID = "101"

    private val _pretendingAsAnotherUser = MutableStateFlow(false)
    val pretendingAsAnotherUser: StateFlow<Boolean> = _pretendingAsAnotherUser

    private val _chatHistory = MutableStateFlow<ArrayList<Message>?>(null)
    val chatHistory: StateFlow<ArrayList<Message>?> = _chatHistory

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val smartReply: SmartReplyGenerator = SmartReply.getClient()

    private val _smartReplyOptions = MutableStateFlow<List<SmartReplySuggestion>>(emptyList())
    val smartReplyOptions: StateFlow<List<SmartReplySuggestion>> = _smartReplyOptions

    init {
        initSmartReplyOptionsGenerator()
    }

    private fun initSmartReplyOptionsGenerator() {
        combine(
            chatHistory,
            pretendingAsAnotherUser
        ) { conversations, isPretendingAsAnotherUser ->
            Pair(conversations, isPretendingAsAnotherUser)
        }.onEach { (conversations, isPretendingAsAnotherUser) ->
            if (!conversations.isNullOrEmpty()) {
                generateSmartReplyOptions(conversations, isPretendingAsAnotherUser)
                    .addOnSuccessListener { result ->
                        _smartReplyOptions.value = result
                    }
            }
        }.launchIn(viewModelScope)
    }

    fun switchUser() {
        val value = _pretendingAsAnotherUser.value
        _pretendingAsAnotherUser.value = !value
    }

    fun setMessages(messages: ArrayList<Message>) {
        _chatHistory.value = messages
    }

    fun addMessages(message: String) {
        val user = _pretendingAsAnotherUser.value

        val list: ArrayList<Message> = chatHistory.value?: ArrayList()

        list.add(Message(message, !user, System.currentTimeMillis()))

        clearSmartReplyOptions()
        _chatHistory.value = list
    }

    private fun generateSmartReplyOptions(
        messages: List<Message>,
        isPretendingAsAnotherUser: Boolean
    ): Task<List<SmartReplySuggestion>> {
        val lastMessage = messages.last()

        if (lastMessage.isLocalUser != isPretendingAsAnotherUser) {
            return Tasks.forException(Exception("Can't run Smart Reply!"))
        }

        val chatConversations = ArrayList<TextMessage>()
        for (message in messages) {
            if (message.isLocalUser != isPretendingAsAnotherUser) {
                chatConversations.add(TextMessage.createForLocalUser(message.text, message.timeStamp))
            } else {
                chatConversations.add(TextMessage.createForRemoteUser(message.text, message.timeStamp, anotherUserID))
            }
        }

        return smartReply
            .suggestReplies(chatConversations)
            .continueWith {
                val result = it.result
                when (result.status) {
                    SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                        _errorMessage.value = "Unable to generate options due to a non-english."
                    }
                    SmartReplySuggestionResult.STATUS_NO_REPLY -> {
                        _errorMessage.value = "Unable to generation options due to no appropriate response found."
                    }
                }
                result.suggestions
            }
            .addOnFailureListener{
                _errorMessage.value = "An error has accured on Smart Reply Instance"
            }
    }

    private fun clearSmartReplyOptions() {
        _smartReplyOptions.value = ArrayList()
    }

    override fun onCleared() {
        super.onCleared()
        smartReply.close()
    }
}