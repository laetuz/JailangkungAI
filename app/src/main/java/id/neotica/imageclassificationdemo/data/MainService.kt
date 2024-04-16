package id.neotica.imageclassificationdemo.data

import id.neotica.domain.model.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MainService {
    @Multipart
    @POST("skin-cancer/predict")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<ImageResponse>
}