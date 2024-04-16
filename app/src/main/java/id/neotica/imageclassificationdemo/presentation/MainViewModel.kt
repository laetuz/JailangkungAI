package id.neotica.imageclassificationdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ImageResponse
import id.neotica.imageclassificationdemo.repository.MainRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepositoryImpl): ViewModel() {
    private val _image: MutableStateFlow<ApiResult<ImageResponse>?> =
        MutableStateFlow(null)

    val image: StateFlow<ApiResult<ImageResponse>?> = _image
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getUploadImage(image: ByteArray) = viewModelScope.launch {
        repo.uploadImage(image)
            .collect {
            _image.value = it
        }
    }
}