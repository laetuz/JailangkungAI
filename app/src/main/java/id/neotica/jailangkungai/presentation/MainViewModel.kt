package id.neotica.jailangkungai.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ImageResponse
import id.neotica.jailangkungai.repository.MainRepositoryImpl
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

    //MLKit Translate
    private val _translation = MutableStateFlow("")
    val translation = _translation

    fun getUploadImage(image: ByteArray) = viewModelScope.launch {
        repo.uploadImage(image)
            .collect {
            _image.value = it
        }
    }

    fun analyzeImage(uri: Uri, context: Context) {
        _isLoading.value = true
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(context, uri)
        textRecognizer.process(inputImage)
            .addOnSuccessListener { text: Text ->
                _isLoading.value = false
                _visionText.value = text.text
            }
            .addOnFailureListener {
                _isLoading.value = false
                _visionText.value = it.message.toString()
            }
    }

    fun translateText(detectedText: String) {
        _isLoading.value = true
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val idToEn = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        idToEn.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                idToEn.translate(detectedText)
                    .addOnSuccessListener { translatedText ->
                        isLoading.value = false

                        _translation.value = translatedText.toString()
                        idToEn.close()
                    }
            }
            .addOnFailureListener { exception ->
                isLoading.value = false

                _translation.value = exception.message.toString()
            }
    }
}