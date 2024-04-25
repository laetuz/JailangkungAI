package id.neotica.bert.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.neotica.bert.R
import id.neotica.bert.databinding.FragmentTopicsBinding

class TopicsFragment : Fragment(R.layout.fragment_topics) {
    private var _binding: FragmentTopicsBinding? = null
    private val binding: FragmentTopicsBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTopicsBinding.bind(view)

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