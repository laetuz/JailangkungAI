package id.neotica.mediapipe.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.neotica.mediapipe.R
import id.neotica.mediapipe.databinding.FragmentMediapipeBinding

class MediaPipeFragment : Fragment(R.layout.fragment_mediapipe) {
    private var _binding: FragmentMediapipeBinding? = null
    private val binding: FragmentMediapipeBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediapipeBinding.bind(view)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            btnCamera.setOnClickListener {
                findNavController().navigate(MediaPipeFragmentDirections.actionMediaPipeFragmentToMediaPipeCameraFragment())
            }
            btnAudio.setOnClickListener {
                findNavController().navigate(MediaPipeFragmentDirections.actionMediaPipeFragmentToAudioFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}