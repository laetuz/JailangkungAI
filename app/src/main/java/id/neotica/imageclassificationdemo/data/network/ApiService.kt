package id.neotica.imageclassificationdemo.data.network

import id.neotica.imageclassificationdemo.data.network.KtorConfig.BASE_URL
import id.neotica.domain.model.ImageResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully

object ApiService {
    @OptIn(InternalAPI::class)
    suspend fun postImage(file: ByteArray, contentType: String = "image/jpeg"): ImageResponse {
        return KtorConfig.httpClient.post("$BASE_URL/skin-cancer/predict") {
            body = MultiPartFormDataContent(
                formData {
                    appendInput(
                        "photo",
                        Headers.build {
                            append(HttpHeaders.ContentType, contentType)
                        },
                        file.size.toLong()
                    ) {
                        buildPacket { writeFully(file) }
                    }
                }
            )
        }.body()
    }
    /*@OptIn(InternalAPI::class)
    suspend fun postImage(file: ByteArray): ImageResponse {
        return KtorConfig.httpClient.post("$BASE_URL/skin-cancer/predict") {
            body = MultiPartFormDataContent(
                formData {
                    appendInput(
                        "photo",
                        Headers.build {
                            append(HttpHeaders.ContentType, ContentType.parse("image/jpeg"))
                        },
                        file.size.toLong()
                    ) {
                        buildPacket { writeFully(file) }
                    }
                }
            )
        }.body()
    }*/
}