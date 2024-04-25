package id.neotica.smartreply.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.neotica.smartreply.R
import id.neotica.smartreply.databinding.FragmentChatMainBinding

class MainChatFragment : Fragment(R.layout.fragment_chat_main) {
    private var _binding: FragmentChatMainBinding? = null
    private val binding: FragmentChatMainBinding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatMainBinding.bind(view)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}