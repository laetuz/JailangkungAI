package id.neotica.smartreply.presentation

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.neotica.smartreply.R
import id.neotica.smartreply.data.Message
import id.neotica.jailangkungai.R as MainR
import id.neotica.smartreply.databinding.FragmentChatBinding
import id.neotica.smartreply.viewModelModules
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class SmartReplyFragment : Fragment(R.layout.fragment_chat) {
    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!

    private var chatAdapter = ChatHistoryAdapter()

    private var replyOptionsAdapter = ReplyOptionsAdapter(object: ReplyOptionsAdapter.OnItemClickCallback {
        override fun onOptionClicked(optionText: String) {
            binding.tietInputTextEditText.setText(optionText)
        }
    })

    init {
        loadKoinModules(viewModelModules)
    }

    private val viewModel: ChatViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBinding.bind(view)

        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar as Toolbar)

        setupUI()
        setupMenu()
        observeViewModel()
    }

    private fun setupUI() {
        with(binding) {

            rvChatHistory.layoutManager = LinearLayoutManager(context)
            rvChatHistory.adapter = chatAdapter

            val optionsLayoutManager = LinearLayoutManager(context)
            optionsLayoutManager.orientation = RecyclerView.HORIZONTAL

            rvSmartReplyOptions.layoutManager = optionsLayoutManager
            rvSmartReplyOptions.adapter = replyOptionsAdapter

            btnSwitchUser.setOnClickListener {
                chatAdapter.pretendingAsAnotherUser = !chatAdapter.pretendingAsAnotherUser
                viewModel.switchUser()
            }

            btnSend.setOnClickListener {
                val input = tietInputTextEditText.text.toString()
                if (input.isNotEmpty()) {
                    viewModel.addMessages(input)
                    tietInputTextEditText.text?.clear()
                }
                observeViewModel()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatHistory.collect {
                if (it != null) {
                    Log.d("neolog", it.toString())
                    chatAdapter.setChatHistory(it)
                    if (chatAdapter.itemCount > 0) binding.rvChatHistory.smoothScrollToPosition(chatAdapter.itemCount -1)
                } else {
                    Log.d("neolog", "null")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pretendingAsAnotherUser.collect {
                if (it) {
                    binding.tvCurrentUser.text = requireContext().getText(R.string.chatting_as_evans)
                    binding.tvCurrentUser.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            androidx.appcompat.R.color.background_floating_material_dark
                        )
                    )
                } else {
                    binding.tvCurrentUser.text = requireContext().getText(R.string.chatting_as_kai)
                    binding.tvCurrentUser.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            androidx.constraintlayout.widget.R.color.background_floating_material_dark
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.smartReplyOptions.collect() {
                replyOptionsAdapter.setReplyOptions(it)
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(MainR.menu.chat_menu_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    MainR.id.generateBasicChatHistory -> {
                        generateBasicChatHistory()
                        true
                    }

                    MainR.id.generateSensitiveChatHistory -> {
                        generateSensitiveChatHistory()
                        true
                    }

                    MainR.id.clearChatHistory -> {
                        viewModel.setMessages(ArrayList())
                        true
                    }

                    else -> {false}
                }
            }
        })
    }

    private fun generateBasicChatHistory() {
        val chatHistory = ArrayList<Message>()
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MINUTE, -10)
        chatHistory.add(
            Message(
                text = "Hello",
                isLocalUser = true,
                timeStamp = calendar.timeInMillis
            )
        )

        calendar.add(Calendar.MINUTE, 10)
        chatHistory.add(
            Message(
                text = "Hey",
                isLocalUser = false,
                timeStamp = calendar.timeInMillis
            )
        )

        viewModel.setMessages(chatHistory)
    }

    private fun generateSensitiveChatHistory() {
        val chatHistory = ArrayList<Message>()
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MINUTE, -10)
        chatHistory.add(Message("Hi", false, calendar.timeInMillis))

        calendar.add(Calendar.MINUTE, 1)
        chatHistory.add(Message("How are you?", true, calendar.timeInMillis))

        calendar.add(Calendar.MINUTE, 10)
        chatHistory.add(Message("My cat died", false, calendar.timeInMillis))

        viewModel.setMessages(chatHistory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}