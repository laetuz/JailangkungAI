package id.neotica.asclepius.data.remote

import id.neotica.asclepius.data.remote.response.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(
    private val apiService: ApiService
) {
    suspend fun getNews(): Flow<ApiResult<NewsResponse>> = flow {
        try {
            val response = apiService.getNews()
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}