package id.neotica.domain

import id.neotica.domain.model.ImageResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun uploadImage(image: ByteArray): Flow<ApiResult<ImageResponse>>
}