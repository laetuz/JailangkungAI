package id.neotica.bert.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.neotica.bert.R
import id.neotica.bert.databinding.FragmentQABinding

class QAFragment : Fragment(R.layout.fragment_q_a) {
    private var _binding: FragmentQABinding? = null
    private val binding: FragmentQABinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQABinding.bind(view)

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