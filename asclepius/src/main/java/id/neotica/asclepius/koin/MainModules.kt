package id.neotica.asclepius.koin

import androidx.room.Room
import id.neotica.asclepius.data.room.AscDatabase
import id.neotica.asclepius.domain.AscDaoInteractor
import id.neotica.asclepius.domain.AscDaoRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import id.neotica.asclepius.presentation.ResultViewModel
import org.koin.core.module.dsl.singleOf

val databaseModule = module {
    single {get<AscDatabase>().dao()}
    single {
        Room.databaseBuilder(androidContext(), AscDatabase::class.java, "asc.db").build()
    }
}

val viewModule = module {
    viewModelOf(::ResultViewModel)
    singleOf(::AscDaoInteractor)
    singleOf(::AscDaoRepoImpl)
}