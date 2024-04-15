package id.neotica.imageclassificationdemo.data.network

sealed class ApiResult<T>(
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T) : ApiResult<T>(data)
    class Loading<T>(data: T? = null) : ApiResult<T>(data)
    class Error<T>(errorMessage: String? = null) : ApiResult<T>(null, errorMessage)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorMessage]"
            is Loading<T> -> "Loading"
        }
    }
}