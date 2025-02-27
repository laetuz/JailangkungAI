package id.neotica.jailangkungai.presentation.camera

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
import id.neotica.jailangkungai.R
import id.neotica.jailangkungai.data.local.ObjectDetectorHelper
import id.neotica.jailangkungai.databinding.FragmentTflCameraBinding
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.text.NumberFormat
import java.util.concurrent.Executors

class ObjectDetectorCameraFragment : Fragment(R.layout.fragment_tfl_camera) {
    private var _binding: FragmentTflCameraBinding? = null
    private val binding: FragmentTflCameraBinding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    //tfLite
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

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

        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            detectorListener = object : ObjectDetectorHelper.DetectorListener {
                override fun onError(error: String) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.tvResult.text = error
                        Log.d("neolog", "lifecycle: $error")
                    }
                }

                override fun onResults(
                    results: MutableList<Detection>?,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int
                ) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.tvInference.text = inferenceTime.toString()

                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val builder = StringBuilder()
                                for (result in results) {
                                    val displayResult = "${result.categories[0].label} " + NumberFormat.getPercentInstance()
                                        .format(result.categories[0].score).trim()
                                    builder.append("$displayResult \n")
                                }

                                binding.overlay.setResults(
                                    results, imageHeight, imageWidth
                                )
                                binding.tvResult.text = builder.toString()
                            } else {
                                binding.overlay.clear()
                                binding.tvResult.text = ""
                            }
                        }

                        binding.overlay.invalidate()
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
                viewLifecycleOwner.lifecycleScope.launch {
                    objectDetectorHelper.detectObject(it)
                }
            }

            imageCapture = ImageCapture.Builder().build()

            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            try {
                Log.d("neolog", "try imageAnalyzer")
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
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
        cameraProvider?.unbindAll()
        _binding = null
    }
}