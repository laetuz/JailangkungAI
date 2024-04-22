package id.neotica.imageclassificationdemo.presentation.mediapipe

import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mediapipe.tasks.components.containers.Classifications
import id.neotica.imageclassificationdemo.R
import id.neotica.imageclassificationdemo.data.local.ImageClassifierHelperMediaPipe
import id.neotica.imageclassificationdemo.databinding.FragmentTflCameraBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.concurrent.Executors

class MediaPipeCameraFragment : Fragment(R.layout.fragment_tfl_camera) {
    private var _binding: FragmentTflCameraBinding? = null
    private val binding: FragmentTflCameraBinding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    //tfLite
    private lateinit var imageClassifierHelper: ImageClassifierHelperMediaPipe

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTflCameraBinding.bind(view)

        setupUiTfLite()
        tfLiteCamera()
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    private fun setupUiTfLite() {
        with(binding) {
            switchCamera.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                tfLiteCamera()
            }
        }
    }

    private fun tfLiteCamera() {
        Log.d("neolog", "tfliteCam")

        imageClassifierHelper = ImageClassifierHelperMediaPipe(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelperMediaPipe.ClassifierListener {
                override fun onError(error: String) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.tvResult.text = error
                        Log.d("neolog", "lifecycle: $error")
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {

                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.tvInference.text = inferenceTime.toString()

                        results?.let {
                            if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                val sortedCategories =
                                    it[0].categories().sortedByDescending { it?.score() }
                                val displayResult =
                                    sortedCategories.joinToString("\n") { category ->
                                        "${category.categoryName()} " + NumberFormat.getPercentInstance()
                                            .format(category.score()).trim()
                                    }
                                binding.tvResult.text = displayResult
                            } else {
                                binding.tvResult.text = ""
                            }
                        }
                    }
                }
            }
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            Log.d("neolog", "cameraProvider")
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) {
                imageClassifierHelper.classifyImage(it)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            try {
                Log.d("neolog", "try imageAnalyzer")
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer,
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d("neolog", e.message.toString())
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}