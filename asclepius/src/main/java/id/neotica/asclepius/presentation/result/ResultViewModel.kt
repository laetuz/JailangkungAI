package id.neotica.asclepius.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.asclepius.data.remote.ApiResult
import id.neotica.asclepius.data.remote.RemoteDataSource
import id.neotica.asclepius.data.remote.response.NewsResponse
import id.neotica.asclepius.data.room.AscEntity
import id.neotica.asclepius.domain.AscDaoInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultViewModel(
    private val dao: AscDaoInteractor,
    private val remoteData: RemoteDataSource
): ViewModel() {
    private val _news: MutableStateFlow<ApiResult<NewsResponse>> = MutableStateFlow(ApiResult.Empty)
    val news: MutableStateFlow<ApiResult<NewsResponse>> = _news

    suspend fun addHistory(item: AscEntity) {
        withContext(Dispatchers.IO) {
            dao.addHistory(item)
        }
    }

    fun getNews() {
        viewModelScope.launch {
            remoteData.getNews()
                .onStart { _news.value = ApiResult.Empty }
                .collect { _news.value = it }
        }
    }
}