package id.neotica.asclepius.presentation.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.neotica.asclepius.R
import id.neotica.asclepius.databinding.FragmentHistoryBinding
import id.neotica.asclepius.presentation.history.adapter.HistoryAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding get() = _binding!!
//    val args by navArgs<ResultFragmentArgs>()

    private lateinit var toolbar: Toolbar
    private val viewModel: HistoryViewModel by viewModel()

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
        setupAdapter()
    }

    private fun setupToolbar() {
        toolbar = binding.topAppBar
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        val adapter = HistoryAdapter {
            val action = HistoryFragmentDirections.actionHistoryFragmentToHistoryResultFragment(
                uri = it.imageUri,
                threshold = it.percentage,
                category = it.category
            )
            findNavController().navigate(action)
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
            setHasFixedSize(true)
            lifecycleScope.launch {
                adapter.submitList(viewModel.getHistory())
            }

            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}