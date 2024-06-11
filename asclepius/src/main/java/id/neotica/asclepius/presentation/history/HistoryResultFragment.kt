package id.neotica.asclepius.presentation.history

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import id.neotica.asclepius.R
import id.neotica.asclepius.data.remote.ApiResult
import id.neotica.asclepius.data.remote.response.Articles
import id.neotica.asclepius.databinding.FragmentResultBinding
import id.neotica.asclepius.presentation.result.adapter.NewsAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding get() = _binding!!
    val args by navArgs<HistoryResultFragmentArgs>()

    private lateinit var toolbar: Toolbar
    private val viewModel: HistoryViewModel by viewModel()

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

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.news.collect {
                    when(it) {
                        is ApiResult.Empty -> {
                            binding.pbMain.visibility = View.VISIBLE
                        }
                        is ApiResult.Success -> {
                            binding.pbMain.visibility = View.GONE
                            val newsList = it.data.articles
                            setupAdapter(newsList)
                        }
                        is ApiResult.Error -> {
                            binding.pbMain.visibility = View.GONE
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.getNews()
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

    private fun setupAdapter(list: ArrayList<Articles>) {
        val adapter = NewsAdapter {
            /*val action = HistoryFragmentDirections.actionHistoryFragmentToHistoryResultFragment(
                uri = it.title,
                threshold = it.percentage,
                category = it.category
            )
            findNavController().navigate(action)*/
        }
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
            setHasFixedSize(true)
            lifecycleScope.launch {
                adapter.submitList(list)
            }

            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}