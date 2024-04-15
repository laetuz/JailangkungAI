package id.neotica.imageclassificationdemo.data

import id.neotica.imageclassificationdemo.data.response.ImageResponse
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