package id.neotica.asclepius.presentation.history

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.neotica.asclepius.R
import id.neotica.asclepius.databinding.FragmentResultBinding

class HistoryResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding get() = _binding!!
    val args by navArgs<HistoryResultFragmentArgs>()

    private lateinit var toolbar: Toolbar

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
            tvPercentage.text = args.threshold
            resultText.text = args.category

        }
        setupToolbar()
    }

    private fun setupToolbar() {

        toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}