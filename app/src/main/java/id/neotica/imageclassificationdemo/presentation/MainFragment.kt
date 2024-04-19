package id.neotica.imageclassificationdemo.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.neotica.imageclassificationdemo.R
import id.neotica.domain.ApiResult
import id.neotica.imageclassificationdemo.databinding.FragmentMainBinding
import id.neotica.imageclassificationdemo.getImageUri
import id.neotica.imageclassificationdemo.reduceFileImage
import id.neotica.imageclassificationdemo.repeatCollectionOnCreated
import id.neotica.imageclassificationdemo.uriToFile
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        setupUI()
        observeViewModel()
        repeatCollectionOnCreated {
            val args = MainFragmentArgs.fromBundle(arguments as Bundle)
            if (args.result != "Null") {
                currentImageUri = args.result?.toUri()
                showImage()
            } else {
                clearImage()
            }
        }
    }

    private fun setupUI() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        with(binding) {
            cameraXButton.setOnClickListener { startCameraX("normal") }
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
            mlButton.setOnClickListener {
                uriChecker {
                    viewModel.analyzeImage(uri = currentImageUri!!, context = requireContext())
                }
            }
            btnTranslate.setOnClickListener {
                uriChecker {
                    val result: String = resultTextView.text.toString()
                    viewModel.translateText(result)
                    Log.d("neotica", result)
                }
            }
            btnQr.setOnClickListener { startCameraX("scan") }
            btnTflite.setOnClickListener { startTFLiteCamera() }
            btnTfliteGms.setOnClickListener { startTfLiteGMSCamera() }
            btnObjectDetector.setOnClickListener { startObjectDetectorCamera() }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.image.collect() {
                when (it) {
                    is ApiResult.Loading -> showLoading(true)
                    is ApiResult.Success -> {
                        showLoading(false)
                        binding.resultTextView.text = it.data?.data?.result
                    }

                    is ApiResult.Error -> {
                        showLoading(false)
                        binding.resultTextView.text = getString(R.string.error)
                    }

                    null -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.visionText.collect() {
                binding.resultTextView.text = it
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                if (it) {
                    binding.progressIndicator.visibility = View.VISIBLE
                } else binding.progressIndicator.visibility = View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.translation.collect {
                binding.resultTextView.text = it
            }
        }
    }

    private fun startGallery() {
        clearImage()
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
        Log.d("neolog", "uri: $currentImageUri")
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            Log.d("neolog", "ifSuccess: $currentImageUri")
        }
    }

    private fun startCameraX(type: String) {
        clearImage()
        val action = MainFragmentDirections.actionMainFragmentToCameraFragment(type)
        findNavController().navigate(action)
    }

    private fun startTFLiteCamera() {
        val action = MainFragmentDirections.actionMainFragmentToTFLiteCameraFragment()
        findNavController().navigate(action)
    }

    private fun startTfLiteGMSCamera() {
        val action = MainFragmentDirections.actionMainFragmentToTFLiteGmsCameraFragment()
        findNavController().navigate(action)
    }

    private fun startObjectDetectorCamera() {
        val action = MainFragmentDirections.actionMainFragmentToObjectDetectorCameraFragment()
        findNavController().navigate(action)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun clearImage() {
        currentImageUri = null
        binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
    }

    private fun uploadImage() {
        uriChecker {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, requireContext())
                val reducedImageFile = if (Build.VERSION.SDK_INT >= VERSION_CODES.Q) {
                    imageFile.reduceFileImage()
                } else {
                    imageFile
                }
                Log.d("Image Classification File", "showImage: ${reducedImageFile.path}")

                val fileBytes = reducedImageFile.readBytes()
                viewModel.getUploadImage(fileBytes)

                Log.d("neotica", "$fileBytes")
            }
        }
    }

    private fun uriChecker(func: () -> Unit) {
        if (currentImageUri == null) {
            showToast("Uri is expected")
        } else {
            func()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}