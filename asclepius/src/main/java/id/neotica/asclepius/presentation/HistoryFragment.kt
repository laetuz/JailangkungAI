package id.neotica.asclepius.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.neotica.asclepius.R
import id.neotica.asclepius.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding get() = _binding!!
//    val args by navArgs<ResultFragmentArgs>()

    private lateinit var toolbar: Toolbar
    private val viewModel: ResultViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        setupUI()
    }

    private fun setupUI() {
        with(binding) {


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