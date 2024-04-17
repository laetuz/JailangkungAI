package id.neotica.imageclassificationdemo.di

import id.neotica.imageclassificationdemo.data.network.KtorConfig.BASE_URL
import id.neotica.imageclassificationdemo.data.network.MainService
import id.neotica.domain.MainRepository
import id.neotica.imageclassificationdemo.repository.MainRepositoryImpl
import id.neotica.imageclassificationdemo.repository.MainRepositoryKtorImpl
import id.neotica.imageclassificationdemo.presentation.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModules = module {
    viewModelOf(::MainViewModel)
}

val repoModules = module {
    singleOf(::MainRepositoryImpl) bind MainRepository::class
    singleOf(::MainRepositoryKtorImpl) bind MainRepository::class
}

val networkModule = module {
    single {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level =
                    HttpLoggingInterceptor.Level.BODY
            }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(MainService::class.java)
    }
}