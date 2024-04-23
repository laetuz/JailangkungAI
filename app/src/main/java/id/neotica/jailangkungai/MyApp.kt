package id.neotica.jailangkungai

import android.app.Application
import com.google.android.material.color.DynamicColors
import id.neotica.jailangkungai.di.networkModule
import id.neotica.jailangkungai.di.repoModules
import id.neotica.jailangkungai.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            modules(
                appModules
            )
        }
    }
}

val appModules = listOf(
    viewModelModules,
    networkModule,
    repoModules
)