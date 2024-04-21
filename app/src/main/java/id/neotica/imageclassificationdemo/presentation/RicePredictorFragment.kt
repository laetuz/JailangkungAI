package id.neotica.imageclassificationdemo.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import id.neotica.imageclassificationdemo.R
import id.neotica.imageclassificationdemo.data.local.PredictionHelper
import id.neotica.imageclassificationdemo.databinding.FragmentRicePredictorBinding

class RicePredictorFragment : Fragment(R.layout.fragment_rice_predictor) {
    private var _binding: FragmentRicePredictorBinding? = null
    private val binding: FragmentRicePredictorBinding get() = _binding!!

    //tfLite
    private lateinit var predictionHelper: PredictionHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRicePredictorBinding.bind(view)

        setupUI()
        predict()
    }

    private fun setupUI() {
        with(binding) {}
    }

    private fun predict() {
        predictionHelper = PredictionHelper(
            context = requireContext(),
            onResult = { result ->
                binding.tvResult.text = result
            },
            onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        )

        binding.btnPredict.setOnClickListener {
            val input = binding.etSales.text.toString()
            predictionHelper.predict(input)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        predictionHelper.close()
        _binding = null
    }
}