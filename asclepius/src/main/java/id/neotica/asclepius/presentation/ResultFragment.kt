package id.neotica.asclepius.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.neotica.asclepius.R
import id.neotica.asclepius.data.room.AscEntity
import id.neotica.asclepius.databinding.FragmentResultBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding get() = _binding!!
    val args by navArgs<ResultFragmentArgs>()

    private lateinit var toolbar: Toolbar
    private val viewModel: ResultViewModel by viewModel()

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

            lifecycleScope.launch {
                viewModel.addHistory(
                    AscEntity(
                        imageUri = args.uri,
                        category = args.category,
                        percentage = args.threshold
                    )
                )
            }

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