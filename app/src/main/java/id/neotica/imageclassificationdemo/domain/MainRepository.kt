package id.neotica.imageclassificationdemo.domain

import id.neotica.imageclassificationdemo.data.network.ApiResult
import id.neotica.imageclassificationdemo.data.response.ImageResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun uploadImage(image:ByteArray): Flow<ApiResult<ImageResponse>>
}