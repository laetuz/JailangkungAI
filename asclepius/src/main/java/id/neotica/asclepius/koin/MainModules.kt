package id.neotica.asclepius.koin

import androidx.room.Room
import id.neotica.asclepius.data.remote.ApiService
import id.neotica.asclepius.data.remote.RemoteDataSource
import id.neotica.asclepius.data.room.AscDatabase
import id.neotica.asclepius.domain.AscDaoInteractor
import id.neotica.asclepius.domain.AscDaoRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import id.neotica.asclepius.presentation.result.ResultViewModel
import id.neotica.asclepius.presentation.history.HistoryViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    single {get<AscDatabase>().dao()}
    single {
        Room.databaseBuilder(androidContext(), AscDatabase::class.java, "asc.db").build()
    }
}

val viewModule = module {
    single {  }
    viewModelOf(::ResultViewModel)
    viewModelOf(::HistoryViewModel)
    singleOf(::AscDaoInteractor)
    singleOf(::AscDaoRepoImpl)
    singleOf(::RemoteDataSource)
}

val networkModule = module {
    single {
        val logBody = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logBody)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            //.certificatePinner(certificatePinner)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}