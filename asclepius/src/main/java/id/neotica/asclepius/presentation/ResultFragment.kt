package id.neotica.asclepius.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import id.neotica.asclepius.R
import id.neotica.asclepius.databinding.FragmentResultBinding

class ResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding get() = _binding!!
    val args by navArgs<ResultFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResultBinding.bind(view)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            val uri = Uri.parse(args.uri)
            resultImage.setImageURI(uri)
            resultText.text = "${args.category} ${args.threshold}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}