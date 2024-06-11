package id.neotica.asclepius.data.remote

import id.neotica.asclepius.data.remote.response.NewsResponse
import retrofit2.http.GET

interface ApiService {
    @GET("top-headlines?country=us&category=health&q=cancer&apiKey=ceb7a9998a0442f496edf6b6165310aa")
    suspend fun getNews(): NewsResponse
}