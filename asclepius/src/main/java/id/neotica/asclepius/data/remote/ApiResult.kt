package id.neotica.asclepius.data.remote

sealed class ApiResult<out R> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val errorMessage: String) : ApiResult<Nothing>()
    data object Empty : ApiResult<Nothing>()
}