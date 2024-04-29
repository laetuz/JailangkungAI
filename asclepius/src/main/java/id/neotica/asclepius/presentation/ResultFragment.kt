package id.neotica.asclepius.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.neotica.asclepius.R
import id.neotica.asclepius.databinding.FragmentResultBinding

class ResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResultBinding.bind(view)

        setupUI()

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
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