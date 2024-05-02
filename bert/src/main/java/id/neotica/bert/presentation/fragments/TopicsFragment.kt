package id.neotica.bert.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.neotica.bert.R
import id.neotica.bert.data.DataSetClient
import id.neotica.bert.databinding.FragmentTopicsBinding
import id.neotica.bert.presentation.adapters.TopicsAdapter
import id.neotica.bert.presentation.fragments.TopicsFragmentDirections

class TopicsFragment : Fragment(R.layout.fragment_topics) {
    private var _binding: FragmentTopicsBinding? = null
    private val binding: FragmentTopicsBinding get() = _binding!!

    private var topicsAdapter: TopicsAdapter? = null
    private var topicsTitle = emptyList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTopicsBinding.bind(view)

        setupUI()
        setupRecView()
    }

    private fun setupUI() {
        with(binding) {
            val dataSetClient = DataSetClient(requireActivity())
            dataSetClient.loadJsonData()?.let {
                topicsTitle = it.getTitles()
            }

            topicsAdapter = TopicsAdapter(topicsTitle, object: TopicsAdapter.OnItemSelected {
                override fun onItemClicked(itemID: Int, itemTitle: String) {
                    startQaScreen(itemID, itemTitle)
                }

            })
        }
    }

    private fun setupRecView() {
        with(binding) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            val decoration = DividerItemDecoration(
                rvTopics.context,
                linearLayoutManager.orientation
            )

            with(rvTopics) {
                layoutManager = linearLayoutManager
                adapter = topicsAdapter
                addItemDecoration(decoration)
            }
        }
    }

    private fun startQaScreen(itemId: Int, itemTitle: String) {
        val action = TopicsFragmentDirections.actionTopicsFragmentToQaFragment(itemId, itemTitle)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}