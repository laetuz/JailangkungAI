package id.neotica.mediapipe.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mediapipe.tasks.components.containers.Classifications
import id.neotica.mediapipe.R
import id.neotica.mediapipe.data.AudioClassifierHelper
import id.neotica.mediapipe.databinding.FragmentAudioBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat

class AudioFragment : Fragment(R.layout.fragment_audio) {
    private var _binding: FragmentAudioBinding? = null
    private val binding: FragmentAudioBinding get() = _binding!!
    private var isRecording = false

    //mediapipe
    private lateinit var audioClassifierHelper: AudioClassifierHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAudioBinding.bind(view)

        initializeAudioClassifierHelper()
        setClickListener()
    }

    private fun initializeAudioClassifierHelper() {
        with(binding) {
            audioClassifierHelper = AudioClassifierHelper(
                context = requireContext(),
                classifierListener = object : AudioClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResults(results: List<Classifications>, inferenceTime: Long) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            results.let {
                                if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                    val sortedCategories =
                                        it[0].categories().sortedByDescending { it?.score() }
                                    val displayResult = sortedCategories.joinToString("\n") {
                                        "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                            .format(it.score()).trim()
                                    }
                                    tvResult.text = displayResult
                                } else { tvResult.text = "" }
                            }
                        }
                    }

                }
            )
        }
    }

    private fun setClickListener() {
        with(binding) {
            btnStart.setOnClickListener {
                audioClassifierHelper.startAudioClassification()
                isRecording = true
                updateButtonStates()
            }
            btnStop.setOnClickListener {
                audioClassifierHelper.stopAudio()
                isRecording = false
                updateButtonStates()
            }
        }
    }

    private fun updateButtonStates() {
        with(binding) {
            btnStart.isEnabled = !isRecording
            btnStop.isEnabled = isRecording
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}