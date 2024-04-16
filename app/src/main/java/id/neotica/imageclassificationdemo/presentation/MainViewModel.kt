package id.neotica.imageclassificationdemo.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ImageResponse
import id.neotica.imageclassificationdemo.repository.MainRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepositoryImpl): ViewModel() {

    //Upload Image response
    private val _image: MutableStateFlow<ApiResult<ImageResponse>?> =
        MutableStateFlow(null)
    val image: StateFlow<ApiResult<ImageResponse>?> = _image
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    //MLKit Text Recognition response
    private val _visionText = MutableStateFlow("")
    val visionText = _visionText


    //Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    fun getUploadImage(image: ByteArray) = viewModelScope.launch {
        repo.uploadImage(image)
            .collect {
            _image.value = it
        }
    }

    fun analyzeImage(uri: Uri, context: Context) {
        isLoading.value = true
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(context, uri)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { text: Text ->
                isLoading.value = false
                _visionText.value = text.text
            }
            .addOnFailureListener {
                isLoading.value = false
                _visionText.value = it.message.toString()
            }
    }
}