package id.neotica.domain.model

data class ImageResponse(
    var message: String? = null,
    var data: DataResponse = DataResponse()
)

data class DataResponse (
    val id: String? = null,
    val result: String? = null,
    val confidenceScore: Double? = null,
    val isAboveThreshold: String? = null,
    val createdAt: String? = null
)