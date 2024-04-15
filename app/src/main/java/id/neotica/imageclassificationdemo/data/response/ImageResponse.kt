package id.neotica.imageclassificationdemo.data.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var data: DataResponse = DataResponse()
)

@Serializable
data class DataResponse (
    val id: String? = null,
    val result: String? = null,
    val confidenceScore: Double? = null,
    val isAboveThreshold: String? = null,
    val createdAt: String? = null
)