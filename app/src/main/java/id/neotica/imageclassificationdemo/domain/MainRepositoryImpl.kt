package id.neotica.imageclassificationdemo.domain

import id.neotica.imageclassificationdemo.data.MainService
import id.neotica.imageclassificationdemo.data.network.ApiResult
import id.neotica.imageclassificationdemo.data.response.ImageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await

class MainRepositoryImpl(private val apiService: MainService): MainRepository {
    override fun uploadImage(image: ByteArray): Flow<ApiResult<ImageResponse>> = flow {

        val file = image.toRequestBody("image/jpeg".toMediaType())
        val photo = MultipartBody.Part.createFormData(
            "photo",
            "photo.jpeg",
            file
        )
        emit(ApiResult.Loading())
        try {
            val response = apiService.uploadImage(photo).await()
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Image error"))
        }

    }
}