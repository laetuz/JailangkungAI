package id.neotica.asclepius.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yalantis.ucrop.UCrop
import id.neotica.asclepius.R
import id.neotica.asclepius.data.ImageClassifierHelper
import id.neotica.asclepius.databinding.FragmentMainBinding
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
          //      showToast("Permission request denied")
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

        getPermission()
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            galleryButton.setOnClickListener { startGallery() }
            analyzeButton.setOnClickListener {
                if (currentImageUri != null) {
                    analyzeImage()
                } else showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    private fun getPermission() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        clearImage()
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            UCrop.of(uri, Uri.fromFile(requireActivity().cacheDir.resolve("${System.currentTimeMillis()}.jpg")))
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(2000, 2000)
                .start(requireActivity(), this)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result is from UCrop
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            showImageFunc(resultUri)
            currentImageUri = resultUri
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast(cropError.toString())
        }
    }

    private fun showImageFunc(uri: Uri?) {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        uri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        currentImageUri?.let {uri ->
            imageClassifierHelper = ImageClassifierHelper(
                context = requireContext(),
                classifierListener = object: ImageClassifierHelper.ClassifierListener {
                    override fun onResults(results: List<Classifications>?) {
                        // binding.progressIndicator.visibility = View.INVISIBLE

                        Log.d("neolog", results.toString())
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val threshold = (it[0].categories[0].score * 100).toInt()
                                val thresholdText = "$threshold %"
                                val category = it[0].categories[0].label
                                moveToResult(uri = uri, thresholdText, category)
                            }
                        }
                    }
                    override fun onError(error: String) {
                        showToast(error)
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(uri)
        }

    }

    private fun setupToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
               //implement
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }

        })
    }

    private fun moveToResult(uri: Uri, resultThreshold: String, resultCategory: String) {
        Log.d("neolog", "move to result")
        val action = MainFragmentDirections.actionMainFragmentToResultFragment(
            uri = uri.toString(),
            threshold = resultThreshold,
            category = resultCategory
        )
        findNavController().navigate(action)
    }

    private fun clearImage() {
        currentImageUri = null
        binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}