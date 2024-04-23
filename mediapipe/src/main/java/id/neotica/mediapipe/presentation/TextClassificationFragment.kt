package id.neotica.mediapipe.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mediapipe.tasks.components.containers.Classifications
import id.neotica.mediapipe.R
import id.neotica.mediapipe.data.TextClassifierHelper
import id.neotica.mediapipe.databinding.FragmentClassifyTextBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat

class TextClassificationFragment : Fragment(R.layout.fragment_classify_text) {
    private var _binding: FragmentClassifyTextBinding? = null
    private val binding: FragmentClassifyTextBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClassifyTextBinding.bind(view)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            val textClassifierHelper = TextClassifierHelper(
                context = requireContext(),
                classifierListener =  object : TextClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            results?.let {
                                if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                    val sortedCategories = it[0].categories().sortedByDescending { it?.score() }
                                    val displayResult = sortedCategories.joinToString("\n") {
                                        "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                            .format(it.score()).trim()
                                    }
                                    tvResult.text = displayResult
                                } else {
                                    tvResult.text = ""
                                }
                            }
                        }
                    }
                }
            )

            btnClassify.setOnClickListener {
                val textInput = etClassify.text.toString()
                textClassifierHelper.classify(textInput)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}