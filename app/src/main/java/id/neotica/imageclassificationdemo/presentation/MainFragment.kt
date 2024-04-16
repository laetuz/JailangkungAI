package id.neotica.imageclassificationdemo.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
                Toast.makeText(context, "result: ${args.result}", Toast.LENGTH_SHORT).show()
                showImage()
            }
        }
    }

    private fun setupUI() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        showImagePlaceholder()
        with(binding) {
            cameraXButton.setOnClickListener { startCameraX() }
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
            mlButton.setOnClickListener {
                if (currentImageUri != null) {
                    viewModel.analyzeImage(uri = currentImageUri!!, context = requireContext())
                } else showToast("Uri is expected")
            }
            btnTranslate.setOnClickListener {
                var result: String = resultTextView.text.toString()
                viewModel.translateText(result)
                Log.d("neotica", result)
            }
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
                        binding.resultTextView.text = "Error"
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
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCameraX() {
        val action = MainFragmentDirections.actionMainFragmentToCameraFragment()
        findNavController().navigate(action)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
        showImagePlaceholder()
    }

    private fun showImagePlaceholder() {
        with(binding) {
            if (currentImageUri != null) {
                previewImagePlaceholder.visibility = View.GONE
            } else {
                previewImagePlaceholder.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImage() {


        currentImageUri?.let {
            val imageFile = uriToFile(it, requireContext()).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")

            val fileBytes = imageFile.readBytes()
            viewModel.getUploadImage(fileBytes)

            Log.d("neotica", "$fileBytes")

        } ?: showToast(getString(R.string.empty_image_warning))
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